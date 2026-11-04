package com.example.inmobi.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleVisitDialog(
    propertyName: String,
    onDismiss: () -> Unit,
    onConfirm: (dateStr: String, timeStr: String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(System.currentTimeMillis()) }

    // Estado del selector de hora de Material 3
    val timePickerState = rememberTimePickerState(
        initialHour = 11,
        initialMinute = 30,
        is24Hour = false
    )

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val formattedDate = selectedDateMillis?.let { dateFormatter.format(Date(it)) } ?: "Seleccionar fecha"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Agendar Visita",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Coordina una visita con el arrendador para: $propertyName",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )

                // Botón disparador del selector de Fecha
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryTeal)
                ) {
                    Icon(imageVector = Icons.Outlined.CalendarMonth, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Fecha: $formattedDate", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                }

                Text(
                    text = "Selecciona la hora:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Selector de hora integrado
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TimePicker(state = timePickerState)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val dateStr = formattedDate
                    val hour = timePickerState.hour
                    val minute = timePickerState.minute
                    val amPm = if (hour < 12) "AM" else "PM"
                    val formattedHour = if (hour % 12 == 0) 12 else hour % 12
                    val timeStr = String.format("%02d:%02d %s", formattedHour, minute, amPm)

                    onConfirm(dateStr, timeStr)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "Confirmar Cita", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )

    // Diálogo nativo de calendario Material 3
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDateMillis = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) {
                    Text("Aceptar", color = PrimaryTeal, fontWeight = FontWeight.Bold)
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
}