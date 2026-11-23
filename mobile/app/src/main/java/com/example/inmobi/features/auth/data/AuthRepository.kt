package com.example.inmobi.features.auth.data

import com.example.inmobi.core.network.TokenManager
import com.example.inmobi.features.auth.data.dto.CompleteProfileRequest
import com.example.inmobi.features.auth.data.dto.GoogleLoginRequest
import com.example.inmobi.features.auth.data.dto.LoginRequest
import com.example.inmobi.features.auth.data.dto.LogoutRequest
import com.example.inmobi.features.auth.data.dto.RefreshRequest
import com.example.inmobi.features.auth.data.dto.RegisterRequest
import com.example.inmobi.features.auth.data.dto.ResendVerificationRequest
import com.example.inmobi.features.auth.data.dto.SessionResponse
import com.example.inmobi.features.auth.data.dto.UserResponse
import com.example.inmobi.features.auth.model.LoginException
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

private data class ApiError(
    @Json(name = "detail") val detail: String?
)

class AuthRepository(private val authApi: AuthApi) {

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val errorAdapter = moshi.adapter(ApiError::class.java)

    suspend fun login(email: String, password: String): Result<SessionResponse> {
        return try {
            val response = authApi.login(LoginRequest(email = email, password = password, clientType = "mobile"))
            if (response.isSuccessful) {
                val sessionResponse = response.body()
                if (sessionResponse != null) {
                    TokenManager.saveTokens(sessionResponse.accessToken, sessionResponse.refreshToken)
                    TokenManager.saveRole(sessionResponse.role)
                    Result.success(sessionResponse)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val detail = parseErrorDetail(errorBody)

                val exception = when {
                    detail?.contains("verify", ignoreCase = true) == true ->
                        LoginException.EmailNotVerified(email)
                    response.code() == 401 -> LoginException.InvalidCredentials
                    detail?.contains("deactivated", ignoreCase = true) == true ->
                        LoginException.AccountDeactivated
                    else -> LoginException.Generic(detail ?: "Error ${response.code()}")
                }
                Result.failure(exception)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun googleLogin(idToken: String): Result<SessionResponse> {
        return try {
            val response = authApi.googleLogin(GoogleLoginRequest(idToken = idToken))
            if (response.isSuccessful) {
                val sessionResponse = response.body()
                if (sessionResponse != null) {
                    TokenManager.saveTokens(sessionResponse.accessToken, sessionResponse.refreshToken)
                    TokenManager.saveRole(sessionResponse.role)
                    Result.success(sessionResponse)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val detail = parseErrorDetail(errorBody)

                val exception = when {
                    detail?.contains("deactivated", ignoreCase = true) == true ->
                        LoginException.AccountDeactivated
                    else -> LoginException.Generic(detail ?: "Error ${response.code()}")
                }
                Result.failure(exception)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun completeProfile(phone: String, birthDate: String, privacyAccepted: Boolean, preference: String): Result<UserResponse> {
        return try {
            val response = authApi.completeProfile(
                CompleteProfileRequest(phone = phone, birthDate = birthDate, privacyAccepted = privacyAccepted, preference = preference)
            )
            if (response.isSuccessful) {
                val userResponse = response.body()
                if (userResponse != null) {
                    Result.success(userResponse)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val detail = parseErrorDetail(errorBody)
                Result.failure(Exception(detail ?: "Error ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        birthDate: String,
        preference: String,
        privacyAccepted: Boolean
    ): Result<UserResponse> {
        return try {
            val response = authApi.register(
                RegisterRequest(
                    fullName = fullName,
                    email = email,
                    phone = phone,
                    password = password,
                    birthDate = birthDate,
                    privacyAccepted = privacyAccepted,
                    preference = preference
                )
            )
            if (response.isSuccessful) {
                val userResponse = response.body()
                if (userResponse != null) {
                    Result.success(userResponse)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val detail = parseErrorDetail(errorBody) ?: "Error en el registro (${response.code()})"
                Result.failure(Exception(detail))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resendVerification(email: String): Result<Unit> {
        return try {
            val response = authApi.resendVerification(ResendVerificationRequest(email))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val detail = parseErrorDetail(errorBody) ?: "Error al reenviar verificación (${response.code()})"
                Result.failure(Exception(detail))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshToken(): Result<SessionResponse> {
        return try {
            val refreshToken = TokenManager.getRefreshToken()
            val response = authApi.refresh(RefreshRequest(clientType = "mobile", refreshToken = refreshToken))
            if (response.isSuccessful) {
                val sessionResponse = response.body()
                if (sessionResponse != null) {
                    TokenManager.saveTokens(sessionResponse.accessToken, sessionResponse.refreshToken)
                    TokenManager.saveRole(sessionResponse.role)
                    Result.success(sessionResponse)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception("Refresh failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            val refreshToken = TokenManager.getRefreshToken()
            authApi.logout(LogoutRequest(clientType = "mobile", refreshToken = refreshToken))
            Result.success(Unit)
        } catch (_: Exception) {
            Result.success(Unit)
        } finally {
            TokenManager.clearTokens()
        }
    }

    suspend fun getUserProfile(): Result<UserResponse> {
        return try {
            val response = authApi.getMe()
            if (response.isSuccessful) {
                val userResponse = response.body()
                if (userResponse != null) {
                    Result.success(userResponse)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception("Get profile failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseErrorDetail(json: String?): String? {
        if (json.isNullOrBlank()) return null
        return try {
            errorAdapter.fromJson(json)?.detail
        } catch (e: Exception) {
            null
        }
    }
}
