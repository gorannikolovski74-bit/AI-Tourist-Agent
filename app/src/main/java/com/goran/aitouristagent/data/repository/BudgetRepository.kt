package com.goran.aitouristagent.data.repository

import com.goran.aitouristagent.data.local.BudgetItemDao
import com.goran.aitouristagent.data.local.BudgetItemEntity
import com.goran.aitouristagent.data.local.ExpenseDao
import com.goran.aitouristagent.data.local.ExpenseEntity
import com.goran.aitouristagent.data.local.toDomain
import com.goran.aitouristagent.domain.BudgetItem
import com.goran.aitouristagent.domain.Expense
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

// Budget items and expenses are Room-only for now — the backend doesn't expose
// /api/v1/trips/:id/budget or /expenses yet (see ANDROID_APP_ARCHITECTURE.md §3.2).
@Singleton
class BudgetRepository @Inject constructor(
    private val budgetItemDao: BudgetItemDao,
    private val expenseDao: ExpenseDao,
) {
    fun observeBudgetItems(tripId: String): Flow<List<BudgetItem>> =
        budgetItemDao.observeBudgetItems(tripId).map { entities -> entities.map { it.toDomain() } }

    fun observeExpenses(tripId: String): Flow<List<Expense>> =
        expenseDao.observeExpenses(tripId).map { entities -> entities.map { it.toDomain() } }

    suspend fun addBudgetItem(tripId: String, label: String, emoji: String, planned: Double) {
        budgetItemDao.upsert(
            BudgetItemEntity(
                id = UUID.randomUUID().toString(),
                tripId = tripId,
                label = label,
                emoji = emoji,
                planned = planned,
                updatedAt = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun deleteBudgetItem(itemId: String) {
        budgetItemDao.markDeleted(itemId, System.currentTimeMillis())
    }

    suspend fun addExpense(tripId: String, date: String, category: String, label: String, amount: Double) {
        expenseDao.upsert(
            ExpenseEntity(
                id = UUID.randomUUID().toString(),
                tripId = tripId,
                date = date,
                category = category,
                label = label,
                amount = amount,
                updatedAt = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun deleteExpense(expenseId: String) {
        expenseDao.markDeleted(expenseId, System.currentTimeMillis())
    }
}
