package com.example.inmobi.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class MessageBubble(
    val id: String,
    val text: String,
    val time: String,
    val isFromMe: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    contactName: String = "Carlos Mendoza",
    propertyTitle: String = "Residencia Moderna en Cañada",
    initialAutoMessage: String = "",
    onBack: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // VALIDACIÓN INTELIGENTE DE MENSAJES:
    // Si viene un mensaje automático (ej. al agendar una cita), inicia con ese mensaje.
    // Si no viene ninguno (chat nuevo desde el botón contactar), inicia completamente vacío.
    val messages = remember(initialAutoMessage) {
        val baseList = mutableListOf<MessageBubble>()

        if (initialAutoMessage.isNotBlank()) {
            // Caso 1: Viene de una acción específica (como agendar cita o consulta)
            baseList.add(
                MessageBubble(
                    id = System.currentTimeMillis().toString(),
                    text = initialAutoMessage,
                    time = "Ahora",
                    isFromMe = true
                )
            )
        } else {
            // Caso 2: Chat limpio para nuevas conversaciones (puedes dejarlo vacío o con un saludo neutral del sistema)
            baseList.add(
                MessageBubble(
                    id = "welcome",
                    text = "¡Hola! Has iniciado una conversación protegida por Inmobi AI respecto a este inmueble.",
                    time = "Sistema",
                    isFromMe = false
                )
            )
        }

        mutableStateListOf(*baseList.toTypedArray())
    }

    // Desplazamiento automático al final de la lista de mensajes
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
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
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Regresar",
                                tint = colorScheme.onSurface
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(PrimaryTeal.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = contactName.take(2).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = PrimaryTeal,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = contactName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface
                            )
                            Text(
                                text = propertyTitle,
                                fontSize = 11.sp,
                                color = PrimaryTeal,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                    }

                    IconButton(onClick = { /* Opciones */ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Opciones",
                            tint = colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 8.dp),
                color = colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Escribe un mensaje...", fontSize = 13.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryTeal,
                            unfocusedBorderColor = colorScheme.outlineVariant,
                            focusedContainerColor = colorScheme.surface,
                            unfocusedContainerColor = colorScheme.surfaceContainerLow
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                messages.add(
                                    MessageBubble(
                                        id = System.currentTimeMillis().toString(),
                                        text = messageText,
                                        time = "Ahora",
                                        isFromMe = true
                                    )
                                )
                                messageText = ""
                            }
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .background(PrimaryTeal, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    border = BorderStroke(1.dp, PrimaryTeal.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimaryTeal.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.VerifiedUser,
                                contentDescription = null,
                                tint = PrimaryTeal,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = propertyTitle,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface
                            )
                            Text(
                                text = "Trato directo verificado por Inmobi AI",
                                fontSize = 10.5.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            items(messages, key = { it.id }) { bubble ->
                ChatBubbleItem(bubble = bubble)
            }
        }
    }
}

@Composable
fun ChatBubbleItem(bubble: MessageBubble) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (bubble.isFromMe) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (bubble.isFromMe) PrimaryTeal else colorScheme.surfaceVariant,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (bubble.isFromMe) 16.dp else 4.dp,
                bottomEnd = if (bubble.isFromMe) 4.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(
                    text = bubble.text,
                    fontSize = 13.sp,
                    color = if (bubble.isFromMe) Color.White else colorScheme.onSurface,
                    lineHeight = 17.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = bubble.time,
                    fontSize = 9.5.sp,
                    color = if (bubble.isFromMe) Color.White.copy(alpha = 0.85f) else colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}