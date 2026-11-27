package com.example.inmobi.features.settings.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.inmobi.core.network.NetworkModule
import com.example.inmobi.core.ui.theme.PrimaryTeal
import com.example.inmobi.core.util.UrlResolver
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLogoutSuccess: () -> Unit,
    onBack: (() -> Unit)? = null,
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(
            NetworkModule.settingsRepository,
            NetworkModule.authRepository
        )
    )
) {
    val state by viewModel.state.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current

    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showSetPasswordDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedBirthDateMillis by remember { mutableStateOf<Long?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.uploadAvatar(uri, context)
        }
    }

    LaunchedEffect(state.user, state.isLoading, state.error) {
        if (state.user == null && !state.isLoading && state.error == null) {
            viewModel.loadProfile()
        }
    }

    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null) {
            Toast.makeText(context, state.successMessage, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedBirthDateMillis
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedBirthDateMillis = datePickerState.selectedDateMillis
                        selectedBirthDateMillis?.let { millis ->
                            // Convertir usando UTC para evitar problemas de zona horaria
                            val localDate = java.time.LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                            viewModel.updateBirthDate(localDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Aceptar", color = PrimaryTeal)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false },
            onConfirm = { currentPassword, newPassword ->
                viewModel.changePassword(currentPassword, newPassword)
                showChangePasswordDialog = false
            }
        )
    }

    if (showSetPasswordDialog) {
        SetPasswordDialog(
            onDismiss = { showSetPasswordDialog = false },
            onConfirm = { newPassword ->
                viewModel.setPassword(newPassword)
                showSetPasswordDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = colorScheme.onSurface)
                }
            }
            Text(
                "Configuracion",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.isLoading) {
                Spacer(Modifier.height(40.dp))
                CircularProgressIndicator(color = PrimaryTeal)
            } else if (state.error != null && state.user == null) {
                Spacer(Modifier.height(40.dp))
                Text(state.error ?: "", fontSize = 14.sp, color = Color.Red, textAlign = TextAlign.Center)
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.loadProfile() },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                ) {
                    Text("Reintentar", color = Color.White)
                }
            } else {
                state.user?.let { user ->
                    Box(modifier = Modifier.padding(top = 8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(PrimaryTeal.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (user.avatarUrl != null) {
                                AsyncImage(
                                    model = UrlResolver.resolveImageUrl(user.avatarUrl),
                                    contentDescription = "Avatar",
                                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                                )
                            } else {
                                Icon(
                                    Icons.Default.Person,
                                    "Avatar",
                                    tint = PrimaryTeal,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }

                        if (user.role != "administrator") {
                            IconButton(
                                onClick = { photoPickerLauncher.launch("image/*") },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryTeal)
                            ) {
                                if (state.isUploadingAvatar) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.CameraAlt,
                                        "Cambiar foto",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        user.fullName ?: "Usuario",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                    Text(
                        user.email,
                        fontSize = 13.sp,
                        color = colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(20.dp))

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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
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
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    if (user.role == "administrator") {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                            border = BorderStroke(1.dp, colorScheme.outlineVariant)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Los administradores no pueden actualizar su perfil",
                                    fontSize = 13.sp,
                                    color = colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                            border = BorderStroke(1.dp, colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Text(
                                    "Editar perfil",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.onSurface
                                )

                                Spacer(Modifier.height(12.dp))

                                if (!state.isEditing) {
                                    Button(
                                        onClick = { viewModel.toggleEditMode() },
                                        modifier = Modifier.fillMaxWidth().height(42.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                                    ) {
                                        Icon(Icons.Default.Person, null, tint = Color.White, modifier = androidx.compose.ui.Modifier.size(18.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text("Editar informacion", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                } else {
                                    ProfileFormFields(
                                        state = state,
                                        viewModel = viewModel,
                                        colorScheme = colorScheme,
                                        showDatePicker = showDatePicker,
                                        onShowDatePicker = { showDatePicker = true }
                                    )

                                    Spacer(Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { viewModel.cancelEdit() },
                                            modifier = Modifier.weight(1f).height(42.dp),
                                            shape = RoundedCornerShape(10.dp),
                                            enabled = !state.isSaving
                                        ) {
                                            Text("Cancelar", color = colorScheme.onSurface, fontSize = 13.sp)
                                        }
                                        Button(
                                            onClick = { viewModel.saveProfile() },
                                            modifier = Modifier.weight(1f).height(42.dp),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                                            enabled = !state.isSaving
                                        ) {
                                            if (state.isSaving) {
                                                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                                            } else {
                                                Text("Guardar", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                            }
                                        }
                                    }

                                    if (state.error != null) {
                                        Spacer(Modifier.height(8.dp))
                                        Text(state.error ?: "", fontSize = 12.sp, color = Color(0xFFE53E3E))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    if (user.role != "administrator") {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                            border = BorderStroke(1.dp, colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Text(
                                    "Seguridad",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.onSurface
                                )

                                Spacer(Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        if (user.hasPassword) {
                                            showChangePasswordDialog = true
                                        } else {
                                            showSetPasswordDialog = true
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(42.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                                ) {
                                    Icon(Icons.Default.Lock, null, tint = Color.White, modifier = androidx.compose.ui.Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        if (user.hasPassword) "Cambiar contrasena" else "Crear contrasena",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(20.dp))
                    }

                    Button(
                        onClick = { viewModel.logout(onSuccess = onLogoutSuccess) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53E3E))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Cerrar sesion", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileFormFields(
    state: SettingsState,
    viewModel: SettingsViewModel,
    colorScheme: androidx.compose.material3.ColorScheme,
    showDatePicker: Boolean,
    onShowDatePicker: () -> Unit
) {
    val user = state.user ?: return

    when (user.role) {
        "user" -> {
            SettingsTextField(
                label = "Nombre completo",
                value = state.fullName,
                onValueChange = { viewModel.updateFullName(it) },
                colorScheme = colorScheme
            )
            Spacer(Modifier.height(10.dp))
            
            OutlinedTextField(
                value = try {
                    if (state.birthDate.isNotBlank()) {
                        val date = java.time.LocalDate.parse(state.birthDate)
                        date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    } else ""
                } catch (e: Exception) {
                    state.birthDate
                },
                onValueChange = { },
                label = { Text("Fecha de nacimiento", fontSize = 13.sp) },
                placeholder = { Text("Selecciona tu fecha", fontSize = 13.sp, color = colorScheme.onSurfaceVariant) },
                singleLine = true,
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                trailingIcon = {
                    IconButton(onClick = onShowDatePicker) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = "Seleccionar fecha",
                            tint = PrimaryTeal,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryTeal,
                    cursorColor = PrimaryTeal,
                    focusedLabelColor = PrimaryTeal
                ),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp)
            )
            
            Spacer(Modifier.height(10.dp))
            SettingsTextField(
                label = "Telefono (10 digitos)",
                value = state.phone,
                onValueChange = { viewModel.updatePhone(it) },
                keyboardType = KeyboardType.Phone,
                colorScheme = colorScheme
            )
            Spacer(Modifier.height(10.dp))
            PreferenceDropdown(
                selectedPreference = state.preference,
                onSelect = { viewModel.updatePreference(it) },
                colorScheme = colorScheme
            )
        }
        "legal_staff" -> {
            SettingsTextField(
                label = "Nombre completo",
                value = state.fullName,
                onValueChange = { viewModel.updateFullName(it) },
                colorScheme = colorScheme
            )
            Spacer(Modifier.height(10.dp))
            SettingsTextField(
                label = "Especialidad",
                value = state.specialty,
                onValueChange = { viewModel.updateSpecialty(it) },
                colorScheme = colorScheme
            )
            Spacer(Modifier.height(10.dp))
            SettingsTextField(
                label = "Despacho",
                value = state.firmName,
                onValueChange = { viewModel.updateFirmName(it) },
                colorScheme = colorScheme
            )
            Spacer(Modifier.height(10.dp))
            SettingsTextField(
                label = "Telefono (10 digitos)",
                value = state.staffPhone,
                onValueChange = { viewModel.updateStaffPhone(it) },
                keyboardType = KeyboardType.Phone,
                colorScheme = colorScheme
            )
        }
    }
}

@Composable
private fun SettingsTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    colorScheme: androidx.compose.material3.ColorScheme
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryTeal,
            cursorColor = PrimaryTeal,
            focusedLabelColor = PrimaryTeal
        ),
        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp)
    )
}

@Composable
private fun PreferenceDropdown(
    selectedPreference: String?,
    onSelect: (String) -> Unit,
    colorScheme: androidx.compose.material3.ColorScheme
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("rent" to "Rentar", "sell" to "Vender", "buy" to "Comprar", "lease" to "Arrendar")
    val currentLabel = options.firstOrNull { it.first == selectedPreference }?.second ?: "Seleccionar"

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Preferencia", fontSize = 12.sp, color = colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth().height(42.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(currentLabel, fontSize = 13.sp, color = colorScheme.onSurface, modifier = Modifier.weight(1f))
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { (key, label) ->
                    DropdownMenuItem(
                        text = { Text(label, fontSize = 13.sp) },
                        onClick = {
                            onSelect(key)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (currentPassword: String, newPassword: String) -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showCurrentPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

    val canConfirm = currentPassword.isNotBlank() &&
        newPassword.length >= 8 &&
        newPassword == confirmPassword

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cambiar contrasena", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Contrasena actual", fontSize = 13.sp) },
                    singleLine = true,
                    visualTransformation = if (showCurrentPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { showCurrentPassword = !showCurrentPassword }) {
                            Icon(
                                if (showCurrentPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryTeal,
                        cursorColor = PrimaryTeal,
                        focusedLabelColor = PrimaryTeal
                    )
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Nueva contrasena", fontSize = 13.sp) },
                    singleLine = true,
                    visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { showNewPassword = !showNewPassword }) {
                            Icon(
                                if (showNewPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryTeal,
                        cursorColor = PrimaryTeal,
                        focusedLabelColor = PrimaryTeal
                    )
                )
                if (newPassword.isNotBlank() && newPassword.length < 8) {
                    Text("Minimo 8 caracteres", fontSize = 11.sp, color = Color(0xFFE53E3E))
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar nueva contrasena", fontSize = 13.sp) },
                    singleLine = true,
                    visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                            Icon(
                                if (showConfirmPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryTeal,
                        cursorColor = PrimaryTeal,
                        focusedLabelColor = PrimaryTeal
                    )
                )
                if (confirmPassword.isNotBlank() && newPassword != confirmPassword) {
                    Text("Las contrasenas no coinciden", fontSize = 11.sp, color = Color(0xFFE53E3E))
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(currentPassword, newPassword) },
                enabled = canConfirm
            ) {
                Text("Guardar", color = if (canConfirm) PrimaryTeal else colorScheme.onSurfaceVariant)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = colorScheme.onSurfaceVariant)
            }
        }
    )
}

@Composable
private fun SetPasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (newPassword: String) -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

    val canConfirm = newPassword.length >= 8 && newPassword == confirmPassword

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Crear contrasena", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    "Crea una contrasena para poder iniciar sesion con tu email y contrasena.",
                    fontSize = 13.sp,
                    color = colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Nueva contrasena", fontSize = 13.sp) },
                    singleLine = true,
                    visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { showNewPassword = !showNewPassword }) {
                            Icon(
                                if (showNewPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryTeal,
                        cursorColor = PrimaryTeal,
                        focusedLabelColor = PrimaryTeal
                    )
                )
                if (newPassword.isNotBlank() && newPassword.length < 8) {
                    Text("Minimo 8 caracteres", fontSize = 11.sp, color = Color(0xFFE53E3E))
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar contrasena", fontSize = 13.sp) },
                    singleLine = true,
                    visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                            Icon(
                                if (showConfirmPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryTeal,
                        cursorColor = PrimaryTeal,
                        focusedLabelColor = PrimaryTeal
                    )
                )
                if (confirmPassword.isNotBlank() && newPassword != confirmPassword) {
                    Text("Las contrasenas no coinciden", fontSize = 11.sp, color = Color(0xFFE53E3E))
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(newPassword) },
                enabled = canConfirm
            ) {
                Text("Crear", color = if (canConfirm) PrimaryTeal else colorScheme.onSurfaceVariant)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = colorScheme.onSurfaceVariant)
            }
        }
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(label, fontSize = 13.sp, color = colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(
            value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}
