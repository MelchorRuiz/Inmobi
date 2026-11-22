package com.example.inmobi.features.properties.ui.calculator

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Percent
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inmobi.core.ui.theme.PrimaryTeal
import com.example.inmobi.features.properties.data.PropertiesRepository
import com.example.inmobi.features.properties.data.Result
import com.example.inmobi.features.properties.domain.Property
import com.example.inmobi.features.properties.domain.toDomain
import com.example.inmobi.features.properties.ui.common.formatPrice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CalculatorUiState(
    val isLoading: Boolean = true,
    val property: Property? = null,
    val error: String? = null
)

class PropertyCalculatorViewModel : ViewModel() {
    private val repository = PropertiesRepository()
    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    fun loadProperty(propertyId: Int) {
        _uiState.value = CalculatorUiState(isLoading = true)
        viewModelScope.launch {
            when (val result = repository.getProperty(propertyId)) {
                is Result.Success -> _uiState.value = CalculatorUiState(
                    isLoading = false,
                    property = result.data.toDomain()
                )
                is Result.Error -> _uiState.value = CalculatorUiState(
                    isLoading = false,
                    error = "Error al cargar: ${result.message}"
                )
                is Result.NetworkError -> _uiState.value = CalculatorUiState(
                    isLoading = false,
                    error = "Error de conexión"
                )
            }
        }
    }
}

