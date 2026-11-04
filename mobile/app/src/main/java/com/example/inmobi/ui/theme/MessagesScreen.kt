package com.example.inmobi.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inmobi.R

data class ChatMessageItem(
    val id: String,
    val senderName: String,
    val propertyTitle: String,
    val lastMessage: String,
    val time: String,
    val unreadCount: Int,
    val isOnline: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    onGoToLogin: () -> Unit = {},
    onGoToRegister: () -> Unit = {},
    onNavigateToSection: (String) -> Unit = {},
    onOpenChat: (ChatMessageItem) -> Unit = {} // Callback para abrir el chat individual
) {
    val colorScheme = MaterialTheme.colorScheme
    var isMenuExpanded by remember { mutableStateOf(false) }

    val chats = remember {
        listOf(
            ChatMessageItem(
                id = "1",
                senderName = "Carlos Mendoza",
                propertyTitle = "Residencia Moderna en Cañada",
                lastMessage = "¿Aún sigue disponible para agendar visita este sábado?",
                time = "10:45 AM",
                unreadCount = 2,
                isOnline = true
            ),
            ChatMessageItem(
                id = "2",
                senderName = "Sofía Rangel",
                propertyTitle = "Departamento Cerca de UTL",
                lastMessage = "Perfecto, me interesa el esquema de renta justa.",
                time = "Ayer",
                unreadCount = 0,
                isOnline = false
            ),
            ChatMessageItem(
                id = "3",
                senderName = "Asesor IA Inmobi",
                propertyTitle = "Tasación de Propiedad",
                lastMessage = "Tu certificado de precio justo ya ha sido actualizado con éxito.",
                time = "24 oct",
                unreadCount = 1,
                isOnline = true
            )
        )
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 3.dp),
                color = colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { isMenuExpanded = true }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_inmobi_launcher_background),
                                    contentDescription = "Logo Inmobi",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Inmobi",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface
                            )
                        }

                        DropdownMenu(
                            expanded = isMenuExpanded,
                            onDismissRequest = { isMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Vender") },
                                leadingIcon = { Icon(Icons.Outlined.Sell, contentDescription = null) },
                                onClick = { isMenuExpanded = false; onNavigateToSection("sales") }
                            )
                            DropdownMenuItem(
                                text = { Text("Arrendar") },
                                leadingIcon = { Icon(Icons.Outlined.CalendarMonth, contentDescription = null) },
                                onClick = { isMenuExpanded = false; onNavigateToSection("arrendar") }
                            )
                            DropdownMenuItem(
                                text = { Text("Comprar") },
                                leadingIcon = { Icon(Icons.Outlined.ShoppingBag, contentDescription = null) },
                                onClick = { isMenuExpanded = false; onNavigateToSection("comprar") }
                            )
                            DropdownMenuItem(
                                text = { Text("Rentar") },
                                leadingIcon = { Icon(Icons.Outlined.Key, contentDescription = null) },
                                onClick = { isMenuExpanded = false; onNavigateToSection("rentar") }
                            )
                            DropdownMenuItem(
                                text = { Text("Mensajes", fontWeight = FontWeight.Bold, color = PrimaryTeal) },
                                leadingIcon = { Icon(Icons.AutoMirrored.Outlined.Chat, contentDescription = null, tint = PrimaryTeal) },
                                onClick = { isMenuExpanded = false; onNavigateToSection("mensajes") }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Salir / Inicio") },
                                leadingIcon = { Icon(Icons.AutoMirrored.Outlined.Login, contentDescription = null) },
                                onClick = { isMenuExpanded = false; onNavigateToSection("home") }
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = colorScheme.surfaceVariant,
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Chat,
                        contentDescription = null,
                        tint = PrimaryTeal,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Centro de mensajes",
                        color = PrimaryTeal,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Tus conversaciones",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Comunícate directamente con compradores, inquilinos y el soporte automatizado de Inmobi.",
                fontSize = 12.sp,
                color = colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            chats.forEach { chat ->
                ChatCardItem(
                    chat = chat,
                    onClickChat = { onOpenChat(chat) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "© 2026 Inmobi. Todos los derechos reservados.",
                fontSize = 11.sp,
                color = colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ChatCardItem(
    chat: ChatMessageItem,
    onClickChat: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
            .clickable { onClickChat() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        border = BorderStroke(1.dp, colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar con indicador de estado en línea
            Box(modifier = Modifier.size(46.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = chat.senderName.take(2).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTeal,
                        fontSize = 14.sp
                    )
                }
                if (chat.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                            .align(Alignment.BottomEnd)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chat.senderName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                    Text(
                        text = chat.time,
                        fontSize = 10.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = chat.propertyTitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryTeal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chat.lastMessage,
                        fontSize = 11.sp,
                        color = colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (chat.unreadCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = PrimaryTeal,
                            shape = CircleShape,
                            modifier = Modifier.size(20.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = chat.unreadCount.toString(),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}