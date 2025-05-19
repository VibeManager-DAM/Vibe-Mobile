package com.example.vibe_mobile.Clases

import com.google.gson.annotations.SerializedName
data class EventCreate(
    @SerializedName("Title") val title: String,
    @SerializedName("Description") val description: String?,
    @SerializedName("Date") val date: String,
    @SerializedName("Time") val time: String,
    @SerializedName("Image") val image: String?,
    @SerializedName("Capacity") val capacity: Int,
    @SerializedName("Seats") val seats: Boolean,
    @SerializedName("Price") val price: Double,
    @SerializedName("NumRows") val num_rows: Int?,
    @SerializedName("NumColumns") val num_columns: Int?,
    @SerializedName("IdOrganizer") val id_organizer: Int?
)
