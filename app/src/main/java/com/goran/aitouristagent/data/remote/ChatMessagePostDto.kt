package com.goran.aitouristagent.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessagePostDto(
    val role: String,
    val content: String,
    val hasImage: Int = 0,
)
