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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.inmobi.core.ui.theme.PrimaryTeal

data class ChatItem(
    val id: String,
    val senderName: String,
    val propertyTitle: String,
    val lastMessage: String,
    val time: String,
    val unreadCount: Int,
    val isOnline: Boolean
)

@Composable
fun MessagesScreen() {
    val colorScheme = MaterialTheme.colorScheme

    val chats = listOf(
        ChatItem("1", "Carlos Mendoza", "Residencia Moderna en Canada", "Aun sigue disponible para agendar visita este sabado?", "10:45 AM", 2, true),
        ChatItem("2", "Sofia Rangel", "Departamento Cerca de UTL", "Perfecto, me interesa el esquema de renta justa.", "Ayer", 0, false),
        ChatItem("3", "Asesor IA Inmobi", "Tasacion de Propiedad", "Tu certificado de precio justo ha sido actualizado con exito.", "24 oct", 1, true)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .verticalScroll(rememberScrollState())
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
        Text("Comunicate directamente con compradores, inquilinos y el soporte automatizado de Inmobi.", fontSize = 12.sp, color = colorScheme.onSurfaceVariant, lineHeight = 16.sp)
        Spacer(Modifier.height(20.dp))

        chats.forEach { chat ->
            ChatCard(chat)
            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.height(20.dp))
        Text("2026 Inmobi. Todos los derechos reservados.", fontSize = 11.sp, color = colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ChatCard(chat: ChatItem) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
            .clickable {},
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        border = BorderStroke(1.dp, colorScheme.outlineVariant)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(46.dp)) {
                Box(
                    modifier = Modifier.fillMaxSize().clip(CircleShape).background(colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(chat.senderName.take(2).uppercase(), fontWeight = FontWeight.Bold, color = PrimaryTeal, fontSize = 14.sp)
                }
                if (chat.isOnline) {
                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(Color(0xFF10B981)).align(Alignment.BottomEnd))
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(chat.senderName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
                    Text(chat.time, fontSize = 10.sp, color = colorScheme.onSurfaceVariant)
                }
                Text(chat.propertyTitle, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = PrimaryTeal, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(2.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(chat.lastMessage, fontSize = 11.sp, color = colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                    if (chat.unreadCount > 0) {
                        Spacer(Modifier.width(8.dp))
                        Surface(color = PrimaryTeal, shape = CircleShape, modifier = Modifier.size(20.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(chat.unreadCount.toString(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.width(4.dp))
            Icon(Icons.Default.ChevronRight, null, tint = colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
        }
    }
}
