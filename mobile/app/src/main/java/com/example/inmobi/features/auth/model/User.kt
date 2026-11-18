package com.example.inmobi.features.auth.model

data class User(
    val id: Long? = null,
    val fullName: String,
    val email: String,
    val phone: String? = null
)
