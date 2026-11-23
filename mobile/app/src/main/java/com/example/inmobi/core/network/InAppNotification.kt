package com.example.inmobi.core.network

data class SenderInfo(
    val fullName: String?,
    val avatarUrl: String?
)

data class InAppNotification(
    val chatId: Int,
    val senderName: String?,
    val senderAvatarUrl: String?,
    val preview: String
)
