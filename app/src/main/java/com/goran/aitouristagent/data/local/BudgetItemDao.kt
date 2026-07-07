package com.goran.aitouristagent.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetItemDao {
    @Query("SELECT * FROM budget_items WHERE tripId = :tripId AND deleted = 0 ORDER BY updatedAt ASC")
    fun observeBudgetItems(tripId: String): Flow<List<BudgetItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: BudgetItemEntity)

    @Query("UPDATE budget_items SET deleted = 1, updatedAt = :updatedAt WHERE id = :itemId")
    suspend fun markDeleted(itemId: String, updatedAt: Long)
}
