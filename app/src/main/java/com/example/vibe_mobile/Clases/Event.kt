package com.example.vibe_mobile.Clases

data class Event(
    val id: Int,
    val title: String,
    val description: String?,
    val date: String,       // formato "yyyy-MM-dd"
    val time: String,       // formato "HH:mm:ss"
    val image: String?,
    val capacity: Int,
    val seats: Boolean,
    val price: Double,
    val num_rows: Int?,
    val num_columns: Int?,
    val id_organizer: Int
)
