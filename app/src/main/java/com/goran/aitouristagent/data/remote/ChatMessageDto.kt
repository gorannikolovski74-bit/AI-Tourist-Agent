package com.goran.aitouristagent.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageDto(
    val id: String,
    val tripId: String,
    val role: String,
    val content: String,
    val hasImage: Int = 0,
    val createdAt: Long,
)
