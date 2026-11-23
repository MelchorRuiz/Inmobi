package com.example.inmobi.features.messages.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OtherUserDto(
    @Json(name = "id") val id: Int,
    @Json(name = "full_name") val fullName: String?,
    @Json(name = "avatar_url") val avatarUrl: String?
)

@JsonClass(generateAdapter = true)
data class PropertySummaryDto(
    @Json(name = "property_id") val propertyId: Int,
    @Json(name = "listing_title") val listingTitle: String
)

@JsonClass(generateAdapter = true)
data class ChatSummaryDto(
    @Json(name = "chat_id") val chatId: Int,
    @Json(name = "other_user") val otherUser: OtherUserDto,
    @Json(name = "property") val property: PropertySummaryDto,
    @Json(name = "last_message") val lastMessage: String?,
    @Json(name = "unread_count") val unreadCount: Int,
    @Json(name = "last_message_at") val lastMessageAt: String?
)

@JsonClass(generateAdapter = true)
data class ChatCreateRequest(
    @Json(name = "property_id") val propertyId: Int
)

@JsonClass(generateAdapter = true)
data class ChatCreateResponse(
    @Json(name = "chat_id") val chatId: Int,
    @Json(name = "other_user") val otherUser: OtherUserDto,
    @Json(name = "property") val property: PropertySummaryDto,
    @Json(name = "last_message") val lastMessage: String?,
    @Json(name = "unread_count") val unreadCount: Int,
    @Json(name = "last_message_at") val lastMessageAt: String?
)

@JsonClass(generateAdapter = true)
data class MessageDto(
    @Json(name = "id") val id: Int,
    @Json(name = "sender_id") val senderId: Int,
    @Json(name = "content") val content: String,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "read_at") val readAt: String?
)

@JsonClass(generateAdapter = true)
data class ChatDetailDto(
    @Json(name = "chat_id") val chatId: Int,
    @Json(name = "other_user") val otherUser: OtherUserDto,
    @Json(name = "property") val property: PropertySummaryDto,
    @Json(name = "messages") val messages: List<MessageDto>,
    @Json(name = "unread_count") val unreadCount: Int
)

@JsonClass(generateAdapter = true)
data class MessageCreateRequest(
    @Json(name = "content") val content: String
)
