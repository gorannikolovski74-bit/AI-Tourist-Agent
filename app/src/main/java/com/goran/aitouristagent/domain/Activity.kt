package com.goran.aitouristagent.domain

data class Activity(
    val id: String,
    val dayId: String,
    val text: String,
    val price: Double?,
    val mapsUrl: String?,
    val orderIndex: Int,
    val updatedAt: Long,
)
