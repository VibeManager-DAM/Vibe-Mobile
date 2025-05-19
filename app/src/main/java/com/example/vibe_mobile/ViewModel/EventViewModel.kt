package com.example.vibe_mobile.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibe_mobile.API.Events.EventRepository
import com.example.vibe_mobile.Clases.Event
import com.example.vibe_mobile.Clases.EventCreate
import kotlinx.coroutines.launch

class  EventViewModel : ViewModel() {
    private val repository = EventRepository()

    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> = _events

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchEvents() {
        viewModelScope.launch {
            try {
                val response = repository.getAllEvents()
                if (response.isSuccessful) {
                    _events.value = response.body()
                } else {
                    _error.value = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Excepción: ${e.message}"
            }
        }
    }

    fun fetchEventsOrganizer(userID: Int?) {
        viewModelScope.launch {
            try {
                val response = repository.getEventsOrganizer(userID)
                if (response.isSuccessful) {
                    _events.value = response.body()
                } else {
                    _error.value = "Error al cargar los eventos del organizador"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            }
        }
    }

    fun createEvent(event: EventCreate, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.createEvent(event)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Error ${response.code()}: no se pudo crear el evento")
                }
            } catch (e: Exception) {
                onError("Error: ${e.message}")
            }
        }
    }

}
