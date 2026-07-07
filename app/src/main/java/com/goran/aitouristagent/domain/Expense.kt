package com.goran.aitouristagent.domain

data class Expense(
    val id: String,
    val tripId: String,
    val date: String,
    val category: String,
    val label: String,
    val amount: Double,
    val updatedAt: Long,
)
