package com.goran.aitouristagent.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE tripId = :tripId ORDER BY createdAt ASC")
    fun observeMessages(tripId: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE tripId = :tripId ORDER BY createdAt DESC LIMIT :limit")
    suspend fun recentMessages(tripId: String, limit: Int): List<ChatMessageEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(message: ChatMessageEntity)
}
