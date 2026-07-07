package com.goran.aitouristagent.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses WHERE tripId = :tripId AND deleted = 0 ORDER BY date DESC, updatedAt DESC")
    fun observeExpenses(tripId: String): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(expense: ExpenseEntity)

    @Query("UPDATE expenses SET deleted = 1, updatedAt = :updatedAt WHERE id = :expenseId")
    suspend fun markDeleted(expenseId: String, updatedAt: Long)
}
