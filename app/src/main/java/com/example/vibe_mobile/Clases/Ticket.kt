package com.example.vibe_mobile.Clases

data class Ticket(
    val id: Int,
    val date: String,       // formato "yyyy-MM-dd"
    val time: String,       // formato "HH:mm:ss"
    val num_col: Int?,
    val num_row: Int?,
    val id_event: Int,
    val id_user: Int
)