@Composable
fun PropertyCalculatorScreen(
    propertyId: Int,
    onBack: () -> Unit,
    viewModel: PropertyCalculatorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(propertyId) {
        viewModel.loadProperty(propertyId)
    }

    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
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
            Icon(Icons.Filled.Calculate, null, tint = PrimaryTeal, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                "Calculadora de costos",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
        }

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Cargando...", color = colorScheme.onSurfaceVariant)
                }
            }
            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(uiState.error!!, color = colorScheme.error)
                }
            }
            uiState.property != null -> {
                val property = uiState.property!!
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    if (property.operationName == "rent") {
                        RentCalculator(property)
                    } else {
                        SaleCalculator(property)
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun SaleCalculator(property: Property) {
    val colorScheme = MaterialTheme.colorScheme
    val price = property.price
    val currency = property.currency
    val state = property.stateProvince

    val defaultIsai = getIsaiRateForState(state)

    var downPaymentPct by remember { mutableFloatStateOf(20f) }
    var openingCommissionPct by remember { mutableFloatStateOf(0f) }
    var isaiPct by remember { mutableFloatStateOf(defaultIsai.toFloat()) }
    var notaryPct by remember { mutableFloatStateOf(2f) }
    var appraisalValue by remember { mutableIntStateOf(5000) }

    val downPayment = price * (downPaymentPct / 100)
    val isaiAmount = price * (isaiPct / 100)
    val notaryAmount = price * (notaryPct / 100)
    val openingCommissionAmount = price * (openingCommissionPct / 100)
    val appraisal = appraisalValue.toDouble()

    val cashNeeded = downPayment + isaiAmount + notaryAmount + openingCommissionAmount + appraisal
    val totalCost = price + isaiAmount + notaryAmount + openingCommissionAmount + appraisal

    Text(
        "Compra de inmueble",
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold,
        color = colorScheme.onSurface
    )
    Spacer(Modifier.height(4.dp))
    Text(
        "Precio publicado: ${formatPrice(price, currency, "")}",
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = PrimaryTeal
    )
    Spacer(Modifier.height(16.dp))

    Text("Parámetros", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
    Spacer(Modifier.height(12.dp))

    SliderRow(
        icon = Icons.Outlined.Payments,
        label = "Enganche",
        value = downPaymentPct,
        onValueChange = { downPaymentPct = it },
        range = 10f..50f,
        suffix = "%",
        amountText = formatPrice(downPayment, currency, "")
    )

    SliderRow(
        icon = Icons.Outlined.Percent,
        label = "ISAI (${state})",
        value = isaiPct,
        onValueChange = { isaiPct = it },
        range = 0f..10f,
        suffix = "%",
        amountText = formatPrice(isaiAmount, currency, "")
    )

    SliderRow(
        icon = Icons.Outlined.AccountBalance,
        label = "Honorarios notario",
        value = notaryPct,
        onValueChange = { notaryPct = it },
        range = 1f..5f,
        suffix = "%",
        amountText = formatPrice(notaryAmount, currency, "")
    )

    SliderRow(
        icon = Icons.Outlined.Payments,
        label = "Comisión apertura crédito",
        value = openingCommissionPct,
        onValueChange = { openingCommissionPct = it },
        range = 0f..5f,
        suffix = "%",
        amountText = formatPrice(openingCommissionAmount, currency, "")
    )

    SliderRow(
        icon = Icons.Outlined.Percent,
        label = "Avalúo",
        value = appraisalValue.toFloat(),
        onValueChange = { appraisalValue = it.toInt() },
        range = 0f..20000f,
        suffix = "",
        amountText = formatPrice(appraisal, currency, ""),
        step = 500f
    )

    Spacer(Modifier.height(20.dp))

    ResultBlock(
        title = "Efectivo inmediato necesario",
        subtitle = "De tu bolsillo",
        amount = cashNeeded,
        currency = currency,
        color = Color(0xFF1565C0),
        items = listOf(
            "Enganche (${downPaymentPct.toInt()}%)" to downPayment,
            "ISAI (${String.format("%.1f", isaiPct)}%)" to isaiAmount,
            "Notario (${String.format("%.1f", notaryPct)}%)" to notaryAmount,
            "Comisión apertura" to openingCommissionAmount,
            "Avalúo" to appraisal
        )
    )

    Spacer(Modifier.height(12.dp))

    ResultBlock(
        title = "Costo total de la operación",
        subtitle = "Precio + gastos",
        amount = totalCost,
        currency = currency,
        color = PrimaryTeal,
        items = listOf(
            "Precio del inmueble" to price,
            "Gastos adicionales" to (totalCost - price)
        )
    )
}

@Composable
private fun RentCalculator(property: Property) {
    val colorScheme = MaterialTheme.colorScheme
    val rent = property.price
    val currency = property.currency
    val maintenance = property.maintenanceFee ?: 0.0
    val bedrooms = property.bedrooms ?: 1

    var depositMonths by remember { mutableFloatStateOf(1f) }
    var policyPct by remember { mutableFloatStateOf(35f) }
    var policyPayer by remember { mutableIntStateOf(0) }
    var bureauCost by remember { mutableIntStateOf(800) }
    var servicesCost by remember { mutableIntStateOf(defaultServices(bedrooms)) }
    var maintenanceInput by remember { mutableIntStateOf(maintenance.toInt()) }

    val depositAmount = rent * depositMonths
    val policyAmount = rent * (policyPct / 100)
    val policyTenantShare: Double = when (policyPayer) {
        0 -> policyAmount
        1 -> policyAmount * 0.5
        else -> 0.0
    }
    val day1Total = rent + depositAmount + policyTenantShare + bureauCost.toDouble()
    val monthlyTotal = rent + maintenanceInput.toDouble() + servicesCost.toDouble()
    val requiredIncome = monthlyTotal * 3

    Text(
        "Renta de inmueble",
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold,
        color = colorScheme.onSurface
    )
    Spacer(Modifier.height(4.dp))
    Text(
        "Renta mensual: ${formatPrice(rent, currency, "")}",
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = PrimaryTeal
    )
    Spacer(Modifier.height(16.dp))

    Text(
        "A. Efectivo para entrar (Día 1)",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = colorScheme.onSurface
    )
    Spacer(Modifier.height(12.dp))

    SliderRow(
        icon = Icons.Outlined.Key,
        label = "Primer mes adelantado",
        value = 1f,
        onValueChange = {},
        range = 1f..1f,
        suffix = " mes",
        amountText = formatPrice(rent, currency, ""),
        enabled = false
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Payments, null, tint = PrimaryTeal, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Depósito en garantía", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colorScheme.onSurface)
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${String.format("%.0f", depositMonths)} ${if (depositMonths == 1f) "mes" else "meses"}",
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariant
                )
                Text(
                    formatPrice(depositAmount, currency, ""),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTeal
                )
            }
            Slider(
                value = depositMonths,
                onValueChange = { depositMonths = it },
                valueRange = 1f..2f,
                steps = 1,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = PrimaryTeal,
                    activeTrackColor = PrimaryTeal,
                    inactiveTrackColor = colorScheme.surfaceVariant
                )
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.Info,
                    null,
                    tint = colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    "Reembolsable al finalizar el contrato",
                    fontSize = 10.sp,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }
    }
    Spacer(Modifier.height(12.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.AccountBalance, null, tint = PrimaryTeal, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Póliza jurídica", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colorScheme.onSurface)
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${String.format("%.0f", policyPct)}% de 1 mes",
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariant
                )
                Text(
                    formatPrice(policyAmount, currency, ""),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTeal
                )
            }
            Slider(
                value = policyPct,
                onValueChange = { policyPct = it },
                valueRange = 25f..50f,
                steps = 4,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = PrimaryTeal,
                    activeTrackColor = PrimaryTeal,
                    inactiveTrackColor = colorScheme.surfaceVariant
                )
            )
            Spacer(Modifier.height(4.dp))
            Text("¿Quién paga?", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PayerChip("Inquilino 100%", policyPayer == 0) { policyPayer = 0 }
                PayerChip("50/50", policyPayer == 1) { policyPayer = 1 }
                PayerChip("Propietario 100%", policyPayer == 2) { policyPayer = 2 }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                "Tu parte: ${formatPrice(policyTenantShare, currency, "")}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryTeal
            )
        }
    }
    Spacer(Modifier.height(12.dp))

    EditableAmountRow(
        icon = Icons.Outlined.Info,
        label = "Investigación buró de crédito",
        amount = bureauCost.toDouble(),
        currency = currency,
        onAmountChange = { bureauCost = it.toInt() }
    )

    Spacer(Modifier.height(20.dp))

    ResultBlock(
        title = "Total para entrar",
        subtitle = "Día 1",
        amount = day1Total,
        currency = currency,
        color = Color(0xFF1565C0),
        items = listOf(
            "1er mes adelantado" to rent,
            "Depósito (${String.format("%.0f", depositMonths)} meses)" to depositAmount,
            "Póliza jurídica (tu parte)" to policyTenantShare,
            "Investigación buró" to bureauCost.toDouble()
        )
    )

    Spacer(Modifier.height(20.dp))

    Text(
        "B. Costo mensual real",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = colorScheme.onSurface
    )
    Spacer(Modifier.height(12.dp))

    EditableAmountRow(
        icon = Icons.Outlined.Payments,
        label = "Renta base",
        amount = rent,
        currency = currency,
        onAmountChange = {},
        enabled = false
    )

    EditableAmountRow(
        icon = Icons.Outlined.AccountBalance,
        label = "Mantenimiento",
        amount = maintenanceInput.toDouble(),
        currency = currency,
        onAmountChange = { maintenanceInput = it.toInt() }
    )

    EditableAmountRow(
        icon = Icons.Outlined.Info,
        label = "Servicios estimados (luz, agua, gas, internet)",
        amount = servicesCost.toDouble(),
        currency = currency,
        onAmountChange = { servicesCost = it.toInt() }
    )

    Spacer(Modifier.height(16.dp))

    ResultBlock(
        title = "Gasto mensual total",
        subtitle = "Renta + fijos",
        amount = monthlyTotal,
        currency = currency,
        color = PrimaryTeal,
        items = listOf(
            "Renta base" to rent,
            "Mantenimiento" to maintenanceInput.toDouble(),
            "Servicios" to servicesCost.toDouble()
        )
    )

    Spacer(Modifier.height(12.dp))

    SolvencyIndicator(
        requiredIncome = requiredIncome,
        currency = currency
    )
}

