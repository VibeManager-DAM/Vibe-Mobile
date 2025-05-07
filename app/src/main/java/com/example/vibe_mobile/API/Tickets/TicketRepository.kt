package com.example.vibe_mobile.API.Tickets
import com.example.vibe_mobile.API.RetrofitClient
import com.example.vibe_mobile.API.Users.UserService
import com.example.vibe_mobile.Clases.Ticket
import com.example.vibe_mobile.Clases.User
import retrofit2.Response

class TicketRepository {

    private val ticketService: TicketService by lazy {
        RetrofitClient.createService(TicketService::class.java)
    }

    suspend fun createTicket(ticket: Ticket): Response<Ticket> {
        return ticketService.createTicket(ticket)
    }
}