package com.goran.aitouristagent.data.repository

import android.util.Log
import com.goran.aitouristagent.data.local.ChatMessageDao
import com.goran.aitouristagent.data.local.ChatMessageEntity
import com.goran.aitouristagent.data.local.toDomain
import com.goran.aitouristagent.data.remote.AnthropicMessage
import com.goran.aitouristagent.data.remote.ApiService
import com.goran.aitouristagent.data.remote.ChatMessageDto
import com.goran.aitouristagent.data.remote.ChatMessagePostDto
import com.goran.aitouristagent.data.remote.ChatRequest
import com.goran.aitouristagent.data.remote.ContentBlock
import com.goran.aitouristagent.data.remote.ImageSource
import com.goran.aitouristagent.domain.Activity
import com.goran.aitouristagent.domain.BudgetItem
import com.goran.aitouristagent.domain.ChatMessage
import com.goran.aitouristagent.domain.Day
import com.goran.aitouristagent.domain.Expense
import com.goran.aitouristagent.domain.Trip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "ChatRepository"
private const val HISTORY_LIMIT = 20

@Singleton
class ChatRepository @Inject constructor(
    private val chatMessageDao: ChatMessageDao,
    private val tripRepository: TripRepository,
    private val itineraryRepository: ItineraryRepository,
    private val budgetRepository: BudgetRepository,
    private val apiService: ApiService,
) {
    fun observeMessages(tripId: String): Flow<List<ChatMessage>> =
        chatMessageDao.observeMessages(tripId).map { entities -> entities.map { it.toDomain() } }

    /** Pulls the text-only history from the server; local messages are kept (insert-or-ignore). */
    suspend fun refreshFromServer(tripId: String) {
        val remote = runCatching { apiService.getChatHistory(tripId) }
            .onFailure { Log.w(TAG, "refreshFromServer: fetch failed", it) }
            .getOrNull() ?: return

        for (dto in remote) {
            chatMessageDao.insert(dto.toEntity())
        }
    }

    suspend fun sendMessage(tripId: String, text: String, imageBase64: String?): Result<String> {
        val trip = tripRepository.getTrip(tripId)
        val days = itineraryRepository.observeDays(tripId).first()
        val activitiesByDay = days.associateWith { day -> itineraryRepository.observeActivities(day.id).first() }
        val budgetItems = budgetRepository.observeBudgetItems(tripId).first()
        val expenses = budgetRepository.observeExpenses(tripId).first()

        val systemPrompt = buildSystemPrompt(trip, days, activitiesByDay, budgetItems, expenses)

        val history = chatMessageDao.recentMessages(tripId, HISTORY_LIMIT).reversed().map { entity ->
            AnthropicMessage(role = entity.role, content = listOf(ContentBlock(type = "text", text = entity.content)))
        }

        val userContentBlocks = buildList {
            if (imageBase64 != null) {
                add(ContentBlock(type = "image", source = ImageSource(media_type = "image/jpeg", data = imageBase64)))
            }
            add(ContentBlock(type = "text", text = text))
        }
        val request = ChatRequest(
            system = systemPrompt,
            messages = history + AnthropicMessage(role = "user", content = userContentBlocks),
        )

        val response = runCatching { apiService.chat(request) }.getOrElse { return Result.failure(it) }
        if (response.error != null) {
            return Result.failure(IOException(response.error.message ?: "Anthropic API error"))
        }
        val assistantText = response.content.firstOrNull { it.type == "text" }?.text.orEmpty()

        val now = System.currentTimeMillis()
        val userPlaceholder = if (imageBase64 != null) "📷 $text".trim() else text
        persistMessage(tripId, "user", userPlaceholder, hasImage = imageBase64 != null, createdAt = now)
        persistMessage(tripId, "assistant", assistantText, hasImage = false, createdAt = now + 1)

        return Result.success(assistantText)
    }

    private suspend fun persistMessage(tripId: String, role: String, content: String, hasImage: Boolean, createdAt: Long) {
        chatMessageDao.insert(
            ChatMessageEntity(
                id = UUID.randomUUID().toString(),
                tripId = tripId,
                role = role,
                content = content,
                hasImage = hasImage,
                createdAt = createdAt,
            ),
        )
        runCatching {
            apiService.postChatMessage(tripId, ChatMessagePostDto(role = role, content = content, hasImage = if (hasImage) 1 else 0))
        }.onFailure { Log.w(TAG, "persistMessage: server sync failed, kept locally", it) }
    }

    private fun buildSystemPrompt(
        trip: Trip?,
        days: List<Day>,
        activitiesByDay: Map<Day, List<Activity>>,
        budgetItems: List<BudgetItem>,
        expenses: List<Expense>,
    ): String {
        if (trip == null) return "Ти си AI Tourist Agent, помошник за патување. Одговарај на јазикот на корисникот."

        val plannedTotal = budgetItems.sumOf { it.planned }
        val spentTotal = expenses.sumOf { it.amount }
        val itineraryText = days.sortedBy { it.orderIndex }.joinToString("\n") { day ->
            val activityText = activitiesByDay[day].orEmpty().sortedBy { it.orderIndex }.joinToString("; ") { it.text }
            "${day.date} (${day.title}): $activityText"
        }

        return buildString {
            appendLine("Ти си AI Tourist Agent — помошник за патувањето „${trip.name}\" во ${trip.destination}.")
            appendLine("Датуми: ${trip.startDate} до ${trip.endDate}, ${trip.travelers} патници, валута ${trip.currency}.")
            appendLine(
                "Вкупен буџет: %.2f %s, планирано: %.2f %s, потрошено досега: %.2f %s."
                    .format(trip.budgetTotal, trip.currency, plannedTotal, trip.currency, spentTotal, trip.currency),
            )
            if (itineraryText.isNotBlank()) {
                appendLine("Итинерар:")
                appendLine(itineraryText)
            }
            append("Одговарај на јазикот на корисникот (стандардно македонски), кратко и практично. ")
            append("Ако добиеш слика, помогни со превод или толкување на она што е на неа.")
        }
    }
}

private fun ChatMessageDto.toEntity() = ChatMessageEntity(
    id = id,
    tripId = tripId,
    role = role,
    content = content,
    hasImage = hasImage != 0,
    createdAt = createdAt,
)
