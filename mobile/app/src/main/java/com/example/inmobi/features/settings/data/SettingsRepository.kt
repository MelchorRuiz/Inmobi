package com.example.inmobi.features.settings.data

import com.example.inmobi.core.network.RetrofitClient
import com.example.inmobi.features.auth.data.dto.UserResponse
import com.example.inmobi.features.settings.data.dto.AvatarResponse
import com.example.inmobi.features.settings.data.dto.PasswordChangeRequest
import com.example.inmobi.features.settings.data.dto.PasswordSetRequest
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private data class ApiError(
    @Json(name = "detail") val detail: String?
)

class SettingsRepository(private val api: SettingsApi) {

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val errorAdapter = moshi.adapter(ApiError::class.java)
    private val mapAdapter = moshi.adapter(Map::class.java)

    suspend fun updateUserProfile(
        fullName: String? = null,
        birthDate: String? = null,
        phone: String? = null,
        preference: String? = null,
        privacyAccepted: Boolean? = null
    ): Result<UserResponse> {
        return try {
            val data = mutableMapOf<String, Any>()
            if (fullName != null) data["full_name"] = fullName
            if (birthDate != null) data["birth_date"] = birthDate
            if (phone != null) data["phone"] = phone
            if (preference != null) data["preference"] = preference
            if (privacyAccepted != null) data["privacy_accepted"] = privacyAccepted
            
            val json = mapAdapter.toJson(data)
            val body = json.toRequestBody("application/json".toMediaTypeOrNull())
            
            val response = api.updateUserProfile(body)
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    Result.success(responseBody)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                val detail = parseError(response.errorBody()?.string())
                Result.failure(Exception(detail ?: "Error ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateLegalStaffProfile(
        fullName: String? = null,
        idNumber: String? = null,
        specialty: String? = null,
        firmName: String? = null,
        phone: String? = null
    ): Result<UserResponse> {
        return try {
            val data = mutableMapOf<String, Any>()
            if (fullName != null) data["full_name"] = fullName
            if (idNumber != null) data["id_number"] = idNumber
            if (specialty != null) data["specialty"] = specialty
            if (firmName != null) data["firm_name"] = firmName
            if (phone != null) data["phone"] = phone
            
            val json = mapAdapter.toJson(data)
            val body = json.toRequestBody("application/json".toMediaTypeOrNull())
            
            val response = api.updateLegalStaffProfile(body)
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    Result.success(responseBody)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                val detail = parseError(response.errorBody()?.string())
                Result.failure(Exception(detail ?: "Error ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadAvatar(file: File): Result<String> {
        return try {
            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
            val response = api.uploadAvatar(part)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body.url)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                val detail = parseError(response.errorBody()?.string())
                Result.failure(Exception(detail ?: "Error ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> {
        return try {
            val request = PasswordChangeRequest(currentPassword, newPassword)
            val response = api.changePassword(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val detail = parseError(response.errorBody()?.string())
                Result.failure(Exception(detail ?: "Error ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setPassword(newPassword: String): Result<Unit> {
        return try {
            val request = PasswordSetRequest(newPassword)
            val response = api.setPassword(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val detail = parseError(response.errorBody()?.string())
                Result.failure(Exception(detail ?: "Error ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseError(json: String?): String? {
        if (json.isNullOrBlank()) return null
        return try {
            errorAdapter.fromJson(json)?.detail
        } catch (_: Exception) {
            null
        }
    }
}
