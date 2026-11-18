package com.example.inmobi.features.properties.ui.detail

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
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.RealEstateAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inmobi.core.ui.theme.PrimaryTeal
import com.example.inmobi.features.properties.ui.common.ErrorState
import com.example.inmobi.features.properties.ui.common.LoadingState

@Composable
fun PropertyDetailScreen(
    propertyId: Int,
    onBack: () -> Unit = {},
    onEdit: (Int) -> Unit = {},
    onDelete: (Int) -> Unit = {},
    viewModel: PropertyDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(propertyId) {
        viewModel.loadProperty(propertyId)
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
                "Detalle de propiedad",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
        }

        when {
            uiState.isLoading -> LoadingState()
            uiState.error != null -> ErrorState(uiState.error!!) { viewModel.loadProperty(propertyId) }
            uiState.property != null -> {
                val property = uiState.property!!

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(colorScheme.surfaceVariant)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Outlined.RealEstateAgent,
                            null,
                            tint = PrimaryTeal.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }

                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Text(
                        property.listingTitle,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colorScheme.onSurface
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        formatPrice(property.price, property.currency, property.operationName),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryTeal
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.LocationOn, null, tint = PrimaryTeal, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${property.city}, ${property.stateProvince}, ${property.country}",
                            fontSize = 13.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    val specs = buildList {
                        property.bedrooms?.let { add(Spec(Icons.Default.Bed, "$it hab")) }
                        val baths = (property.fullBathrooms ?: 0) + (property.halfBathrooms ?: 0) * 0.5
                        if (baths > 0) {
                            add(Spec(Icons.Default.Bathtub, formatBaths(baths)))
                        }
                        property.builtAreaSqm?.let { add(Spec(Icons.Default.SquareFoot, "${it.toInt()} m2")) }
                        property.parkingSpaces?.let { add(Spec(Icons.Default.DirectionsCar, "$it est.")) }
                    }

                    if (specs.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            specs.take(4).forEach { spec ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .background(colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(spec.icon, null, tint = PrimaryTeal, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text(spec.label, fontSize = 11.sp, color = colorScheme.onSurface)
                                }
                            }
                        }
                        Spacer(Modifier.height(20.dp))
                    }

                    if (property.description != null && property.description.isNotBlank()) {
                        Text("Descripcion", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            property.description,
                            fontSize = 13.sp,
                            color = colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                        Spacer(Modifier.height(20.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal, contentColor = Color.White)
                        ) {
                            Text(
                                if (property.operationName == "rent") "Agendar visita" else "Contactar",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        OutlinedButton(
                            onClick = { onEdit(property.id) },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Editar", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

private data class Spec(val icon: androidx.compose.ui.graphics.vector.ImageVector, val label: String)

private fun formatPrice(price: Double, currency: String, operationName: String): String {
    val formatted = if (price == price.toLong().toDouble()) {
        String.format("%,d", price.toLong())
    } else {
        String.format("%,.2f", price)
    }.replace(",", "X").replace(".", ",").replace("X", ".")

    val suffix = when (operationName) {
        "rent" -> " / mes"
        else -> ""
    }

    return "$$formatted $currency$suffix"
}

private fun formatBaths(baths: Double): String {
    return if (baths == baths.toLong().toDouble()) {
        "${baths.toLong()} banos"
    } else {
        "%.1f banos".format(baths)
    }
}
