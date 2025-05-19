package com.example.vibe_mobile.API.UploadImage

import com.example.vibe_mobile.API.RetrofitClient
import com.example.vibe_mobile.Clases.UploadImageResponse
import okhttp3.MultipartBody
import retrofit2.Call

class UploadImageRepository {

    private val uploadImageService: UploadImageService by lazy {
        RetrofitClient.createService(UploadImageService::class.java)
    }

    fun uploadImage(image: MultipartBody.Part): Call<UploadImageResponse> {
        return uploadImageService.uploadImage(image)
    }
}