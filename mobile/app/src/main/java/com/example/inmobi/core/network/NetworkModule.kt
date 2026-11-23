package com.example.inmobi.core.network

import com.example.inmobi.features.admin.data.LegalStaffApi
import com.example.inmobi.features.admin.data.LegalStaffRepository
import com.example.inmobi.features.auth.data.AuthApi
import com.example.inmobi.features.auth.data.AuthRepository

object NetworkModule {

    val authApi: AuthApi by lazy {
        RetrofitClient.create()
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(authApi)
    }

    val legalStaffApi: LegalStaffApi by lazy {
        RetrofitClient.create()
    }

    val legalStaffRepository: LegalStaffRepository by lazy {
        LegalStaffRepository()
    }
}
