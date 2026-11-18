package com.example.inmobi.features.auth.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inmobi.core.auth.GoogleSignInManager
import com.example.inmobi.core.ui.theme.PrimaryTeal
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    registerViewModel: RegisterViewModel = viewModel(factory = RegisterViewModel.Factory(com.example.inmobi.core.network.NetworkModule.authRepository)),
    onBackToHome: () -> Unit = {},
    onGoToLogin: () -> Unit = {},
    onRegisterSuccess: (email: String) -> Unit = {},
    onProfileIncomplete: () -> Unit = {}
) {
    val context = LocalContext.current
    val formState by registerViewModel.formState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val errorColor = Color(0xFFE53E3E)

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedBirthDateMillis by remember { mutableStateOf<Long?>(null) }
    var isGoogleLoading by remember { mutableStateOf(false) }
    var googleError by remember { mutableStateOf<String?>(null) }
    val googleSignInManager = remember { GoogleSignInManager(context) }
    val repository = remember { com.example.inmobi.core.network.NetworkModule.authRepository }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(formState.registeredEmail) {
        formState.registeredEmail?.let { email ->
            registerViewModel.resetState()
            onRegisterSuccess(email)
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
                            val localDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            registerViewModel.onBirthDateChange(
                                localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                            )
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

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 2.dp),
                color = colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.HomeWork,
                                contentDescription = "Logo",
                                tint = PrimaryTeal,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Inmobi",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                    }

                    TextButton(
                        onClick = onBackToHome,
                        colors = ButtonDefaults.textButtonColors(contentColor = colorScheme.onSurfaceVariant)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Volver al Inicio",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                border = BorderStroke(1.dp, colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = PrimaryTeal,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Crea tu cuenta",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Vende, compra o alquila sabiendo con certeza el valor real del mercado.",
                        fontSize = 12.sp,
                        color = colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )

                    AnimatedVisibility(
                        visible = formState.errors["general"] != null || googleError != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        val message = formState.errors["general"] ?: googleError
                        message?.let {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = it,
                                fontSize = 12.sp,
                                color = errorColor,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    FieldLabel(icon = Icons.Outlined.Person, label = "Nombre completo")
                    OutlinedTextField(
                        value = formState.fullName,
                        onValueChange = { registerViewModel.onFullNameChange(it) },
                        placeholder = { Text(text = "Ej. Ana Martínez", fontSize = 13.sp, color = colorScheme.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        isError = formState.touchedFields.contains("fullName") && formState.errors["fullName"] != null,
                        supportingText = {
                            if (formState.touchedFields.contains("fullName") && formState.errors["fullName"] != null) {
                                Text(
                                    text = formState.errors["fullName"]!!,
                                    color = errorColor,
                                    fontSize = 11.sp
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = colorScheme.surfaceContainerLow,
                            focusedContainerColor = colorScheme.surface,
                            focusedBorderColor = PrimaryTeal,
                            unfocusedBorderColor = if (formState.touchedFields.contains("fullName") && formState.errors["fullName"] != null) errorColor else colorScheme.outlineVariant,
                            focusedTextColor = colorScheme.onSurface,
                            unfocusedTextColor = colorScheme.onSurface,
                            errorBorderColor = errorColor
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FieldLabel(icon = Icons.Outlined.Email, label = "Correo electrónico")
                    OutlinedTextField(
                        value = formState.email,
                        onValueChange = { registerViewModel.onEmailChange(it) },
                        placeholder = { Text(text = "tu@correo.com", fontSize = 13.sp, color = colorScheme.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = formState.touchedFields.contains("email") && formState.errors["email"] != null,
                        supportingText = {
                            if (formState.touchedFields.contains("email") && formState.errors["email"] != null) {
                                Text(
                                    text = formState.errors["email"]!!,
                                    color = errorColor,
                                    fontSize = 11.sp
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = colorScheme.surfaceContainerLow,
                            focusedContainerColor = colorScheme.surface,
                            focusedBorderColor = PrimaryTeal,
                            unfocusedBorderColor = if (formState.touchedFields.contains("email") && formState.errors["email"] != null) errorColor else colorScheme.outlineVariant,
                            focusedTextColor = colorScheme.onSurface,
                            unfocusedTextColor = colorScheme.onSurface,
                            errorBorderColor = errorColor
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FieldLabel(icon = Icons.Outlined.Phone, label = "Teléfono")
                    OutlinedTextField(
                        value = formState.phone,
                        onValueChange = { registerViewModel.onPhoneChange(it) },
                        placeholder = { Text(text = "Ej. 55 1234 5678", fontSize = 13.sp, color = colorScheme.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        isError = formState.touchedFields.contains("phone") && formState.errors["phone"] != null,
                        supportingText = {
                            if (formState.touchedFields.contains("phone") && formState.errors["phone"] != null) {
                                Text(
                                    text = formState.errors["phone"]!!,
                                    color = errorColor,
                                    fontSize = 11.sp
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = colorScheme.surfaceContainerLow,
                            focusedContainerColor = colorScheme.surface,
                            focusedBorderColor = PrimaryTeal,
                            unfocusedBorderColor = if (formState.touchedFields.contains("phone") && formState.errors["phone"] != null) errorColor else colorScheme.outlineVariant,
                            focusedTextColor = colorScheme.onSurface,
                            unfocusedTextColor = colorScheme.onSurface,
                            errorBorderColor = errorColor
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FieldLabel(icon = Icons.Outlined.CalendarMonth, label = "Fecha de nacimiento")
                    OutlinedTextField(
                        value = if (formState.birthDate.isNotBlank()) {
                            try {
                                val date = java.time.LocalDate.parse(formState.birthDate, DateTimeFormatter.ISO_LOCAL_DATE)
                                date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            } catch (e: Exception) {
                                formState.birthDate
                            }
                        } else "",
                        onValueChange = { },
                        placeholder = { Text(text = "Selecciona tu fecha", fontSize = 13.sp, color = colorScheme.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        readOnly = true,
                        isError = formState.touchedFields.contains("birthDate") && formState.errors["birthDate"] != null,
                        supportingText = {
                            if (formState.touchedFields.contains("birthDate") && formState.errors["birthDate"] != null) {
                                Text(
                                    text = formState.errors["birthDate"]!!,
                                    color = errorColor,
                                    fontSize = 11.sp
                                )
                            }
                        },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarMonth,
                                    contentDescription = "Seleccionar fecha",
                                    tint = PrimaryTeal,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = colorScheme.surfaceContainerLow,
                            focusedContainerColor = colorScheme.surface,
                            focusedBorderColor = PrimaryTeal,
                            unfocusedBorderColor = if (formState.touchedFields.contains("birthDate") && formState.errors["birthDate"] != null) errorColor else colorScheme.outlineVariant,
                            focusedTextColor = colorScheme.onSurface,
                            unfocusedTextColor = colorScheme.onSurface,
                            errorBorderColor = errorColor
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FieldLabel(icon = Icons.Outlined.Lock, label = "Contraseña")
                    OutlinedTextField(
                        value = formState.password,
                        onValueChange = { registerViewModel.onPasswordChange(it) },
                        placeholder = { Text(text = "Mínimo 8 caracteres", fontSize = 13.sp, color = colorScheme.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        isError = formState.touchedFields.contains("password") && formState.errors["password"] != null,
                        supportingText = {
                            if (formState.touchedFields.contains("password") && formState.errors["password"] != null) {
                                Text(
                                    text = formState.errors["password"]!!,
                                    color = errorColor,
                                    fontSize = 11.sp
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = colorScheme.surfaceContainerLow,
                            focusedContainerColor = colorScheme.surface,
                            focusedBorderColor = PrimaryTeal,
                            unfocusedBorderColor = if (formState.touchedFields.contains("password") && formState.errors["password"] != null) errorColor else colorScheme.outlineVariant,
                            focusedTextColor = colorScheme.onSurface,
                            unfocusedTextColor = colorScheme.onSurface,
                            errorBorderColor = errorColor
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FieldLabel(icon = Icons.Outlined.LockReset, label = "Confirmar contraseña")
                    OutlinedTextField(
                        value = formState.confirmPassword,
                        onValueChange = { registerViewModel.onConfirmPasswordChange(it) },
                        placeholder = { Text(text = "Repite tu contraseña", fontSize = 13.sp, color = colorScheme.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        isError = formState.touchedFields.contains("confirmPassword") && formState.errors["confirmPassword"] != null,
                        supportingText = {
                            if (formState.touchedFields.contains("confirmPassword") && formState.errors["confirmPassword"] != null) {
                                Text(
                                    text = formState.errors["confirmPassword"]!!,
                                    color = errorColor,
                                    fontSize = 11.sp
                                )
                            }
                        },
                        visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (isConfirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = colorScheme.surfaceContainerLow,
                            focusedContainerColor = colorScheme.surface,
                            focusedBorderColor = PrimaryTeal,
                            unfocusedBorderColor = if (formState.touchedFields.contains("confirmPassword") && formState.errors["confirmPassword"] != null) errorColor else colorScheme.outlineVariant,
                            focusedTextColor = colorScheme.onSurface,
                            unfocusedTextColor = colorScheme.onSurface,
                            errorBorderColor = errorColor
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "¿Qué te interesa hacer?",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val preferences = listOf(
                            "rent" to "Rentar",
                            "sell" to "Vender",
                            "buy" to "Comprar",
                            "lease" to "Arrendar"
                        )

                        preferences.forEach { (value, label) ->
                            FilterChip(
                                selected = formState.preference == value,
                                onClick = { registerViewModel.onPreferenceChange(value) },
                                label = {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = if (formState.preference == value) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryTeal.copy(alpha = 0.2f),
                                    selectedLabelColor = PrimaryTeal
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = colorScheme.outlineVariant,
                                    selectedBorderColor = PrimaryTeal,
                                    enabled = true,
                                    selected = formState.preference == value,
                                    borderWidth = 1.dp,
                                    selectedBorderWidth = 2.dp
                                )
                            )
                        }
                    }

                    if (formState.touchedFields.contains("preference") && formState.errors["preference"] != null) {
                        Text(
                            text = formState.errors["preference"]!!,
                            color = errorColor,
                            fontSize = 11.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = formState.privacyAccepted,
                            onCheckedChange = { registerViewModel.onPrivacyAcceptedChange(it) },
                            colors = CheckboxDefaults.colors(checkedColor = PrimaryTeal)
                        )

                        val termsText = buildAnnotatedString {
                            append("Acepto la ")
                            withStyle(style = SpanStyle(color = PrimaryTeal, fontWeight = FontWeight.Bold)) {
                                append("Política de Privacidad")
                            }
                            append(".")
                        }

                        Text(text = termsText, fontSize = 11.5.sp, color = colorScheme.onSurface)
                    }

                    if (formState.touchedFields.contains("privacyAccepted") && formState.errors["privacyAccepted"] != null) {
                        Text(
                            text = formState.errors["privacyAccepted"]!!,
                            color = errorColor,
                            fontSize = 11.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { registerViewModel.register() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryTeal,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                        enabled = !formState.isSubmitting
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (formState.isSubmitting) "Creando cuenta..." else "Crear cuenta",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = colorScheme.outlineVariant)
                        Text(text = "  o continúa con  ", fontSize = 11.sp, color = colorScheme.onSurfaceVariant)
                        HorizontalDivider(modifier = Modifier.weight(1f), color = colorScheme.outlineVariant)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = {
                            googleError = null
                            isGoogleLoading = true
                            coroutineScope.launch {
                                googleSignInManager.signIn()
                                    .mapCatching { idToken ->
                                        repository.googleLogin(idToken).getOrThrow()
                                    }
                                    .onSuccess {
                                        isGoogleLoading = false
                                        onProfileIncomplete()
                                    }
                                    .onFailure { throwable ->
                                        isGoogleLoading = false
                                        googleError = throwable.message ?: "Error al iniciar sesion con Google"
                                    }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, colorScheme.outlineVariant),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = colorScheme.surface,
                            contentColor = colorScheme.onSurface
                        ),
                        enabled = !isGoogleLoading
                    ) {
                        if (isGoogleLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = Color(0xFF4285F4)
                            )
                        } else {
                            Text(
                                text = "G  ",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF4285F4),
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Continuar con Google",
                            fontSize = 13.sp,
                            color = colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "¿Ya tienes cuenta? ",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        TextButton(
                            onClick = onGoToLogin,
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.textButtonColors(contentColor = PrimaryTeal)
                        ) {
                            Text(
                                text = "Inicia sesión",
                                fontSize = 12.sp,
                                color = PrimaryTeal,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "¿Eres profesional legal y quieres ofrecer tus servicios? El alta la realiza un administrador.",
                fontSize = 10.5.sp,
                color = colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 30.dp),
                lineHeight = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "© 2026 Inmobi. Todos los derechos reservados.",
                fontSize = 11.sp,
                color = colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun FieldLabel(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onSurface
        )
    }
}
