package com.example.vibe_mobile.Clases

import java.time.LocalDate
import java.time.LocalTime

data class TicketItem(
    val id: Int,
    val title: String,       // Título del evento
    val date: LocalDate,
    val time: LocalTime,
    val numCol: Int?,
    val numRow: Int?,
    val imageResId: Int      // Si usás un recurso local. Si viene de la DB, puede ser String (URL)
)