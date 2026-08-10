package com.goran.aitouristagent.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities WHERE dayId = :dayId AND deleted = 0 ORDER BY orderIndex ASC")
    fun observeActivities(dayId: String): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE dayId IN (:dayIds) AND deleted = 0 ORDER BY orderIndex ASC")
    fun observeActivitiesForDays(dayIds: List<String>): Flow<List<ActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(activity: ActivityEntity)

    @Query("SELECT COALESCE(MAX(orderIndex), -1) FROM activities WHERE dayId = :dayId AND deleted = 0")
    suspend fun maxOrderIndex(dayId: String): Int

    @Query("UPDATE activities SET deleted = 1, updatedAt = :updatedAt WHERE id = :activityId")
    suspend fun markDeleted(activityId: String, updatedAt: Long)
}
