package com.example.inmobi.features.admin.data

import com.example.inmobi.core.network.RetrofitClient
import com.example.inmobi.features.properties.data.Result
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

private data class ApiError(
    @Json(name = "detail") val detail: String?
)

class LegalStaffRepository {
    private val api: LegalStaffApi = RetrofitClient.create()
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val errorAdapter = moshi.adapter(ApiError::class.java)

    suspend fun list(): Result<List<LegalStaffResponse>> {
        return try {
            val response = api.list()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(body)
                } else {
                    Result.Success(emptyList())
                }
            } else {
                Result.Error(response.code(), parseError(response.errorBody()?.string()) ?: response.message())
            }
        } catch (e: Exception) {
            Result.NetworkError
        }
    }

    suspend fun get(userId: Long): Result<LegalStaffResponse> {
        return try {
            val response = api.get(userId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(body)
                } else {
                    Result.Error(response.code(), "Empty response")
                }
            } else {
                Result.Error(response.code(), parseError(response.errorBody()?.string()) ?: response.message())
            }
        } catch (e: Exception) {
            Result.NetworkError
        }
    }

    suspend fun create(request: LegalStaffCreateRequest): Result<LegalStaffResponse> {
        return try {
            val response = api.create(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(body)
                } else {
                    Result.Error(response.code(), "Empty response")
                }
            } else {
                Result.Error(response.code(), parseError(response.errorBody()?.string()) ?: response.message())
            }
        } catch (e: Exception) {
            Result.NetworkError
        }
    }

    suspend fun update(userId: Long, request: LegalStaffUpdateRequest): Result<LegalStaffResponse> {
        return try {
            val response = api.update(userId, request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(body)
                } else {
                    Result.Error(response.code(), "Empty response")
                }
            } else {
                Result.Error(response.code(), parseError(response.errorBody()?.string()) ?: response.message())
            }
        } catch (e: Exception) {
            Result.NetworkError
        }
    }

    suspend fun delete(userId: Long): Result<Unit> {
        return try {
            val response = api.delete(userId)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(response.code(), parseError(response.errorBody()?.string()) ?: response.message())
            }
        } catch (e: Exception) {
            Result.NetworkError
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
