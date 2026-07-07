package com.goran.aitouristagent.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.goran.aitouristagent.domain.Expense

@Entity(
    tableName = "expenses",
    indices = [Index("tripId")],
)
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val tripId: String,
    val date: String,
    val category: String,
    val label: String,
    val amount: Double,
    val updatedAt: Long,
    val deleted: Boolean = false,
)

fun ExpenseEntity.toDomain() = Expense(
    id = id,
    tripId = tripId,
    date = date,
    category = category,
    label = label,
    amount = amount,
    updatedAt = updatedAt,
)
