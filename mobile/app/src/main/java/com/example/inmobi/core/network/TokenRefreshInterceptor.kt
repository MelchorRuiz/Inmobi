package com.example.inmobi.core.network

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.util.concurrent.TimeUnit

class TokenRefreshInterceptor : Interceptor {

    private val mutex = Mutex()
    private val jsonMediaType = "application/json".toMediaType()

    data class RefreshResponse(
        @Json(name = "access_token") val accessToken: String? = null,
        @Json(name = "refresh_token") val refreshToken: String? = null
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        if (isAuthEndpoint(originalRequest.url.toString())) {
            return chain.proceed(originalRequest)
        }

        val originalResponse = chain.proceed(originalRequest)

        if (originalResponse.code != HTTP_UNAUTHORIZED) {
            return originalResponse
        }

        val refreshToken = TokenManager.getRefreshToken() ?: return originalResponse

        return try {
            val newAccessToken = refreshAccessToken(refreshToken)

            if (newAccessToken != null) {
                originalResponse.close()

                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $newAccessToken")
                    .build()

                chain.proceed(newRequest)
            } else {
                originalResponse
            }
        } catch (e: Exception) {
            originalResponse
        }
    }

    private fun isAuthEndpoint(url: String): Boolean {
        return url.contains("/auth/login") ||
                url.contains("/auth/register") ||
                url.contains("/auth/refresh") ||
                url.contains("/auth/logout")
    }

    private fun refreshAccessToken(refreshToken: String): String? {
        return kotlinx.coroutines.runBlocking {
            mutex.withLock {
                try {
                    val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
                    val adapter = moshi.adapter(RefreshResponse::class.java)

                    val client = OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build()

                    val requestBody = """{"client_type":"mobile","refresh_token":"$refreshToken"}"""
                        .toRequestBody(jsonMediaType)

                    val request = Request.Builder()
                        .url(com.example.inmobi.BuildConfig.BASE_URL + "auth/refresh/")
                        .post(requestBody)
                        .build()

                    val response = client.newCall(request).execute()

                    if (response.isSuccessful) {
                        val body = response.body?.string()
                        val refreshResponse = body?.let { adapter.fromJson(it) }

                        val newAccessToken = refreshResponse?.accessToken
                        val newRefreshToken = refreshResponse?.refreshToken

                        if (newAccessToken != null) {
                            TokenManager.saveTokens(newAccessToken, newRefreshToken)
                            newAccessToken
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    companion object {
        private const val HTTP_UNAUTHORIZED = 401
    }
}
