package com.example.inmobi.features.properties.ui.rent

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inmobi.core.location.LocationProvider
import com.example.inmobi.core.ui.theme.PrimaryTeal
import com.example.inmobi.features.properties.domain.Property
import com.example.inmobi.features.properties.ui.common.EmptyState
import com.example.inmobi.features.properties.ui.common.ErrorState
import com.example.inmobi.features.properties.ui.common.LoadingState
import com.example.inmobi.features.properties.ui.common.PropertyCard
import com.example.inmobi.features.properties.ui.common.PropertyMapView
import com.example.inmobi.features.properties.ui.common.PropertySummarySheet
import com.example.inmobi.features.properties.ui.common.ViewMode
import com.example.inmobi.features.properties.ui.common.ViewModeToggle

@Composable
fun RentPropertyScreen(
    onPropertyClick: (Int) -> Unit = {},
    refreshKey: Int = 0,
    viewModel: RentPropertyViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current

    var viewMode by remember { mutableStateOf(ViewMode.LIST) }
    var selectedProperty by remember { mutableStateOf<Property?>(null) }
    var userLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var locationPermissionGranted by remember { mutableStateOf(false) }

    val locationProvider = remember { LocationProvider(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        locationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(refreshKey) {
        viewModel.loadProperties()
    }

    LaunchedEffect(viewMode, locationPermissionGranted) {
        if (viewMode == ViewMode.MAP && locationPermissionGranted) {
            userLocation = locationProvider.getCurrentLocation()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            Surface(color = colorScheme.surfaceVariant, shape = RoundedCornerShape(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Icon(Icons.Outlined.Key, null, tint = PrimaryTeal, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Arrendamiento seguro", color = PrimaryTeal, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(12.dp))
            Text("Rentas con tope etico y sin abusos", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = colorScheme.onSurface)
            Spacer(Modifier.height(4.dp))
            Text("Explora opciones de renta equilibradas y ajustadas a la capacidad de pago real, respaldadas por Inmobi.", fontSize = 12.sp, color = colorScheme.onSurfaceVariant, lineHeight = 16.sp)
            Spacer(Modifier.height(16.dp))

            if (!uiState.isLoading && uiState.error == null && uiState.properties.isNotEmpty()) {
                ViewModeToggle(
                    selected = viewMode,
                    onModeChange = { newMode ->
                        viewMode = newMode
                        if (newMode == ViewMode.MAP && !locationPermissionGranted) {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    }
                )
                Spacer(Modifier.height(12.dp))
            }
        }

        when {
            uiState.isLoading -> LoadingState()
            uiState.error != null -> ErrorState(uiState.error!!) { viewModel.loadProperties() }
            uiState.properties.isEmpty() -> EmptyState("No hay propiedades en renta disponibles")
            viewMode == ViewMode.LIST -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                ) {
                    uiState.properties.forEach { property ->
                        PropertyCard(
                            property = property,
                            tag = "Renta Justa",
                            tagColor = Color(0xFFD97706),
                            tagBgColor = Color(0xFFFEF3C7),
                            onActionClick = { onPropertyClick(property.id) }
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                    Spacer(Modifier.height(20.dp))
                    Text("2026 Inmobi. Todos los derechos reservados.", fontSize = 11.sp, color = colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(24.dp))
                }
            }
            viewMode == ViewMode.MAP -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clipToBounds()
                ) {
                    PropertyMapView(
                        properties = uiState.properties,
                        onPropertyClick = { property -> selectedProperty = property },
                        userLocation = if (locationPermissionGranted) userLocation else null
                    )
                }
            }
        }
    }

    if (selectedProperty != null) {
        PropertySummarySheet(
            property = selectedProperty!!,
            onDismiss = { selectedProperty = null },
            onViewDetails = {
                val id = selectedProperty!!.id
                selectedProperty = null
                onPropertyClick(id)
            }
        )
    }
}
