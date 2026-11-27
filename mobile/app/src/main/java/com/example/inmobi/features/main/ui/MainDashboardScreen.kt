package com.example.inmobi.features.main.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.HomeWork
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inmobi.core.ui.theme.DarkTeal
import com.example.inmobi.core.ui.theme.PrimaryTeal
import com.example.inmobi.features.properties.domain.ListingStatus
import com.example.inmobi.features.properties.domain.Property
import com.example.inmobi.features.properties.ui.common.ErrorState
import com.example.inmobi.features.properties.ui.common.LoadingState
import com.example.inmobi.features.properties.ui.common.ManagePropertyCard
import com.example.inmobi.features.properties.ui.common.deviationLabel

@Composable
fun MainDashboardScreen(
    onInterestedPropertyClick: (Int) -> Unit = {},
    onPublishedPropertyClick: (Int) -> Unit = {},
    onNavigateToLegalAdvice: () -> Unit = {},
    viewModel: MainDashboardViewModel = viewModel(factory = MainDashboardViewModel.Factory())
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        viewModel.loadDashboard()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Mi Dashboard",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Tu resumen de propiedades",
            fontSize = 13.sp,
            color = colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(20.dp))

        LegalAdviceBanner(onClick = onNavigateToLegalAdvice)
        Spacer(modifier = Modifier.height(20.dp))

        when {
            uiState.isLoading -> LoadingState()
            uiState.error != null -> ErrorState(uiState.error!!) { viewModel.loadDashboard() }
            else -> {
                val hasInterested = uiState.interestedProperties.isNotEmpty()
                val hasPublished = uiState.publishedProperties.isNotEmpty()

                if (!hasInterested && !hasPublished) {
                    EmptyDashboardMessage()
                } else {
                    if (hasInterested) {
                        DashboardSection(
                            title = "Propiedades que te interesan",
                            icon = Icons.Outlined.Favorite,
                            properties = uiState.interestedProperties,
                            cardConfig = { property ->
                                InterestedPropertyCardConfig(property)
                            },
                            onPropertyClick = onInterestedPropertyClick
                        )
                    }

                    if (hasPublished) {
                        if (hasInterested) {
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        DashboardSection(
                            title = "Tus propiedades publicadas",
                            icon = Icons.Outlined.HomeWork,
                            properties = uiState.publishedProperties,
                            cardConfig = { property ->
                                PublishedPropertyCardConfig(property)
                            },
                            onPropertyClick = onPublishedPropertyClick
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun EmptyDashboardMessage() {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.HomeWork,
                contentDescription = null,
                tint = PrimaryTeal,
                modifier = Modifier
                    .size(80.dp)
                    .shadow(4.dp, shape = androidx.compose.foundation.shape.CircleShape)
                    .background(colorScheme.surfaceVariant, androidx.compose.foundation.shape.CircleShape)
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Tu dashboard está vacío",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Comienza a explorar propiedades, muestra interés en las que te gusten y publica las tuyas para gestionarlas desde aquí.",
                fontSize = 14.sp,
                color = colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun DashboardSection(
    title: String,
    icon: ImageVector,
    properties: List<Property>,
    cardConfig: (Property) -> PropertyCardConfig,
    onPropertyClick: (Int) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Column {
        androidx.compose.foundation.layout.Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(icon, null, tint = PrimaryTeal)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(end = 20.dp)
        ) {
            items(properties) { property ->
                val config = cardConfig(property)
                ManagePropertyCard(
                    property = property,
                    statusText = config.statusText,
                    statusColor = config.statusColor,
                    statusBgColor = config.statusBgColor,
                    statText = config.statText,
                    actionText = config.actionText,
                    onClick = { onPropertyClick(property.id) },
                    modifier = Modifier.width(220.dp)
                )
            }
        }
    }
}

private data class PropertyCardConfig(
    val statusText: String,
    val statusColor: Color,
    val statusBgColor: Color,
    val statText: String,
    val actionText: String
)

private fun InterestedPropertyCardConfig(property: Property): PropertyCardConfig {
    val operationText = when (property.operationName.lowercase()) {
        "sale" -> "Venta"
        "rent" -> "Renta"
        else -> property.operationName
    }
    return PropertyCardConfig(
        statusText = operationText,
        statusColor = PrimaryTeal,
        statusBgColor = Color(0xFFEBF5F4),
        statText = "${property.city}, ${property.stateProvince}",
        actionText = "Ver detalle"
    )
}

private fun PublishedPropertyCardConfig(property: Property): PropertyCardConfig {
    return when (property.listingStatus) {
        ListingStatus.ACTIVE.apiName -> {
            val statText = property.priceEstimation?.let {
                deviationLabel(property.price, it.estimatedPrice)?.label
            } ?: "Precio justo IA"
            PropertyCardConfig(
                statusText = "Publicada",
                statusColor = PrimaryTeal,
                statusBgColor = Color(0xFFEBF5F4),
                statText = statText,
                actionText = "Gestionar"
            )
        }
        ListingStatus.CANCELLED.apiName -> PropertyCardConfig(
            statusText = "Pausada",
            statusColor = Color(0xFFD97706),
            statusBgColor = Color(0xFFFEF3C7),
            statText = "Cancelada",
            actionText = "Reactivar"
        )
        ListingStatus.CLOSED.apiName -> PropertyCardConfig(
            statusText = "Cerrada",
            statusColor = Color(0xFF64748B),
            statusBgColor = Color(0xFFF1F5F9),
            statText = "Operación cerrada",
            actionText = "Ver"
        )
        else -> PropertyCardConfig(
            statusText = "Borrador",
            statusColor = Color(0xFF64748B),
            statusBgColor = Color(0xFFF1F5F9),
            statText = "Completa para publicar",
            actionText = "Continuar"
        )
    }
}

@Composable
private fun LegalAdviceBanner(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryTeal)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                        colors = listOf(PrimaryTeal, DarkTeal)
                    )
                )
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Gavel,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "¿Necesitas asesoría legal?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Solicita asesoramiento sobre tus propiedades de interés",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    lineHeight = 14.sp
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                null,
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
