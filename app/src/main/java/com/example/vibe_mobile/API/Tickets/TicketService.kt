package com.example.vibe_mobile.API.Tickets
import com.example.vibe_mobile.Clases.Ticket
import retrofit2.Response
import retrofit2.http.*

interface TicketService {

    @POST("tickets")
    suspend fun createTicket(@Body ticket: Ticket): Response<Ticket>
}