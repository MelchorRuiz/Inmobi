package com.example.inmobi.features.messages.data

import com.example.inmobi.features.messages.data.dto.ChatCreateRequest
import com.example.inmobi.features.messages.data.dto.ChatCreateResponse
import com.example.inmobi.features.messages.data.dto.ChatDetailDto
import com.example.inmobi.features.messages.data.dto.ChatSummaryDto
import com.example.inmobi.features.messages.data.dto.MessageCreateRequest
import com.example.inmobi.features.messages.data.dto.MessageDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatsApi {

    @GET("chats/")
    suspend fun listChats(): Response<List<ChatSummaryDto>>

    @POST("chats/")
    suspend fun createOrGetChat(
        @Body request: ChatCreateRequest
    ): Response<ChatCreateResponse>

    @GET("chats/{chat_id}/")
    suspend fun getChat(
        @Path("chat_id") chatId: Int
    ): Response<ChatDetailDto>

    @POST("chats/{chat_id}/messages/")
    suspend fun sendMessage(
        @Path("chat_id") chatId: Int,
        @Body request: MessageCreateRequest
    ): Response<MessageDto>
}
