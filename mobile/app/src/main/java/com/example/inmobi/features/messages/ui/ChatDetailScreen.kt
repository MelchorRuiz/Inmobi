package com.example.inmobi.features.messages.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.inmobi.core.network.ChatWebSocket
import com.example.inmobi.core.network.NetworkModule
import com.example.inmobi.core.ui.theme.PrimaryTeal
import com.example.inmobi.core.util.UrlResolver
import com.example.inmobi.features.auth.ui.MainViewModel
import com.example.inmobi.features.messages.data.dto.MessageDto

@Composable
fun ChatDetailScreen(
    chatId: Int? = null,
    propertyId: Int? = null,
    onBack: () -> Unit = {},
    viewModel: ChatDetailViewModel = viewModel(),
    mainViewModel: MainViewModel = viewModel(
        factory = MainViewModel.Factory(NetworkModule.authRepository)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val mainState by mainViewModel.state.collectAsStateWithLifecycle()
    val colorScheme = MaterialTheme.colorScheme
    val listState = rememberLazyListState()

    LaunchedEffect(mainState.user?.id) {
        mainState.user?.id?.let { viewModel.setCurrentUserId(it.toInt()) }
    }

    LaunchedEffect(chatId, propertyId) {
        when {
            chatId != null -> viewModel.loadByChatId(chatId)
            propertyId != null -> viewModel.loadByPropertyId(propertyId)
        }
    }

    LaunchedEffect(uiState.chat?.messages?.size) {
        val messages = uiState.chat?.messages
        if (!messages.isNullOrEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    LaunchedEffect(uiState.chat?.chatId) {
        uiState.chat?.let { chat ->
            ChatWebSocket.updateSenderCache(
                chatId = chat.chatId,
                otherUserId = chat.otherUser.id,
                otherUserName = chat.otherUser.fullName,
                otherUserAvatar = chat.otherUser.avatarUrl
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .imePadding()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Outlined.ArrowBack, "Volver", tint = colorScheme.onSurface)
            }
            if (uiState.chat != null) {
                val chat = uiState.chat!!
                Spacer(Modifier.width(4.dp))
                Box(modifier = Modifier.size(38.dp)) {
                    Box(
                        modifier = Modifier.fillMaxSize().clip(CircleShape).background(colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        if (chat.otherUser.avatarUrl != null) {
                            AsyncImage(
                                model = UrlResolver.resolveImageUrl(chat.otherUser.avatarUrl),
                                contentDescription = chat.otherUser.fullName,
                                modifier = Modifier.fillMaxSize().clip(CircleShape)
                            )
                        } else {
                            Text(
                                getInitials(chat.otherUser.fullName),
                                fontWeight = FontWeight.Bold,
                                color = PrimaryTeal,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        chat.otherUser.fullName ?: "Usuario",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        chat.property.listingTitle,
                        fontSize = 11.sp,
                        color = colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryTeal)
                }
            }
            uiState.error != null -> {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(uiState.error!!, color = colorScheme.error, fontSize = 14.sp, modifier = Modifier.padding(bottom = 16.dp))
                        Button(
                            onClick = {
                                when {
                                    chatId != null -> viewModel.loadByChatId(chatId)
                                    propertyId != null -> viewModel.loadByPropertyId(propertyId)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            uiState.chat != null -> {
                val chat = uiState.chat!!
                val currentUserIdValue = uiState.currentUserId

                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item { Spacer(Modifier.height(0.dp)) }
                    items(chat.messages, key = { it.id }) { message ->
                        MessageBubble(
                            message = message,
                            isMine = currentUserIdValue != null && message.senderId == currentUserIdValue
                        )
                    }
                    item { Spacer(Modifier.height(0.dp)) }
                }

                MessageInput(
                    value = uiState.newMessageText,
                    onValueChange = { viewModel.updateMessageText(it) },
                    onSend = { viewModel.sendMessage(uiState.newMessageText) },
                    isSending = uiState.isSending
                )

                uiState.sendError?.let { error ->
                    Text(
                        error,
                        color = colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: MessageDto,
    isMine: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme
    val alignment = if (isMine) Alignment.End else Alignment.Start
    val bubbleColor = if (isMine) PrimaryTeal else colorScheme.surfaceVariant
    val textColor = if (isMine) Color.White else colorScheme.onSurface

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMine) 16.dp else 4.dp,
                bottomEnd = if (isMine) 4.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                message.content,
                fontSize = 13.sp,
                color = textColor,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun MessageInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    isSending: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text("Escribe un mensaje...", fontSize = 13.sp) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                singleLine = false,
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryTeal,
                    focusedLabelColor = PrimaryTeal,
                    cursorColor = PrimaryTeal
                )
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = onSend,
                enabled = value.trim().isNotEmpty() && !isSending,
                modifier = Modifier.size(44.dp)
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = PrimaryTeal,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        "Enviar",
                        tint = if (value.trim().isNotEmpty()) PrimaryTeal else colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

private fun getInitials(name: String?): String {
    if (name.isNullOrBlank()) return "?"
    return name.split(" ").take(2).mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("")
}
