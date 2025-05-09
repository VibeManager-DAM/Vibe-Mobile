package com.example.vibe_mobile.Clases

data class UserTicketsResponse(
    val id: Int,
    val fullname: String,
    val email: String,
    val tickets: List<Ticket>
)