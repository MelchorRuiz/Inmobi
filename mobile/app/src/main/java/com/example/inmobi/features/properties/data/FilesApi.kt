package com.example.inmobi.features.properties.data

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import com.example.inmobi.features.properties.data.dto.FileUploadResponse

interface FilesApi {

    @Multipart
    @POST("files/upload/")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part
    ): Response<FileUploadResponse>
}
