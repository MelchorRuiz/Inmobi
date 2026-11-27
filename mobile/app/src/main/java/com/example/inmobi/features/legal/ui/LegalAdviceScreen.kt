package com.example.inmobi.features.legal.ui

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.outlined.RealEstateAgent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.inmobi.BuildConfig
import com.example.inmobi.core.ui.theme.PrimaryTeal
import com.example.inmobi.features.legal.data.dto.InterestedPropertyDto

@Composable
fun LegalAdviceScreen(
    onBack: () -> Unit,
    viewModel: LegalAdviceViewModel = viewModel(factory = LegalAdviceViewModel.Factory())
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colorScheme = MaterialTheme.colorScheme

    var selectedPropertyId by remember { mutableStateOf<Int?>(null) }
    var selectedPropertyTitle by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var descriptionText by remember { mutableStateOf("") }
    var descriptionError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadProperties()
    }

    LaunchedEffect(uiState.submitSuccess) {
        if (uiState.submitSuccess) {
            showDialog = false
            descriptionText = ""
            descriptionError = null
            selectedPropertyId = null
            viewModel.resetSubmitSuccess()
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(colorScheme.background)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Default.Gavel, null, tint = PrimaryTeal, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Asesoría Legal",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
        }

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryTeal)
                }
            }
            uiState.error != null && uiState.properties.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = uiState.error ?: "Error desconocido",
                            color = colorScheme.error,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        TextButton(onClick = { viewModel.loadProperties() }) {
                            Text("Reintentar", color = PrimaryTeal)
                        }
                    }
                }
            }
            uiState.properties.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.RealEstateAgent,
                            null,
                            tint = PrimaryTeal.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "No tienes propiedades con interés",
                            fontSize = 16.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Muestra interés en una propiedad para poder solicitar asesoría legal sobre ella.",
                            fontSize = 13.sp,
                            color = colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.properties) { property ->
                        InterestedPropertyCard(
                            property = property,
                            onClick = {
                                selectedPropertyId = property.propertyId
                                selectedPropertyTitle = property.listingTitle
                                showDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDialog && selectedPropertyId != null) {
        LegalAdviceRequestDialog(
            propertyTitle = selectedPropertyTitle,
            descriptionText = descriptionText,
            descriptionError = descriptionError,
            isSubmitting = uiState.isSubmitting,
            onDescriptionChange = { text ->
                descriptionText = text
                if (text.length >= 10) descriptionError = null
            },
            onSubmit = {
                if (descriptionText.length < 10) {
                    descriptionError = "Mínimo 10 caracteres"
                } else {
                    viewModel.submitRequest(selectedPropertyId!!, descriptionText)
                }
            },
            onDismiss = {
                showDialog = false
                descriptionText = ""
                descriptionError = null
                selectedPropertyId = null
            }
        )
    }
}

@Composable
private fun InterestedPropertyCard(
    property: InterestedPropertyDto,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (!property.hasLegalAdviceRequest) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                val imageUrl = property.images.firstOrNull()
                if (imageUrl != null) {
                    val fullUrl = if (imageUrl.startsWith("http")) {
                        imageUrl
                    } else {
                        "${BuildConfig.BASE_URL}$imageUrl"
                    }
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(fullUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = property.listingTitle,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Outlined.RealEstateAgent,
                        null,
                        tint = PrimaryTeal.copy(alpha = 0.4f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = property.listingTitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(8.dp))

                if (property.hasLegalAdviceRequest) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CheckCircle,
                            null,
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Solicitud enviada",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF059669)
                        )
                    }
                } else {
                    Text(
                        text = "Toca para solicitar asesoría",
                        fontSize = 12.sp,
                        color = PrimaryTeal,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun LegalAdviceRequestDialog(
    propertyTitle: String,
    descriptionText: String,
    descriptionError: String?,
    isSubmitting: Boolean,
    onDescriptionChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    AlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        icon = {
            Icon(Icons.Default.Gavel, null, tint = PrimaryTeal, modifier = Modifier.size(28.dp))
        },
        title = {
            Text(
                text = "Solicitar Asesoría Legal",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                Text(
                    text = propertyTitle,
                    fontSize = 13.sp,
                    color = colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Describe brevemente tu problema legal:",
                    fontSize = 13.sp,
                    color = colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = descriptionText,
                    onValueChange = onDescriptionChange,
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    placeholder = { Text("Ej: Necesito ayuda con...", fontSize = 13.sp) },
                    isError = descriptionError != null,
                    enabled = !isSubmitting,
                    shape = RoundedCornerShape(12.dp)
                )
                if (descriptionError != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = descriptionError,
                        fontSize = 12.sp,
                        color = colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSubmit,
                enabled = !isSubmitting && descriptionText.length >= 10,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text("Enviar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSubmitting
            ) {
                Text("Cancelar", color = colorScheme.onSurfaceVariant)
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
