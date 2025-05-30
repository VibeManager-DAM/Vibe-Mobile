package com.example.vibe_mobile.API.Users
import com.example.vibe_mobile.Clases.ChatPreview
import com.example.vibe_mobile.Clases.ModifyUserResponse
import com.example.vibe_mobile.Clases.RegisterResponse
import com.example.vibe_mobile.Clases.User
import com.example.vibe_mobile.Clases.UserTicketsResponse
import com.example.vibe_mobile.Clases.UserUpdateRequest
import retrofit2.Response
import retrofit2.http.*

interface UserService {
    @GET("api/users")
    suspend fun getUsuarios(): Response<List<User>>

    @POST("api/users/login")
    suspend fun login(
        @Query("email") email: String,
        @Query("password") password: String
    ): Response<User>

    @POST("api/users/register")
    suspend fun register(@Body data: User): Response<RegisterResponse>

    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") id: Int): Response<User>

    @GET("api/users/{id}/chats")
    suspend fun getChatsByUserId(@Path("id") userId: Int): Response<List<ChatPreview>>

    @GET("api/users/{id}/tickets")
    suspend fun getUserTickets(@Path("id") id: Int?): Response<UserTicketsResponse>


    @PUT("api/users/{id}")
    suspend fun modifyUser(
        @Path("id") userId: Int,
        @Body userUpdate: UserUpdateRequest
    ): Response<ModifyUserResponse>
}