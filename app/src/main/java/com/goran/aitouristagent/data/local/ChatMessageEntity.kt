package com.goran.aitouristagent.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.goran.aitouristagent.domain.ChatMessage

// Append-only, text-only — image bytes are never persisted (§3.7 lesson
// learned: base64 in chat history breaks the next API call).
@Entity(
    tableName = "chat_messages",
    indices = [Index("tripId")],
)
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val tripId: String,
    val role: String,
    val content: String,
    val hasImage: Boolean,
    val createdAt: Long,
)

fun ChatMessageEntity.toDomain() = ChatMessage(
    id = id,
    tripId = tripId,
    role = role,
    content = content,
    hasImage = hasImage,
    createdAt = createdAt,
)
