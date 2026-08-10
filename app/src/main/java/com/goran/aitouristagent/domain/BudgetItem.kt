package com.goran.aitouristagent.domain

data class BudgetItem(
    val id: String,
    val tripId: String,
    val label: String,
    val emoji: String,
    val planned: Double,
    val updatedAt: Long,
)
