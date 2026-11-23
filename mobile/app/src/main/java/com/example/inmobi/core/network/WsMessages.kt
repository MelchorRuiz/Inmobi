package com.example.inmobi.core.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WsAuthMessage(
    @Json(name = "type") val type: String,
    @Json(name = "token") val token: String
)

@JsonClass(generateAdapter = true)
data class WsNewMessagePayload(
    @Json(name = "type") val type: String,
    @Json(name = "chat_id") val chatId: Int,
    @Json(name = "sender_id") val senderId: Int,
    @Json(name = "content") val content: String,
    @Json(name = "created_at") val createdAt: String
)

sealed class InboundWsMessage {
    data class NewMessage(val payload: WsNewMessagePayload) : InboundWsMessage()
    data class Unknown(val raw: String) : InboundWsMessage()
}
