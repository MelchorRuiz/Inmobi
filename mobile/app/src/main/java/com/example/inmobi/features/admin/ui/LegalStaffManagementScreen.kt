package com.example.inmobi.features.admin.ui

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inmobi.core.ui.theme.PrimaryTeal
import com.example.inmobi.features.admin.data.LegalStaffResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalStaffManagementScreen(
    viewModel: LegalStaffViewModel = viewModel(factory = LegalStaffViewModel.Factory())
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colorScheme = MaterialTheme.colorScheme
    var showCreateForm by remember { mutableStateOf(false) }
    var editingStaff by remember { mutableStateOf<LegalStaffResponse?>(null) }
    var confirmDeleteId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(uiState.lastSavedUserId) {
        if (uiState.lastSavedUserId != null) {
            showCreateForm = false
            editingStaff = null
            viewModel.clearSavedFlag()
        }
    }

    LaunchedEffect(uiState.lastDeletedUserId) {
        if (uiState.lastDeletedUserId != null) {
            confirmDeleteId = null
            viewModel.clearSavedFlag()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Personal Legal",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colorScheme.onSurface
                    )
                    Text(
                        text = "${uiState.staff.size} ${if (uiState.staff.size == 1) "registro" else "registros"}",
                        fontSize = 12.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryTeal)
                    }
                }
                uiState.error != null && uiState.staff.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = uiState.error ?: "",
                                fontSize = 13.sp,
                                color = Color(0xFFE53E3E),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.loadStaff() },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                            ) {
                                Text("Reintentar", color = Color.White)
                            }
                        }
                    }
                }
                uiState.staff.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = null,
                                tint = colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No hay personal legal registrado",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Toca el boton + para agregar uno",
                                fontSize = 13.sp,
                                color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            start = 20.dp,
                            end = 20.dp,
                            bottom = 80.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.staff, key = { it.id }) { staff ->
                            StaffCard(
                                staff = staff,
                                onEdit = { editingStaff = staff },
                                onDelete = { confirmDeleteId = staff.id }
                            )
                        }
                    }
                }
            }

            uiState.savingError?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    fontSize = 12.sp,
                    color = Color(0xFFE53E3E),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                )
            }
        }

        if (!showCreateForm && editingStaff == null) {
            FloatingActionButton(
                onClick = { showCreateForm = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp),
                containerColor = PrimaryTeal,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar personal legal")
            }
        }
    }

    if (showCreateForm) {
        LegalStaffFormDialog(
            title = "Nuevo Personal Legal",
            isSaving = uiState.saving,
            onDismiss = {
                showCreateForm = false
                viewModel.clearSavedFlag()
            },
            onSubmit = { email, password, fullName, idNumber, specialty, firmName, phone ->
                viewModel.createStaff(email, password, fullName, idNumber, specialty, firmName, phone)
            }
        )
    }

    editingStaff?.let { staff ->
        LegalStaffFormDialog(
            title = "Editar Personal Legal",
            isSaving = uiState.saving,
            initialEmail = staff.email,
            initialFullName = staff.fullName ?: "",
            initialIdNumber = staff.legalStaff.idNumber,
            initialSpecialty = staff.legalStaff.specialty,
            initialFirmName = staff.legalStaff.firmName ?: "",
            initialPhone = staff.legalStaff.phone ?: "",
            onDismiss = {
                editingStaff = null
                viewModel.clearSavedFlag()
            },
            onSubmit = { email, password, fullName, idNumber, specialty, firmName, phone ->
                viewModel.updateStaff(
                    userId = staff.id,
                    password = password.takeIf { it.isNotBlank() },
                    fullName = fullName,
                    idNumber = idNumber,
                    specialty = specialty,
                    firmName = firmName,
                    phone = phone
                )
            }
        )
    }

    confirmDeleteId?.let { userId ->
        AlertDialog(
            onDismissRequest = { confirmDeleteId = null },
            title = { Text("Eliminar registro") },
            text = { Text("¿Estas seguro de que deseas eliminar este miembro del personal legal? Esta accion no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteStaff(userId) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53E3E))
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteId = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun StaffCard(
    staff: LegalStaffResponse,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PrimaryTeal.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = PrimaryTeal,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = staff.fullName ?: staff.email,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = staff.legalStaff.specialty,
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = staff.email,
                        fontSize = 11.sp,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color(0xFFE53E3E),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun LegalStaffFormDialog(
    title: String,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (email: String, password: String, fullName: String, idNumber: String, specialty: String, firmName: String, phone: String) -> Unit,
    initialEmail: String = "",
    initialFullName: String = "",
    initialIdNumber: String = "",
    initialSpecialty: String = "",
    initialFirmName: String = "",
    initialPhone: String = ""
) {
    var email by remember { mutableStateOf(initialEmail) }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf(initialFullName) }
    var idNumber by remember { mutableStateOf(initialIdNumber) }
    var specialty by remember { mutableStateOf(initialSpecialty) }
    var firmName by remember { mutableStateOf(initialFirmName) }
    var phone by remember { mutableStateOf(initialPhone) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val isEditing = initialEmail.isNotEmpty()

    val phoneError = if (phone.isNotEmpty() && !isValidPhone(phone)) "Debe tener 10 digitos" else null

    val canSubmit = email.isNotBlank() &&
        fullName.isNotBlank() &&
        idNumber.isNotBlank() &&
        specialty.isNotBlank() &&
        (isEditing || password.length >= 8) &&
        phoneError == null &&
        !isSaving

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (!isSaving) {
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", modifier = Modifier.size(18.dp))
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FormField(
                    label = "Nombre completo",
                    value = fullName,
                    onValueChange = { fullName = it },
                    enabled = !isSaving
                )
                FormField(
                    label = "Correo electronico",
                    value = email,
                    onValueChange = { email = it },
                    keyboardType = KeyboardType.Email,
                    enabled = !isSaving
                )
                FormField(
                    label = if (isEditing) "Nueva contraseña (dejar vacio para no cambiar)" else "Contraseña (min. 8 caracteres)",
                    value = password,
                    onValueChange = { password = it },
                    keyboardType = KeyboardType.Password,
                    isPassword = true,
                    isPasswordVisible = isPasswordVisible,
                    onTogglePassword = { isPasswordVisible = !isPasswordVisible },
                    enabled = !isSaving
                )
                FormField(
                    label = "Numero de cedula",
                    value = idNumber,
                    onValueChange = { idNumber = it },
                    enabled = !isSaving
                )
                FormField(
                    label = "Especialidad",
                    value = specialty,
                    onValueChange = { specialty = it },
                    enabled = !isSaving
                )
                FormField(
                    label = "Nombre del despacho (opcional)",
                    value = firmName,
                    onValueChange = { firmName = it },
                    enabled = !isSaving
                )
                FormField(
                    label = "Telefono (opcional, 10 digitos)",
                    value = phone,
                    onValueChange = { phone = it.filter { c -> c.isDigit() }.take(10) },
                    keyboardType = KeyboardType.Phone,
                    error = phoneError,
                    enabled = !isSaving
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(email, password, fullName, idNumber, specialty, firmName, phone) },
                enabled = canSubmit,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text("Guardar", color = Color.White)
                }
            }
        },
        dismissButton = {
            if (!isSaving) {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        }
    )
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onTogglePassword: () -> Unit = {},
    error: String? = null,
    enabled: Boolean = true
) {
    val colorScheme = MaterialTheme.colorScheme
    val errorColor = Color(0xFFE53E3E)

    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError = error != null,
            enabled = enabled,
            visualTransformation = when {
                isPassword && !isPasswordVisible -> PasswordVisualTransformation()
                else -> VisualTransformation.None
            },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = onTogglePassword) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } else null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (error != null) errorColor else PrimaryTeal,
                unfocusedBorderColor = if (error != null) errorColor else colorScheme.outlineVariant,
                focusedTextColor = colorScheme.onSurface,
                unfocusedTextColor = colorScheme.onSurface
            ),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp)
        )
        error?.let {
            Text(text = it, fontSize = 11.sp, color = errorColor)
        }
    }
}

private fun isValidPhone(phone: String): Boolean {
    return phone.length == 10 && phone.all { it.isDigit() }
}
