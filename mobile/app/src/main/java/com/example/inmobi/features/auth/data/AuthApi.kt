package com.example.inmobi.features.auth.data

import com.example.inmobi.features.auth.data.dto.CompleteProfileRequest
import com.example.inmobi.features.auth.data.dto.GoogleLoginRequest
import com.example.inmobi.features.auth.data.dto.LoginRequest
import com.example.inmobi.features.auth.data.dto.LogoutRequest
import com.example.inmobi.features.auth.data.dto.RefreshRequest
import com.example.inmobi.features.auth.data.dto.RegisterRequest
import com.example.inmobi.features.auth.data.dto.ResendVerificationRequest
import com.example.inmobi.features.auth.data.dto.SessionResponse
import com.example.inmobi.features.auth.data.dto.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/login/")
    suspend fun login(@Body request: LoginRequest): Response<SessionResponse>

    @POST("auth/google/")
    suspend fun googleLogin(@Body request: GoogleLoginRequest): Response<SessionResponse>

    @POST("end-users/register/")
    suspend fun register(@Body request: RegisterRequest): Response<UserResponse>

    @POST("auth/resend-verification/")
    suspend fun resendVerification(@Body request: ResendVerificationRequest): Response<Unit>

    @POST("auth/refresh/")
    suspend fun refresh(@Body request: RefreshRequest): Response<SessionResponse>

    @POST("auth/logout/")
    suspend fun logout(@Body request: LogoutRequest): Response<Unit>

    @GET("auth/me/")
    suspend fun getMe(): Response<UserResponse>

    @PATCH("end-users/me/")
    suspend fun completeProfile(@Body request: CompleteProfileRequest): Response<UserResponse>
}
