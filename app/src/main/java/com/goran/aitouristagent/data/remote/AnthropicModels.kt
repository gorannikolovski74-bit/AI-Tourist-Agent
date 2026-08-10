package com.goran.aitouristagent.data.remote

import kotlinx.serialization.Serializable

// Minimal slice of the Anthropic Messages API needed by the chat feature.
// The server (/api/v1/chat) forwards this body to Anthropic as-is.

@Serializable
data class ImageSource(
    val type: String = "base64",
    val media_type: String,
    val data: String,
)

@Serializable
data class ContentBlock(
    val type: String,
    val text: String? = null,
    val source: ImageSource? = null,
)

@Serializable
data class AnthropicMessage(
    val role: String,
    val content: List<ContentBlock>,
)

@Serializable
data class ChatRequest(
    val model: String = "claude-sonnet-4-5",
    val max_tokens: Int = 1024,
    val system: String,
    val messages: List<AnthropicMessage>,
)

@Serializable
data class ChatResponse(
    val content: List<ContentBlock> = emptyList(),
    val error: AnthropicError? = null,
)

@Serializable
data class AnthropicError(
    val type: String? = null,
    val message: String? = null,
)
