package com.example.vibe_mobile.API.Events

import com.example.vibe_mobile.API.RetrofitClient
import com.example.vibe_mobile.Clases.Event
import com.example.vibe_mobile.Clases.Space

import retrofit2.Response

class EventRepository {
    private val eventService: EventService by lazy {
        RetrofitClient.createService(EventService::class.java)
    }

    suspend fun getAllEvents(): Response<List<Event>> {
        return eventService.getEvents()
    }

    suspend fun getEventById(id: Int): Response<Event>{
        return eventService.getEventById(id)
    }

    suspend fun getSpaceByEvent(id: Int): Response<Space>{
        return eventService.getEventSpace(id)
    }

}