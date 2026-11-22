package com.example.inmobi.features.properties.ui.manage

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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.RealEstateAgent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.inmobi.core.ui.theme.PrimaryTeal
import com.example.inmobi.features.properties.domain.ListingStatus
import com.example.inmobi.features.properties.ui.common.ErrorState
import com.example.inmobi.features.properties.ui.common.LoadingState
import com.example.inmobi.features.properties.ui.common.PriceEstimationBlock
import com.example.inmobi.features.properties.ui.common.formatPrice

@Composable
fun ManagePropertyScreen(
    propertyId: Int,
    onBack: () -> Unit = {},
    onEdit: (Int) -> Unit = {},
    viewModel: ManagePropertyViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colorScheme = MaterialTheme.colorScheme
    var showCancelDialog by remember { mutableStateOf(false) }
    var showCloseDialog by remember { mutableStateOf(false) }
    val isSale = uiState.property?.operationName == "sale"

    LaunchedEffect(propertyId) {
        viewModel.loadProperty(propertyId)
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collect { event ->
            when (event) {
                is ManagePropertyEvent.NavigateBack -> onBack()
            }
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancelar propiedad") },
            text = { Text("Deseas cancelar esta propiedad? Dejara de ser visible para los demas usuarios.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteProperty(ListingStatus.CANCELLED)
                        showCancelDialog = false
                    }
                ) {
                    Text("Si, cancelar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    if (showCloseDialog) {
        AlertDialog(
            onDismissRequest = { showCloseDialog = false },
            title = { Text(if (isSale) "Marcar como vendida" else "Marcar como rentada") },
            text = { Text(if (isSale) "Deseas marcar esta propiedad como vendida? Ya no estara disponible." else "Deseas marcar esta propiedad como rentada? Ya no estara disponible.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteProperty(ListingStatus.CLOSED)
                        showCloseDialog = false
                    }
                ) {
                    Text("Si, confirmar", color = PrimaryTeal)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCloseDialog = false }) {
                    Text("No")
                }
            }
        )
    }

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
            Text(
                "Gestionar propiedad",
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

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    val images = property.images
                    val pagerState = rememberPagerState(pageCount = { images.size.coerceAtLeast(1) })

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .background(colorScheme.surfaceVariant)
                    ) {
                        if (images.isNotEmpty()) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { page ->
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(images[page].url)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = property.listingTitle,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            if (images.size > 1) {
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    repeat(images.size) { index ->
                                        Box(
                                            modifier = Modifier
                                                .size(if (index == pagerState.currentPage) 8.dp else 6.dp)
                                                .background(
                                                    if (index == pagerState.currentPage) Color.White else Color.White.copy(alpha = 0.5f),
                                                    CircleShape
                                                )
                                        )
                                    }
                                }
                            }
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Outlined.RealEstateAgent,
                                    null,
                                    tint = PrimaryTeal.copy(alpha = 0.4f),
                                    modifier = Modifier.size(64.dp)
                                )
                            }
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
                        if (property.priceEstimation != null) {
                            Spacer(Modifier.height(12.dp))
                            PriceEstimationBlock(
                                priceEstimation = property.priceEstimation,
                                listingPrice = property.price,
                                currency = property.currency,
                                operationName = property.operationName
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.LocationOn, null, tint = PrimaryTeal, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "${property.city}, ${property.stateProvince}",
                                fontSize = 13.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(Modifier.height(20.dp))

                        val specs = buildList {
                            property.bedrooms?.let { add(Spec(Icons.Default.Bed, "$it hab")) }
                            val baths = (property.fullBathrooms ?: 0) + (property.halfBathrooms ?: 0) * 0.5
                            if (baths > 0) add(Spec(Icons.Default.Bathtub, formatBaths(baths)))
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
                            Spacer(Modifier.height(24.dp))
                        }

                        if (property.listingStatus == "active") {
                            Button(
                                onClick = { onEdit(property.id) },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal, contentColor = Color.White)
                            ) {
                                Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Editar propiedad", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = { showCloseDialog = true },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryTeal)
                            ) {
                                Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(if (isSale) "Ya vendida" else "Ya rentada", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = { showCancelDialog = true },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                            ) {
                                Icon(Icons.Default.Cancel, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Cancelar propiedad", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = { viewModel.reactivateProperty() },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal, contentColor = Color.White)
                            ) {
                                Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Reactivar propiedad", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

private data class Spec(val icon: ImageVector, val label: String)

private fun formatBaths(baths: Double): String {
    return if (baths == baths.toLong().toDouble()) {
        "${baths.toLong()} banos"
    } else {
        "%.1f banos".format(baths)
    }
}
