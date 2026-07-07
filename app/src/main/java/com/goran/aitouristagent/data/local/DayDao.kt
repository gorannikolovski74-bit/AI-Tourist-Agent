package com.goran.aitouristagent.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DayDao {
    @Query("SELECT * FROM days WHERE tripId = :tripId AND deleted = 0 ORDER BY orderIndex ASC")
    fun observeDays(tripId: String): Flow<List<DayEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(day: DayEntity)

    @Query("SELECT COALESCE(MAX(orderIndex), -1) FROM days WHERE tripId = :tripId AND deleted = 0")
    suspend fun maxOrderIndex(tripId: String): Int

    @Query("UPDATE days SET deleted = 1, updatedAt = :updatedAt WHERE id = :dayId")
    suspend fun markDeleted(dayId: String, updatedAt: Long)
}