@Composable
private fun SliderRow(
    icon: ImageVector,
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>,
    suffix: String,
    amountText: String,
    enabled: Boolean = true,
    step: Float = 0f
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = PrimaryTeal, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colorScheme.onSurface)
                    if (suffix.isNotEmpty()) {
                        Text(
                            "${String.format(if (value == value.toLong().toFloat()) "%.0f" else "%.1f", value)}$suffix",
                            fontSize = 11.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    amountText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTeal
                )
            }
            if (enabled) {
                Slider(
                    value = value,
                    onValueChange = onValueChange,
                    valueRange = range,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryTeal,
                        activeTrackColor = PrimaryTeal,
                        inactiveTrackColor = colorScheme.surfaceVariant
                    )
                )
            }
        }
    }
}

@Composable
private fun EditableAmountRow(
    icon: ImageVector,
    label: String,
    amount: Double,
    currency: String,
    onAmountChange: (Double) -> Unit,
    enabled: Boolean = true
) {
    val colorScheme = MaterialTheme.colorScheme
    var inputText by remember(amount) { mutableFloatStateOf(amount.toFloat()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = PrimaryTeal, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colorScheme.onSurface)
                if (enabled) {
                    Text(
                        "Ajustable",
                        fontSize = 10.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
            if (enabled) {
                androidx.compose.material3.TextField(
                    value = if (inputText == inputText.toLong().toFloat()) "${inputText.toLong()}" else "${inputText}",
                    onValueChange = { text ->
                        text.toDoubleOrNull()?.let {
                            inputText = it.toFloat()
                            onAmountChange(it)
                        }
                    },
                    modifier = Modifier.width(110.dp),
                    singleLine = true,
                    suffix = { Text(" $currency", fontSize = 11.sp) },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTeal
                    )
                )
            } else {
                Text(
                    formatPrice(amount, currency, ""),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTeal
                )
            }
        }
    }
}

@Composable
private fun ResultBlock(
    title: String,
    subtitle: String,
    amount: Double,
    currency: String,
    color: Color,
    items: List<Pair<String, Double>>
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                subtitle,
                fontSize = 11.sp,
                color = colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Text(
                formatPrice(amount, currency, ""),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Spacer(Modifier.height(12.dp))
            items.forEach { (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        label,
                        fontSize = 12.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                    Text(
                        formatPrice(value, currency, ""),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun PayerChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) PrimaryTeal else colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) Color.White else colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun SolvencyIndicator(
    requiredIncome: Double,
    currency: String
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Outlined.Info,
                null,
                tint = Color(0xFFE65100),
                modifier = Modifier.size(18.dp).padding(top = 2.dp)
            )
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    "Requisito de solvencia",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE65100)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "La mayoría de propietarios piden comprobar ingresos netos de al menos ${formatPrice(requiredIncome, currency, "")} (relación 3:1 respecto al gasto mensual).",
                    fontSize = 11.sp,
                    color = Color(0xFFBF360C),
                    lineHeight = 15.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Esto te ahorra tiempo y rechazos.",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFE65100)
                )
            }
        }
    }
}

private fun defaultServices(bedrooms: Int): Int {
    return when {
        bedrooms <= 1 -> 1200
        bedrooms <= 2 -> 1500
        bedrooms <= 3 -> 1800
        else -> 2200
    }
}
