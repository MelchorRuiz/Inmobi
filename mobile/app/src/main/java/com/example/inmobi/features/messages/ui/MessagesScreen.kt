package com.example.inmobi.features.messages.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.inmobi.core.ui.theme.PrimaryTeal
import com.example.inmobi.core.util.UrlResolver
import com.example.inmobi.features.messages.data.dto.ChatSummaryDto
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun MessagesScreen(
    onChatClick: (chatId: Int) -> Unit = {},
    viewModel: MessagesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        viewModel.loadChats()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        Surface(color = colorScheme.surfaceVariant, shape = RoundedCornerShape(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                Icon(Icons.AutoMirrored.Outlined.Chat, null, tint = PrimaryTeal, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
                Text("Centro de mensajes", color = PrimaryTeal, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(12.dp))
        Text("Tus conversaciones", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = colorScheme.onSurface)
        Spacer(Modifier.height(4.dp))
        Text("Comunicate directamente con propietarios y el soporte automatizado de Inmobi.", fontSize = 12.sp, color = colorScheme.onSurfaceVariant, lineHeight = 16.sp)
        Spacer(Modifier.height(20.dp))

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryTeal)
                }
            }
            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(uiState.error!!, color = colorScheme.error, fontSize = 14.sp, modifier = Modifier.padding(bottom = 16.dp))
                        Button(
                            onClick = { viewModel.retry() },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            uiState.chats.isEmpty() -> {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text("No tienes conversaciones aún", fontSize = 14.sp, color = colorScheme.onSurfaceVariant)
                }
            }
            else -> {
                LazyColumn {
                    items(uiState.chats, key = { it.chatId }) { chat ->
                        ChatCard(chat, onClick = { onChatClick(chat.chatId) })
                        Spacer(Modifier.height(12.dp))
                    }
                    item {
                        Spacer(Modifier.height(20.dp))
                        Text("2026 Inmobi. Todos los derechos reservados.", fontSize = 11.sp, color = colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatCard(chat: ChatSummaryDto, onClick: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    val formattedTime = formatMessageTime(chat.lastMessageAt)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        border = BorderStroke(1.dp, colorScheme.outlineVariant)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(46.dp)) {
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
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(chat.otherUser.fullName ?: "Usuario", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colorScheme.onSurface, lineHeight = 18.sp)
                    Text(formattedTime, fontSize = 10.sp, color = colorScheme.onSurfaceVariant, lineHeight = 14.sp)
                }
                Text(chat.property.listingTitle, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = PrimaryTeal, maxLines = 1, overflow = TextOverflow.Ellipsis, lineHeight = 14.sp)
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        chat.lastMessage ?: "Sin mensajes",
                        fontSize = 11.sp,
                        color = colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 14.sp
                    )
                }
                if (chat.unreadCount > 0) {
                    Text(
                        "${chat.unreadCount} mensajes",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTeal,
                        lineHeight = 14.sp
                    )
                }
            }

            Spacer(Modifier.width(4.dp))
            Icon(Icons.Default.ChevronRight, null, tint = colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
        }
    }
}

private fun getInitials(name: String?): String {
    if (name.isNullOrBlank()) return "?"
    return name.split(" ").take(2).mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("")
}

private fun formatMessageTime(isoString: String?): String {
    if (isoString == null) return ""
    return try {
        val dateTime = OffsetDateTime.parse(isoString)
        val now = OffsetDateTime.now()
        val diff = now.toEpochSecond() - dateTime.toEpochSecond()

        when {
            diff < 86400 -> dateTime.format(DateTimeFormatter.ofPattern("h:mm a"))
            diff < 172800 -> "Ayer"
            diff < 604800 -> dateTime.format(DateTimeFormatter.ofPattern("EEE"))
            else -> dateTime.format(DateTimeFormatter.ofPattern("d MMM"))
        }
    } catch (e: Exception) {
        ""
    }
}
