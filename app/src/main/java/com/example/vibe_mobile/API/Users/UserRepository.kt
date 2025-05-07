package com.example.vibe_mobile.repository

import com.example.vibe_mobile.API.RetrofitClient
import com.example.vibe_mobile.API.Users.UserService
import com.example.vibe_mobile.Clases.RegisterResponse
import com.example.vibe_mobile.Clases.Ticket
import com.example.vibe_mobile.Clases.User
import retrofit2.Response

class UserRepository {

    private val userService: UserService by lazy {
        RetrofitClient.createService(UserService::class.java)
    }

    suspend fun getAllUsers(): Response<List<User>> {
        return userService.getUsuarios()
    }

    suspend fun login(email: String, password: String): Response<User> {
        return userService.login(email,password)
    }

    suspend fun registerUser(data: User): Response<RegisterResponse> {
        return userService.register(data)
    }

    suspend fun getUserById(id: Int): Response<User>{
        return userService.getUserById(id)
    }

    suspend fun getUserTickets(id: Int): Response<List<Ticket>>{
        return userService.getUserTickets(id)
    }

}
