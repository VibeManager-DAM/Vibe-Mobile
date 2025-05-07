package com.example.vibe_mobile.Clases

data class Space(
    val id: Int,
    val name: String,
    val square_meters: Double,
    val capacity: Int,
    val address: String,
    val latitude: Double,
    val longitude: Double
)
