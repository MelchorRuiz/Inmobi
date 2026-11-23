package com.example.inmobi.features.messages.data

import com.example.inmobi.core.network.RetrofitClient
import com.example.inmobi.features.messages.data.dto.ChatCreateRequest
import com.example.inmobi.features.messages.data.dto.ChatCreateResponse
import com.example.inmobi.features.messages.data.dto.ChatDetailDto
import com.example.inmobi.features.messages.data.dto.ChatSummaryDto
import com.example.inmobi.features.messages.data.dto.MessageCreateRequest
import com.example.inmobi.features.messages.data.dto.MessageDto
import com.example.inmobi.features.properties.data.Result

class ChatsRepository {

    private val api: ChatsApi = RetrofitClient.create()

    suspend fun listChats(): Result<List<ChatSummaryDto>> {
        return safeApiCall { api.listChats() }
    }

    suspend fun createOrGetChat(propertyId: Int): Result<ChatCreateResponse> {
        return safeApiCall { api.createOrGetChat(ChatCreateRequest(propertyId)) }
    }

    suspend fun getChat(chatId: Int): Result<ChatDetailDto> {
        return safeApiCall { api.getChat(chatId) }
    }

    suspend fun sendMessage(chatId: Int, content: String): Result<MessageDto> {
        return safeApiCall { api.sendMessage(chatId, MessageCreateRequest(content)) }
    }

    private suspend fun <T> safeApiCall(call: suspend () -> retrofit2.Response<T>): Result<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(body)
                } else {
                    Result.Success(Unit as T)
                }
            } else {
                Result.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            Result.NetworkError
        }
    }
}
