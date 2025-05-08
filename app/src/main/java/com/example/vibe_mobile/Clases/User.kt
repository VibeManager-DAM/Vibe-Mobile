package com.example.vibe_mobile.Clases

data class User(
    val id: Int? = null,
    val fullname: String,
    val email: String,
    val password: String,
    val id_rol: Int
)