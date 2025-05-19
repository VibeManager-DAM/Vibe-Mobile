package com.example.vibe_mobile.API.Tickets

import com.example.vibe_mobile.API.RetrofitClient
import com.example.vibe_mobile.Clases.ReserveTicket
import com.example.vibe_mobile.Clases.TicketResponse
import retrofit2.Response

class TicketRepository {

    private val ticketService: TicketService by lazy {
        RetrofitClient.createService(TicketService::class.java)
    }

    suspend fun reserveTicket(ticket: ReserveTicket): Response<TicketResponse> {
        return ticketService.reserveTicket(ticket)
    }
}