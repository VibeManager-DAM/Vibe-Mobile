package com.example.vibe_mobile.Clases

data class Ticket(
    val id: Int,
    val date: String,
    val time: String,
    val num_row: Int,
    val num_col: Int,
    val eventInfo: EventInfo,
    val id_user: Int
)
