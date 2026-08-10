package com.goran.aitouristagent.data.repository

import com.goran.aitouristagent.data.local.ActivityDao
import com.goran.aitouristagent.data.local.ActivityEntity
import com.goran.aitouristagent.data.local.DayDao
import com.goran.aitouristagent.data.local.DayEntity
import com.goran.aitouristagent.data.local.toDomain
import com.goran.aitouristagent.domain.Activity
import com.goran.aitouristagent.domain.Day
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

// Days/activities are Room-only for now — the backend doesn't expose
// /api/v1/trips/:id/days yet (see ANDROID_APP_ARCHITECTURE.md §3.2).
@Singleton
class ItineraryRepository @Inject constructor(
    private val dayDao: DayDao,
    private val activityDao: ActivityDao,
) {
    fun observeDays(tripId: String): Flow<List<Day>> =
        dayDao.observeDays(tripId).map { entities -> entities.map { it.toDomain() } }

    fun observeActivities(dayId: String): Flow<List<Activity>> =
        activityDao.observeActivities(dayId).map { entities -> entities.map { it.toDomain() } }

    suspend fun addDay(tripId: String, date: String, title: String, subtitle: String) {
        val orderIndex = dayDao.maxOrderIndex(tripId) + 1
        val day = DayEntity(
            id = UUID.randomUUID().toString(),
            tripId = tripId,
            date = date,
            title = title,
            subtitle = subtitle,
            orderIndex = orderIndex,
            updatedAt = System.currentTimeMillis(),
        )
        dayDao.upsert(day)
    }

    suspend fun deleteDay(dayId: String) {
        dayDao.markDeleted(dayId, System.currentTimeMillis())
    }

    suspend fun addActivity(dayId: String, text: String, price: Double?, mapsUrl: String?) {
        val orderIndex = activityDao.maxOrderIndex(dayId) + 1
        val activity = ActivityEntity(
            id = UUID.randomUUID().toString(),
            dayId = dayId,
            text = text,
            price = price,
            mapsUrl = mapsUrl,
            orderIndex = orderIndex,
            updatedAt = System.currentTimeMillis(),
        )
        activityDao.upsert(activity)
    }

    suspend fun deleteActivity(activityId: String) {
        activityDao.markDeleted(activityId, System.currentTimeMillis())
    }
}
