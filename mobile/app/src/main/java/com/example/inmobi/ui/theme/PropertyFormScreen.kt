package com.example.inmobi.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AddHomeWork
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.RoomPreferences
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyFormScreen(
    isEditing: Boolean = false,
    initialTitle: String = "",
    initialPrice: String = "",
    onBack: () -> Unit = {},
    onSubmitSuccess: () -> Unit = {},
    onDeleteProperty: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme

    // Estados vinculados a la entidad tblinmuebles
    var titulo by remember { mutableStateOf(initialTitle) }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf(initialPrice) }
    var costoMantenimiento by remember { mutableStateOf("") }
    var calleNumero by remember { mutableStateOf("") }
    var colonia by remember { mutableStateOf("") }
    var codigoPostal by remember { mutableStateOf("") }
    var superficieTerreno by remember { mutableStateOf("") }
    var superficieConstruida by remember { mutableStateOf("") }
    var recamaras by remember { mutableStateOf("") }
    var banos by remember { mutableStateOf("") }
    var estacionamientos by remember { mutableStateOf("") }

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
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Regresar",
                                tint = colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isEditing) "Editar Propiedad" else "Publicar Propiedad",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                    }

                    // Botón de eliminar rápido en la barra superior si está editando
                    if (isEditing) {
                        IconButton(onClick = onDeleteProperty) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Eliminar propiedad",
                                tint = Color(0xFFEF4444)
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
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección 1: Información General
            FormSectionTitle(title = "Información General", icon = Icons.Outlined.AddHomeWork)

            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título de la publicación") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = formInputColors(),
                singleLine = true
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción detallada") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = formInputColors(),
                minLines = 3,
                maxLines = 4
            )

            // Sección 2: Precios y Costos
            FormSectionTitle(title = "Precios y Costos", icon = Icons.Outlined.AttachMoney)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it },
                    label = { Text("Precio ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = formInputColors(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = costoMantenimiento,
                    onValueChange = { costoMantenimiento = it },
                    label = { Text("Mantenimiento ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = formInputColors(),
                    singleLine = true
                )
            }

            // Sección 3: Ubicación
            FormSectionTitle(title = "Ubicación del Inmueble", icon = Icons.Outlined.LocationOn)

            OutlinedTextField(
                value = calleNumero,
                onValueChange = { calleNumero = it },
                label = { Text("Calle y Número") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = formInputColors(),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = colonia,
                    onValueChange = { colonia = it },
                    label = { Text("Colonia / Barrio") },
                    modifier = Modifier.weight(1.3f),
                    shape = RoundedCornerShape(12.dp),
                    colors = formInputColors(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = codigoPostal,
                    onValueChange = { codigoPostal = it },
                    label = { Text("C.P.") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(0.7f),
                    shape = RoundedCornerShape(12.dp),
                    colors = formInputColors(),
                    singleLine = true
                )
            }

            // Sección 4: Características Físicas y Medidas
            FormSectionTitle(title = "Características y Medidas", icon = Icons.Outlined.RoomPreferences)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = superficieTerreno,
                    onValueChange = { superficieTerreno = it },
                    label = { Text("Terreno (m²)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = formInputColors(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = superficieConstruida,
                    onValueChange = { superficieConstruida = it },
                    label = { Text("Construcción (m²)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = formInputColors(),
                    singleLine = true
                )
            }

            // Fila de 3 elementos perfectamente balanceada con weight
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = recamaras,
                    onValueChange = { recamaras = it },
                    label = { Text("Recámaras") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = formInputColors(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = banos,
                    onValueChange = { banos = it },
                    label = { Text("Baños") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = formInputColors(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = estacionamientos,
                    onValueChange = { estacionamientos = it },
                    label = { Text("Cochera") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = formInputColors(),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Principal (Guardar / Publicar / Actualizar)
            Button(
                onClick = onSubmitSuccess,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
            ) {
                Text(
                    text = if (isEditing) "Actualizar Propiedad" else "Publicar Propiedad",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Botón de Eliminar (Visible únicamente si se encuentra en modo edición)
            if (isEditing) {
                OutlinedButton(
                    onClick = onDeleteProperty,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Eliminar propiedad del registro",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FormSectionTitle(title: String, icon: ImageVector) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryTeal,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface
        )
    }
}

@Composable
fun formInputColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = PrimaryTeal,
    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
    focusedLabelColor = PrimaryTeal,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    focusedContainerColor = MaterialTheme.colorScheme.surface
)