package com.example.demo.model

import java.time.LocalDateTime

data class ChatMessage(
    val id: String,
    val text: String,
    val senderId: String,
    val senderName: String,
    val timestamp: String,
    val isFromMe: Boolean = false
)