package com.example.inmobi.features.properties.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Height
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.outlined.RealEstateAgent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.inmobi.core.network.NetworkModule
import com.example.inmobi.core.ui.theme.PrimaryTeal
import com.example.inmobi.features.auth.ui.MainViewModel
import com.example.inmobi.features.properties.ui.common.ErrorState
import com.example.inmobi.features.properties.ui.common.LoadingState
import com.example.inmobi.features.properties.ui.common.PriceEstimationBlock
import com.example.inmobi.features.properties.ui.common.formatPrice

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PropertyDetailScreen(
    propertyId: Int,
    onBack: () -> Unit = {},
    onDelete: (Int) -> Unit = {},
    onNavigateToJourney: (Int) -> Unit = {},
    viewModel: PropertyDetailViewModel = viewModel(),
    mainViewModel: MainViewModel = viewModel(
        factory = MainViewModel.Factory(NetworkModule.authRepository)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val mainState by mainViewModel.state.collectAsStateWithLifecycle()
    val colorScheme = MaterialTheme.colorScheme

    val isOwner = uiState.property?.let { mainState.user?.id?.toInt() == it.ownerUserId } ?: false

    LaunchedEffect(propertyId) {
        viewModel.loadProperty(propertyId)
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
                "Detalle de propiedad",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {

        when {
            uiState.isLoading -> LoadingState()
            uiState.error != null -> ErrorState(uiState.error!!) { viewModel.loadProperty(propertyId) }
            uiState.property != null -> {
                val property = uiState.property!!
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
                    Surface(
                        color = colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            translatePropertyType(property.propertyType),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryTeal,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

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

                    Spacer(Modifier.height(16.dp))

                    val fullAddress = buildList {
                        property.streetAddress?.takeIf { it.isNotBlank() }?.let { add(it) }
                        property.neighborhood?.takeIf { it.isNotBlank() }?.let { add(it) }
                        property.postalCode?.takeIf { it.isNotBlank() }?.let { add("CP $it") }
                    }.joinToString(", ")

                    if (fullAddress.isNotBlank()) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                Icons.Outlined.LocationOn,
                                null,
                                tint = PrimaryTeal,
                                modifier = Modifier.size(18.dp).padding(top = 2.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(
                                    fullAddress,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colorScheme.onSurface
                                )
                                Text(
                                    "${property.city}, ${property.stateProvince}, ${property.country}",
                                    fontSize = 12.sp,
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.LocationOn, null, tint = PrimaryTeal, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "${property.city}, ${property.stateProvince}, ${property.country}",
                                fontSize = 13.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    Text("Especificaciones", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
                    Spacer(Modifier.height(10.dp))

                    val specs = buildList<DetailSpec> {
                        property.bedrooms?.let { add(DetailSpec(Icons.Default.Bed, "$it", "Recámaras")) }
                        val totalBaths = (property.fullBathrooms ?: 0) + (property.halfBathrooms ?: 0)
                        if (totalBaths > 0) {
                            val bathsText = if (property.halfBathrooms ?: 0 > 0) {
                                "${property.fullBathrooms ?: 0} completos + ${property.halfBathrooms} medios"
                            } else {
                                "$totalBaths"
                            }
                            add(DetailSpec(Icons.Default.Bathtub, bathsText, "Baños"))
                        }
                        property.builtAreaSqm?.let { add(DetailSpec(Icons.Default.SquareFoot, "${it.toInt()} m²", "Construcción")) }
                        property.lotSizeSqm?.let { add(DetailSpec(Icons.Outlined.MeetingRoom, "${it.toInt()} m²", "Terreno")) }
                        property.storiesFloors?.let { add(DetailSpec(Icons.Outlined.Layers, "$it", "Pisos")) }
                        property.parkingSpaces?.let { add(DetailSpec(Icons.Default.DirectionsCar, "$it", "Estacionamiento")) }
                        property.propertyAgeYears?.let { 
                            val ageText = if (it == 0) "Nueva" else if (it == 1) "1 año" else "$it años"
                            add(DetailSpec(Icons.Outlined.CalendarToday, ageText, "Antigüedad")) 
                        }
                    }

                    if (specs.isNotEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            specs.forEach { spec ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(spec.icon, null, tint = PrimaryTeal, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(spec.label, fontSize = 11.sp, color = colorScheme.onSurfaceVariant)
                                        Text(spec.value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colorScheme.onSurface)
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    property.maintenanceFee?.let { fee ->
                        if (fee > 0) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.AttachMoney, null, tint = PrimaryTeal, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Mantenimiento", fontSize = 11.sp, color = colorScheme.onSurfaceVariant)
                                    Text(
                                        formatPrice(fee, property.currency, ""),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colorScheme.onSurface
                                    )
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                        }
                    }

                    if (property.amenities.isNotEmpty()) {
                        Text("Amenidades y servicios", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
                        Spacer(Modifier.height(10.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            property.amenities.forEach { amenity ->
                                Surface(
                                    color = PrimaryTeal.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            Icons.Outlined.Check,
                                            null,
                                            tint = PrimaryTeal,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            formatAmenityName(amenity),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = PrimaryTeal
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    if (property.priceEstimation != null) {
                        PriceEstimationBlock(
                            priceEstimation = property.priceEstimation,
                            listingPrice = property.price,
                            currency = property.currency,
                            operationName = property.operationName
                        )
                        Spacer(Modifier.height(16.dp))
                    }

                    if (property.description != null && property.description.isNotBlank()) {
                        Text("Descripción", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            property.description,
                            fontSize = 13.sp,
                            color = colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                        Spacer(Modifier.height(16.dp))
                    }

                    if (!isOwner) {
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }

        if (!isOwner && uiState.property != null) {
            InterestFooter(
                state = uiState.interestState,
                error = uiState.interestError,
                onRegister = { viewModel.registerInterest(propertyId) },
                onViewProgress = { onNavigateToJourney(propertyId) },
                onRemoveInterest = { viewModel.removeInterest(propertyId) }
            )
        }
    }
}

private data class DetailSpec(val icon: ImageVector, val value: String, val label: String)

@Composable
private fun InterestFooter(
    state: InterestState,
    error: String?,
    onRegister: () -> Unit,
    onViewProgress: () -> Unit,
    onRemoveInterest: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    var showConfirmDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = colorScheme.surface,
        shadowElevation = 8.dp,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            InterestButton(
                state = state,
                onRegister = onRegister,
                onViewProgress = onViewProgress,
                onRemoveInterest = { showConfirmDialog = true }
            )
            if (error != null) {
                Spacer(Modifier.height(6.dp))
                Text(
                    error,
                    fontSize = 12.sp,
                    color = colorScheme.error,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("¿Quitar interés?") },
            text = { Text("Se perderá todo tu avance en esta propiedad. Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        onRemoveInterest()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC62828),
                        contentColor = Color.White
                    )
                ) {
                    Text("Sí, quitar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun InterestButton(
    state: InterestState,
    onRegister: () -> Unit,
    onViewProgress: () -> Unit,
    onRemoveInterest: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    when (state) {
        InterestState.NONE -> Button(
            onClick = onRegister,
            modifier = Modifier.fillMaxWidth().height(44.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal, contentColor = Color.White)
        ) {
            Text("Me interesa esta propiedad", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        InterestState.LOADING -> Button(
            onClick = {},
            enabled = false,
            modifier = Modifier.fillMaxWidth().height(44.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal.copy(alpha = 0.5f))
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        }
        InterestState.REGISTERED -> Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onViewProgress,
                modifier = Modifier.weight(1f).height(44.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal, contentColor = Color.White)
            ) {
                Icon(Icons.Outlined.Check, "Check", modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Mi progreso", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = onRemoveInterest,
                modifier = Modifier.weight(1f).height(44.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colorScheme.error)
            ) {
                Text("Quitar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
        InterestState.ERROR -> Button(
            onClick = onRegister,
            modifier = Modifier.fillMaxWidth().height(44.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.error, contentColor = Color.White)
        ) {
            Text("Reintentar", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

private fun translatePropertyType(type: String): String {
    return when (type.lowercase()) {
        "house" -> "Casa"
        "apartment" -> "Departamento"
        "land" -> "Terreno"
        "office" -> "Oficina"
        "commercial_premise" -> "Local comercial"
        "warehouse" -> "Bodega"
        "industrial" -> "Nave industrial"
        "parking" -> "Estacionamiento"
        else -> type.replaceFirstChar { it.uppercase() }
    }
}

private fun formatAmenityName(amenity: String): String {
    return amenity
        .replace("_", " ")
        .split(" ")
        .joinToString(" ") { word ->
            word.replaceFirstChar { it.uppercase() }
        }
}
