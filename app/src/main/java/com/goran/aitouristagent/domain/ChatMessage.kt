package com.goran.aitouristagent.domain

data class ChatMessage(
    val id: String,
    val tripId: String,
    val role: String,
    val content: String,
    val hasImage: Boolean,
    val createdAt: Long,
)
