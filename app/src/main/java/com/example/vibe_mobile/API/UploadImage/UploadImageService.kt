package com.example.vibe_mobile.API.UploadImage

import com.example.vibe_mobile.Clases.UploadImageResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface UploadImageService {

    @Multipart
    @POST("api/upload")
    fun uploadImage(
        @Part image: MultipartBody.Part
    ): Call<UploadImageResponse>
}