package com.example.vibe_mobile.API.Spaces

import com.example.vibe_mobile.Clases.Space
import retrofit2.Response
import retrofit2.http.*

interface SpaceService {

    @GET("spaces")
    suspend fun getAllSpaces(): Response<List<Space>>

    @GET("spaces/{id}")
    suspend fun getSpaceById(@Path("id") spaceId: Int): Response<Space>
}