package com.goran.aitouristagent.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class TripDto(
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
