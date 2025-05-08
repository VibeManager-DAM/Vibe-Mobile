package com.example.vibe_mobile.Clases

data class TicketItem(
    val id: Int,
    val date: String,
    val time: String,
    val num_row: Int,
    val num_col: Int,
    val title: String,
    val image: String
)