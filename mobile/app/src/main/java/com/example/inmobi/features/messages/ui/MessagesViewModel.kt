package com.example.inmobi.features.messages.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inmobi.core.network.ChatWebSocket
import com.example.inmobi.core.network.InboundWsMessage
import com.example.inmobi.features.messages.data.ChatsRepository
import com.example.inmobi.features.messages.data.dto.ChatSummaryDto
import com.example.inmobi.features.properties.data.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MessagesUiState(
    val isLoading: Boolean = false,
    val chats: List<ChatSummaryDto> = emptyList(),
    val error: String? = null
)

class MessagesViewModel : ViewModel() {

    private val repository = ChatsRepository()
    private val _uiState = MutableStateFlow(MessagesUiState(isLoading = true))
    val uiState: StateFlow<MessagesUiState> = _uiState.asStateFlow()

    init {
        startObservingWebSocket()
    }

    private fun startObservingWebSocket() {
        viewModelScope.launch {
            ChatWebSocket.incomingMessages.collect { message ->
                when (message) {
                    is InboundWsMessage.NewMessage -> {
                        val payload = message.payload
                        updateChatInList(payload.chatId, payload.content, payload.createdAt)
                    }
                    is InboundWsMessage.Unknown -> {}
                }
            }
        }
    }

    private fun updateChatInList(chatId: Int, lastMessage: String, lastMessageAt: String) {
        val currentChats = _uiState.value.chats.toMutableList()
        val index = currentChats.indexOfFirst { it.chatId == chatId }

        if (index >= 0) {
            val chat = currentChats[index]
            val updatedChat = chat.copy(
                lastMessage = lastMessage,
                lastMessageAt = lastMessageAt,
                unreadCount = chat.unreadCount + 1
            )
            currentChats[index] = updatedChat
            val target = currentChats.removeAt(index)
            currentChats.add(0, target)
            _uiState.value = _uiState.value.copy(chats = currentChats)
        }
    }

    fun loadChats() {
        _uiState.value = MessagesUiState(isLoading = true)
        viewModelScope.launch {
            when (val result = repository.listChats()) {
                is Result.Success -> {
                    result.data.forEach { chat ->
                        ChatWebSocket.updateSenderCache(
                            chatId = chat.chatId,
                            otherUserId = chat.otherUser.id,
                            otherUserName = chat.otherUser.fullName,
                            otherUserAvatar = chat.otherUser.avatarUrl
                        )
                    }
                    _uiState.value = MessagesUiState(
                        isLoading = false,
                        chats = result.data
                    )
                }
                is Result.Error -> _uiState.value = MessagesUiState(
                    isLoading = false,
                    error = "Error al cargar chats: ${result.message}"
                )
                is Result.NetworkError -> _uiState.value = MessagesUiState(
                    isLoading = false,
                    error = "Error de conexión"
                )
            }
        }
    }

    fun retry() {
        loadChats()
    }
}
