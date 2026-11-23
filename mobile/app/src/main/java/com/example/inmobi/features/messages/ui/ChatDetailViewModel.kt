package com.example.inmobi.features.messages.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inmobi.core.network.ChatWebSocket
import com.example.inmobi.core.network.InboundWsMessage
import com.example.inmobi.features.messages.data.ChatsRepository
import com.example.inmobi.features.messages.data.dto.ChatDetailDto
import com.example.inmobi.features.messages.data.dto.MessageDto
import com.example.inmobi.features.properties.data.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatDetailUiState(
    val isLoading: Boolean = false,
    val chat: ChatDetailDto? = null,
    val error: String? = null,
    val newMessageText: String = "",
    val isSending: Boolean = false,
    val sendError: String? = null,
    val currentUserId: Int? = null
)

class ChatDetailViewModel : ViewModel() {

    private val repository = ChatsRepository()
    private val _uiState = MutableStateFlow(ChatDetailUiState(isLoading = true))
    val uiState: StateFlow<ChatDetailUiState> = _uiState.asStateFlow()

    private var currentChatId: Int? = null

    init {
        startObservingWebSocket()
    }

    private fun startObservingWebSocket() {
        viewModelScope.launch {
            ChatWebSocket.incomingMessages.collect { message ->
                when (message) {
                    is InboundWsMessage.NewMessage -> {
                        val payload = message.payload
                        if (payload.chatId == currentChatId) {
                            val newMessage = MessageDto(
                                id = System.currentTimeMillis().toInt(),
                                senderId = payload.senderId,
                                content = payload.content,
                                createdAt = payload.createdAt,
                                readAt = null
                            )
                            val updatedChat = _uiState.value.chat?.let { chat ->
                                chat.copy(messages = chat.messages + newMessage)
                            }
                            _uiState.value = _uiState.value.copy(chat = updatedChat)
                        }
                    }
                    is InboundWsMessage.Unknown -> {}
                }
            }
        }
    }

    fun setCurrentUserId(userId: Int) {
        _uiState.value = _uiState.value.copy(currentUserId = userId)
    }

    fun loadByChatId(chatId: Int) {
        currentChatId = chatId
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            when (val result = repository.getChat(chatId)) {
                is Result.Success -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    chat = result.data,
                    error = null
                )
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar el chat: ${result.message}"
                )
                is Result.NetworkError -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error de conexión"
                )
            }
        }
    }

    fun loadByPropertyId(propertyId: Int) {
        _uiState.value = ChatDetailUiState(isLoading = true)
        viewModelScope.launch {
            when (val createResult = repository.createOrGetChat(propertyId)) {
                is Result.Success -> {
                    val chatId = createResult.data.chatId
                    currentChatId = chatId
                    when (val chatResult = repository.getChat(chatId)) {
                        is Result.Success -> _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            chat = chatResult.data,
                            error = null
                        )
                        is Result.Error -> _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Error al cargar el chat: ${chatResult.message}"
                        )
                        is Result.NetworkError -> _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Error de conexión"
                        )
                    }
                }
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al crear el chat: ${createResult.message}"
                )
                is Result.NetworkError -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error de conexión"
                )
            }
        }
    }

    fun updateMessageText(text: String) {
        _uiState.value = _uiState.value.copy(newMessageText = text)
    }

    fun sendMessage(content: String) {
        val chatId = currentChatId ?: return
        val trimmed = content.trim()
        if (trimmed.isEmpty()) return

        _uiState.value = _uiState.value.copy(isSending = true, sendError = null)
        viewModelScope.launch {
            when (val result = repository.sendMessage(chatId, trimmed)) {
                is Result.Success -> {
                    val newMessage = result.data
                    val updatedChat = _uiState.value.chat?.let { chat ->
                        chat.copy(
                            messages = chat.messages + newMessage,
                            unreadCount = 0
                        )
                    }
                    _uiState.value = _uiState.value.copy(
                        isSending = false,
                        chat = updatedChat,
                        newMessageText = "",
                        sendError = null
                    )
                }
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    isSending = false,
                    sendError = "No se pudo enviar: ${result.message}"
                )
                is Result.NetworkError -> _uiState.value = _uiState.value.copy(
                    isSending = false,
                    sendError = "Error de conexión"
                )
            }
        }
    }

    fun clearSendError() {
        _uiState.value = _uiState.value.copy(sendError = null)
    }

    fun refresh() {
        val chatId = currentChatId ?: return
        loadByChatId(chatId)
    }
}
