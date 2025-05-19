package com.example.vibe_mobile.API.Events
import com.example.vibe_mobile.Clases.CreateEventResponse
import com.example.vibe_mobile.Clases.Event
import com.example.vibe_mobile.Clases.EventCreate
import com.example.vibe_mobile.Clases.Space
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface EventService {
    @GET("api/events")
    suspend fun getEvents(): Response<List<Event>>

    @GET("api/events/{id}")
    suspend fun getEventById(@Path("id") id: Int): Response<Event>

    @GET("events/{id}/space")
    suspend fun getEventSpace(@Path("id") eventId: Int): Response<Space>

    @GET("api/events/organizer/{id}")
    suspend fun getEventsOrganizer(@Path("id") organizerId: Int?): Response<List<Event>>

    @Headers("Content-Type: application/json")
    @POST("api/events")
    suspend fun createEvent(@Body event: EventCreate): Response<CreateEventResponse>

}