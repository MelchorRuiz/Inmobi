package com.example.inmobi.core.network

import com.example.inmobi.features.auth.data.AuthApi
import com.example.inmobi.features.auth.data.AuthRepository

object NetworkModule {

    val authApi: AuthApi by lazy {
        RetrofitClient.create()
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(authApi)
    }
}
