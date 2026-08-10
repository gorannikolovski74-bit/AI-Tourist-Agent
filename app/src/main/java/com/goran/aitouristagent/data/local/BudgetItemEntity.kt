package com.goran.aitouristagent.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.goran.aitouristagent.domain.BudgetItem

@Entity(
    tableName = "budget_items",
    indices = [Index("tripId")],
)
data class BudgetItemEntity(
    @PrimaryKey val id: String,
    val tripId: String,
    val label: String,
    val emoji: String,
    val planned: Double,
    val updatedAt: Long,
    val deleted: Boolean = false,
)

fun BudgetItemEntity.toDomain() = BudgetItem(
    id = id,
    tripId = tripId,
    label = label,
    emoji = emoji,
    planned = planned,
    updatedAt = updatedAt,
)
