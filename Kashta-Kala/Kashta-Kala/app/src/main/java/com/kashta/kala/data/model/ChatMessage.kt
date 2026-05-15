package com.kashta.kala.data.model

/**
 * Represents a single chat message in the AI assistant conversation.
 */
data class ChatMessage(
    val id: String = "",
    val role: String = "user",          // "user" or "assistant"
    val content: String = ""
)
