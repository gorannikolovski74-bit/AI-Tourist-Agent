package com.goran.aitouristagent.domain

data class Day(
    val id: String,
    val tripId: String,
    val date: String,
    val title: String,
    val subtitle: String,
    val orderIndex: Int,
    val updatedAt: Long,
)
