package com.example.inmobi.features.properties.ui.sell

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inmobi.core.ui.theme.PrimaryTeal
import com.example.inmobi.features.properties.domain.ListingStatus
import com.example.inmobi.features.properties.domain.Property
import com.example.inmobi.features.properties.ui.common.EmptyState
import com.example.inmobi.features.properties.ui.common.ErrorState
import com.example.inmobi.features.properties.ui.common.LoadingState
import com.example.inmobi.features.properties.ui.common.ManagePropertyCard
import com.example.inmobi.features.properties.ui.common.MetricCard

@Composable
fun SellPropertiesScreen(
    onCreateProperty: () -> Unit = {},
    onPropertyClick: (Int) -> Unit = {},
    refreshKey: Int = 0,
    viewModel: SellPropertiesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(refreshKey) {
        viewModel.loadProperties()
    }

    val activeProperties = uiState.properties.filter { it.listingStatus == ListingStatus.ACTIVE.apiName }
    val totalValue = activeProperties.sumOf { it.price }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Surface(color = colorScheme.surfaceVariant, shape = RoundedCornerShape(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Outlined.Sell, null, tint = PrimaryTeal, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
                Text("Mis ventas", color = PrimaryTeal, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(12.dp))
        Text("Propiedades en venta", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = colorScheme.onSurface)
        Spacer(Modifier.height(4.dp))
        Text("Administra tus publicaciones activas, pausadas y borradores con el valor justo certificado por la IA de Inmobi.", fontSize = 12.sp, color = colorScheme.onSurfaceVariant, lineHeight = 16.sp)
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onCreateProperty,
            modifier = Modifier.fillMaxWidth().height(46.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal, contentColor = Color.White)
        ) {
            Icon(Icons.Default.AddBusiness, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Publicar propiedad", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(18.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard(Modifier.width(110.dp), Icons.Outlined.Sell, "${activeProperties.size}", "En venta activas")
            MetricCard(Modifier.width(110.dp), Icons.Outlined.Visibility, "${uiState.properties.size}", "Total publicadas")
            MetricCard(Modifier.width(110.dp), Icons.Outlined.AccountBalanceWallet, formatCurrency(totalValue), "Valor total publicado")
        }

        Spacer(Modifier.height(20.dp))

        when {
            uiState.isLoading -> LoadingState()
            uiState.error != null -> ErrorState(uiState.error!!) { viewModel.loadProperties() }
            uiState.properties.isEmpty() -> EmptyState("No tienes propiedades en venta")
            else -> {
                uiState.properties.forEach { property ->
                    val (statusText, statusColor, statusBgColor, statText, actionText) = getStatusInfo(property)
                    ManagePropertyCard(
                        property = property,
                        statusText = statusText,
                        statusColor = statusColor,
                        statusBgColor = statusBgColor,
                        statText = statText,
                        actionText = actionText,
                        onClick = { onPropertyClick(property.id) }
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Text("2026 Inmobi. Todos los derechos reservados.", fontSize = 11.sp, color = colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(24.dp))
    }
}

private fun getStatusInfo(property: Property): StatusInfo {
    return when (property.listingStatus) {
        "active" -> StatusInfo(
            statusText = "Publicada",
            statusColor = PrimaryTeal,
            statusBgColor = Color(0xFFEBF5F4),
            statText = "Precio justo IA",
            actionText = "Editar"
        )
        "cancelled" -> StatusInfo(
            statusText = "Pausada",
            statusColor = Color(0xFFD97706),
            statusBgColor = Color(0xFFFEF3C7),
            statText = "Cancelada",
            actionText = "Reactivar"
        )
        "closed" -> StatusInfo(
            statusText = "Cerrada",
            statusColor = Color(0xFF64748B),
            statusBgColor = Color(0xFFF1F5F9),
            statText = "Venta cerrada",
            actionText = "Ver"
        )
        else -> StatusInfo(
            statusText = "Borrador",
            statusColor = Color(0xFF64748B),
            statusBgColor = Color(0xFFF1F5F9),
            statText = "Completa para publicar",
            actionText = "Continuar"
        )
    }
}

private data class StatusInfo(
    val statusText: String,
    val statusColor: Color,
    val statusBgColor: Color,
    val statText: String,
    val actionText: String
)

private fun formatCurrency(value: Double): String {
    return when {
        value >= 1_000_000 -> "$${(value / 1_000_000).toInt()}M"
        value >= 1_000 -> "$${(value / 1_000).toInt()}K"
        else -> "$${value.toInt()}"
    }
}
