package com.example.vibe_mobile.API.Spaces
import com.example.vibe_mobile.API.RetrofitClient
import com.example.vibe_mobile.Clases.Space
import retrofit2.Response

class SpaceRepository {
    private val spaceService: SpaceService by lazy {
        RetrofitClient.createService(SpaceService::class.java)
    }

    suspend fun getAllSpaces(): Response<List<Space>> {
        return spaceService.getAllSpaces()
    }

    suspend fun getSpaceById(id: Int): Response<Space> {
        return spaceService.getSpaceById(id)
    }
}