package com.example.inmobi.features.properties.ui.form

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inmobi.core.ui.theme.PrimaryTeal
import com.example.inmobi.features.properties.domain.OperationType
import com.example.inmobi.features.properties.domain.PropertyType
import com.example.inmobi.features.properties.ui.common.ErrorState
import com.example.inmobi.features.properties.ui.common.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyFormScreen(
    propertyId: Int? = null,
    defaultOperationType: String = "sale",
    onBack: () -> Unit = {},
    onSaved: () -> Unit = {},
    viewModel: PropertyFormViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formData by viewModel.formData.collectAsStateWithLifecycle()
    val colorScheme = MaterialTheme.colorScheme

    val isEditing = propertyId != null

    LaunchedEffect(propertyId) {
        if (propertyId != null) {
            viewModel.loadPropertyForEdit(propertyId)
        } else {
            viewModel.initializeForCreate()
        }
    }

    LaunchedEffect(uiState.savedSuccessfully) {
        if (uiState.savedSuccessfully) {
            viewModel.clearSavedState()
            onSaved()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Outlined.ArrowBack, "Volver", tint = colorScheme.onSurface)
            }
            Text(
                if (isEditing) "Editar propiedad" else "Nueva propiedad",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
        }

        when {
            uiState.isLoading -> LoadingState()
            uiState.error != null && isEditing -> ErrorState(uiState.error!!) {
                viewModel.loadPropertyForEdit(propertyId!!)
            }
            else -> {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Spacer(Modifier.height(8.dp))

                    Text("Informacion basica", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
                    Spacer(Modifier.height(8.dp))

                    FormTextField(
                        label = "Titulo de la publicacion *",
                        value = formData.listingTitle,
                        onValueChange = { viewModel.updateField { copy(listingTitle = it) } }
                    )
                    Spacer(Modifier.height(12.dp))

                    FormTextField(
                        label = "Descripcion",
                        value = formData.description,
                        onValueChange = { viewModel.updateField { copy(description = it) } },
                        singleLine = false
                    )
                    Spacer(Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        FormPriceField(
                            label = "Precio *",
                            value = formData.price?.toString() ?: "",
                            onValueChange = { viewModel.updateField { copy(price = it.toDoubleOrNull()) } },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        FormTextField(
                            label = "Moneda",
                            value = formData.currency,
                            onValueChange = { viewModel.updateField { copy(currency = it) } },
                            modifier = Modifier.width(90.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))

                    var propertyTypeExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = propertyTypeExpanded,
                        onExpandedChange = { propertyTypeExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = PropertyType.fromApiName(formData.propertyType)?.name ?: formData.propertyType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tipo de propiedad") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = propertyTypeExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryTeal,
                                focusedLabelColor = PrimaryTeal,
                                cursorColor = PrimaryTeal
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = propertyTypeExpanded,
                            onDismissRequest = { propertyTypeExpanded = false }
                        ) {
                            PropertyType.entries.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }) },
                                    onClick = {
                                        viewModel.updateField { copy(propertyType = type.apiName) }
                                        propertyTypeExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    Text("Ubicacion", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
                    Spacer(Modifier.height(8.dp))

                    FormTextField(
                        label = "Ciudad *",
                        value = formData.city,
                        onValueChange = { viewModel.updateField { copy(city = it) } }
                    )
                    Spacer(Modifier.height(12.dp))

                    FormTextField(
                        label = "Estado *",
                        value = formData.stateProvince,
                        onValueChange = { viewModel.updateField { copy(stateProvince = it) } }
                    )
                    Spacer(Modifier.height(16.dp))

                    Text("Caracteristicas (opcional)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
                    Spacer(Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        FormNumberField(
                            label = "Recamaras",
                            value = formData.bedrooms?.toString() ?: "",
                            onValueChange = { viewModel.updateField { copy(bedrooms = it.toIntOrNull()) } },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        FormNumberField(
                            label = "Banos compl.",
                            value = formData.fullBathrooms?.toString() ?: "",
                            onValueChange = { viewModel.updateField { copy(fullBathrooms = it.toIntOrNull()) } },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        FormNumberField(
                            label = "Medios banos",
                            value = formData.halfBathrooms?.toString() ?: "",
                            onValueChange = { viewModel.updateField { copy(halfBathrooms = it.toIntOrNull()) } },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        FormNumberField(
                            label = "Area construida (m2)",
                            value = formData.builtAreaSqm?.toString() ?: "",
                            onValueChange = { viewModel.updateField { copy(builtAreaSqm = it.toDoubleOrNull()) } },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        FormNumberField(
                            label = "Area terreno (m2)",
                            value = formData.lotSizeSqm?.toString() ?: "",
                            onValueChange = { viewModel.updateField { copy(lotSizeSqm = it.toDoubleOrNull()) } },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(12.dp))

                    FormNumberField(
                        label = "Estacionamientos",
                        value = formData.parkingSpaces?.toString() ?: "",
                        onValueChange = { viewModel.updateField { copy(parkingSpaces = it.toIntOrNull()) } }
                    )

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = {
                            viewModel.saveProperty(
                                operationType = defaultOperationType,
                                propertyId = propertyId
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !uiState.isSaving,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal, contentColor = Color.White)
                    ) {
                        Text(
                            if (uiState.isSaving) "Guardando..." else if (isEditing) "Guardar cambios" else "Publicar propiedad",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (uiState.error != null && !isEditing) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            uiState.error!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = singleLine,
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryTeal,
            focusedLabelColor = PrimaryTeal,
            cursorColor = PrimaryTeal
        )
    )
}

@Composable
private fun FormPriceField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = modifier,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryTeal,
            focusedLabelColor = PrimaryTeal,
            cursorColor = PrimaryTeal
        )
    )
}

@Composable
private fun FormNumberField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryTeal,
            focusedLabelColor = PrimaryTeal,
            cursorColor = PrimaryTeal
        )
    )
}
