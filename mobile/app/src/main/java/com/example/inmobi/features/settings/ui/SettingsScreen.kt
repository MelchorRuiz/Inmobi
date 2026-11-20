package com.example.inmobi.features.settings.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inmobi.core.ui.theme.PrimaryTeal
import com.example.inmobi.features.auth.ui.MainViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SettingsScreen(
    onLogoutSuccess: () -> Unit,
    viewModel: MainViewModel = viewModel(factory = MainViewModel.Factory(com.example.inmobi.core.network.NetworkModule.authRepository))
) {
    val state by viewModel.state.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(state.user, state.isLoading, state.error) {
        if (state.user == null && !state.isLoading && state.error == null) {
            viewModel.loadProfile()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))

        Text("Configuracion", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)

        Spacer(Modifier.height(24.dp))

        if (state.isLoading) {
            CircularProgressIndicator(color = PrimaryTeal)
        } else if (state.error != null) {
            Text(state.error ?: "", fontSize = 14.sp, color = Color.Red, textAlign = TextAlign.Center)
            Spacer(Modifier.height(12.dp))
            Button(onClick = { viewModel.loadProfile() }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)) {
                Text("Reintentar", color = Color.White)
            }
        } else {
            state.user?.let { user ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier.size(80.dp).clip(CircleShape).background(PrimaryTeal.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, "Avatar", tint = PrimaryTeal, modifier = Modifier.size(48.dp))
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(user.fullName ?: "Usuario", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
                    Text(user.email, fontSize = 14.sp, color = colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                }

                Spacer(Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    border = BorderStroke(1.dp, colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        InfoRow("Rol", when (user.role) {
                            "administrator" -> "Administrador"
                            "user" -> "Usuario"
                            "legal_staff" -> "Personal Legal"
                            else -> user.role
                        })

                        Spacer(Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.Email, null, tint = PrimaryTeal, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Email verificado", fontSize = 13.sp, color = colorScheme.onSurfaceVariant)
                            }
                            if (user.emailVerified) {
                                Icon(Icons.Default.CheckCircle, "Verified", tint = PrimaryTeal, modifier = Modifier.size(20.dp))
                            } else {
                                Text("No verificado", fontSize = 12.sp, color = Color(0xFFE53E3E))
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        InfoRow("Fecha de registro", try {
                            val dateTime = LocalDateTime.parse(user.createdAt)
                            dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                        } catch (e: Exception) {
                            user.createdAt
                        })
                    }
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.logout(onSuccess = onLogoutSuccess) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53E3E)),
                    enabled = !state.isLoggingOut && !state.logoutFailed
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = when {
                            state.isLoggingOut -> "Cerrando sesion..."
                            state.logoutFailed -> "Error al cerrar sesion"
                            else -> "Cerrar sesion"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                if (state.logoutFailed) {
                    Spacer(Modifier.height(8.dp))
                    Text("No se puede cerrar sesion. Intenta nuevamente.", fontSize = 12.sp, color = Color(0xFFE53E3E), textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { viewModel.logout(onSuccess = onLogoutSuccess) }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal), modifier = Modifier.height(36.dp)) {
                        Text("Reintentar", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    val colorScheme = MaterialTheme.colorScheme
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Text(label, fontSize = 13.sp, color = colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colorScheme.onSurface, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
    }
}
