package com.example.vibe_mobile.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibe_mobile.API.Users.UserRepository
import com.example.vibe_mobile.Clases.Ticket
import com.example.vibe_mobile.Clases.TicketItem

import kotlinx.coroutines.launch

class TicketViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _tickets = MutableLiveData<List<TicketItem>>()
    val tickets: LiveData<List<TicketItem>> = _tickets

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchUserTickets(userId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getUserTickets(userId)
                if (response.isSuccessful && response.body() != null) {
                    val userData = response.body()!!
                    val ticketItems = userData.tickets.map { ticket ->
                        TicketItem(
                            id = ticket.id,
                            date = ticket.date,
                            time = ticket.time,
                            num_row = ticket.num_row,
                            num_col = ticket.num_col,
                            title = ticket.eventInfo.title,
                            image = ticket.eventInfo.image
                        )
                    }
                    _tickets.value = ticketItems
                } else {
                    _error.value = "Error ${response.code()}: No se pudieron cargar los tickets"
                }
            } catch (e: Exception) {
                _error.value = "Excepción: ${e.message}"
            }
        }
    }
}
