package com.example.vibe_mobile.Clases

data class Message(
    val id: Int,
    val context: String,
    val send_at: String,     // formato "yyyy-MM-dd'T'HH:mm:ss"
    val sender_id: Int,
    val id_chat: Int
)