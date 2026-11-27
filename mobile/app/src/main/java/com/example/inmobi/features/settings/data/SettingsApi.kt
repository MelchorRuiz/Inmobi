package com.example.inmobi.features.settings.data

import com.example.inmobi.features.auth.data.dto.UserResponse
import com.example.inmobi.features.settings.data.dto.AvatarResponse
import com.example.inmobi.features.settings.data.dto.PasswordChangeRequest
import com.example.inmobi.features.settings.data.dto.PasswordSetRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part

interface SettingsApi {

    @PATCH("end-users/me/")
    suspend fun updateUserProfile(@Body request: RequestBody): Response<UserResponse>

    @PATCH("legal-staff/me/")
    suspend fun updateLegalStaffProfile(@Body request: RequestBody): Response<UserResponse>

    @Multipart
    @POST("auth/me/avatar/")
    suspend fun uploadAvatar(@Part file: MultipartBody.Part): Response<AvatarResponse>

    @POST("auth/me/change-password/")
    suspend fun changePassword(@Body request: PasswordChangeRequest): Response<Unit>

    @POST("auth/me/set-password/")
    suspend fun setPassword(@Body request: PasswordSetRequest): Response<Unit>
}
