package com.goran.aitouristagent.domain

data class Trip(
    val id: String,
    val name: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val travelers: Int,
    val currency: String,
    val budgetTotal: Double,
    val updatedAt: Long,
)
