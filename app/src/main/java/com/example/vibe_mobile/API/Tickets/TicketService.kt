package com.example.vibe_mobile.API.Tickets

import com.example.vibe_mobile.Clases.ReserveTicket
import com.example.vibe_mobile.Clases.TicketResponse
import retrofit2.Response
import retrofit2.http.*

interface TicketService {

    @POST("api/tickets")
    suspend fun reserveTicket(@Body ticket: ReserveTicket): Response<TicketResponse>
}