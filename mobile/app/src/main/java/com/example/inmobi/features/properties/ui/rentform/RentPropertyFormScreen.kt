package com.example.inmobi.features.properties.ui.rentform

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Bathtub
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Cabin
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Carpenter
import androidx.compose.material.icons.outlined.CellTower
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.DryCleaning
import androidx.compose.material.icons.outlined.Elevator
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Grass
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.House
import androidx.compose.material.icons.outlined.Houseboat
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.KingBed
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.outlined.LocalParking
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.outlined.Pool
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material.icons.outlined.Wc
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.inmobi.core.ui.theme.PrimaryTeal
import com.example.inmobi.features.properties.domain.PropertyType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RentPropertyFormScreen(
    onBack: () -> Unit = {},
    onSaved: () -> Unit = {},
    viewModel: RentPropertyFormViewModel = viewModel()
) {
    val formState by viewModel.formState.collectAsState()
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val errorColor = Color(0xFFE53E3E)

    LaunchedEffect(formState.submitSuccess) {
        if (formState.submitSuccess) {
            viewModel.clearSubmitState()
            onSaved()
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        viewModel.addImages(uris)
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Cerrar",
                        tint = colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Nueva propiedad en arrendamiento",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colorScheme.background)
        ) {
            StepIndicator(
                currentStep = formState.currentStep,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                when (formState.currentStep) {
                    0 -> BasicInfoStep(
                        formState = formState,
                        viewModel = viewModel,
                        errorColor = errorColor
                    )
                    1 -> LocationStep(
                        formState = formState,
                        viewModel = viewModel,
                        errorColor = errorColor
                    )
                    2 -> CharacteristicsAmenitiesStep(
                        formState = formState,
                        viewModel = viewModel
                    )
                    3 -> PhotosStep(
                        formState = formState,
                        viewModel = viewModel,
                        onPickImages = { imagePickerLauncher.launch("image/*") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (formState.submitError != null) {
                    Text(
                        text = formState.submitError!!,
                        color = errorColor,
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            StepNavigationButtons(
                currentStep = formState.currentStep,
                isSubmitting = formState.isSubmitting,
                isUploadingImages = formState.isUploadingImages,
                onPrevious = { viewModel.previousStep() },
                onNext = { viewModel.nextStep() },
                onSubmit = { viewModel.submitForm(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
private fun StepIndicator(
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val stepLabels = listOf("Basica", "Ubicacion", "Detalles", "Fotos")

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        stepLabels.forEachIndexed { index, label ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (index <= currentStep) PrimaryTeal else colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (index < currentStep) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Text(
                            text = "${index + 1}",
                            color = if (index == currentStep) Color.White else colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = if (index == currentStep) PrimaryTeal else colorScheme.onSurfaceVariant,
                    fontWeight = if (index == currentStep) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BasicInfoStep(
    formState: RentPropertyFormState,
    viewModel: RentPropertyFormViewModel,
    errorColor: Color
) {
    val colorScheme = MaterialTheme.colorScheme

    Spacer(modifier = Modifier.height(8.dp))

    FieldLabel(icon = Icons.Outlined.Title, label = "Titulo de la publicacion *")
    FormTextField(
        value = formState.listingTitle,
        onValueChange = { viewModel.onListingTitleChange(it) },
        placeholder = "Ej. Depto en renta en Polanco",
        isError = formState.touchedFields.contains("listingTitle") && formState.errors["listingTitle"] != null,
        errorMessage = if (formState.touchedFields.contains("listingTitle")) formState.errors["listingTitle"] else null,
        errorColor = errorColor
    )
    Spacer(modifier = Modifier.height(12.dp))

    FieldLabel(icon = Icons.Outlined.Description, label = "Descripcion")
    FormTextField(
        value = formState.description,
        onValueChange = { viewModel.onDescriptionChange(it) },
        placeholder = "Describe tu propiedad...",
        singleLine = false,
        minLines = 3
    )
    Spacer(modifier = Modifier.height(12.dp))

    FieldLabel(icon = Icons.Outlined.AttachMoney, label = "Renta mensual *")
    FormTextField(
        value = formState.price,
        onValueChange = { viewModel.onPriceChange(it) },
        placeholder = "Ej. 15000",
        keyboardType = KeyboardType.Decimal,
        isError = formState.touchedFields.contains("price") && formState.errors["price"] != null,
        errorMessage = if (formState.touchedFields.contains("price")) formState.errors["price"] else null,
        errorColor = errorColor
    )
    Spacer(modifier = Modifier.height(12.dp))

    FieldLabel(icon = Icons.Outlined.CreditCard, label = "Cuota de mantenimiento")
    FormTextField(
        value = formState.maintenanceFee,
        onValueChange = { viewModel.onMaintenanceFeeChange(it) },
        placeholder = "Ej. 500",
        keyboardType = KeyboardType.Decimal
    )
    Spacer(modifier = Modifier.height(12.dp))

    FieldLabel(icon = Icons.Outlined.Home, label = "Tipo de propiedad *")
    PropertyTypeDropdown(
        selectedType = formState.propertyType,
        onTypeSelected = { viewModel.onPropertyTypeChange(it) }
    )
}

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationStep(
    formState: RentPropertyFormState,
    viewModel: RentPropertyFormViewModel,
    errorColor: Color
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    val initialLat = formState.latitude ?: 19.4326
    val initialLng = formState.longitude ?: -99.1332

    val htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                body { margin: 0; padding: 0; }
                #map { width: 100%; height: 250px; border-radius: 12px; }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                var map = L.map('map').setView([$initialLat, $initialLng], 15);
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    attribution: '© OpenStreetMap',
                    maxZoom: 19
                }).addTo(map);

                var marker = L.marker([$initialLat, $initialLng], {draggable: true}).addTo(map);

                marker.on('dragend', function(e) {
                    var pos = marker.getLatLng();
                    Android.onCoordinateSelected(pos.lat, pos.lng);
                });

                map.on('click', function(e) {
                    marker.setLatLng(e.latlng);
                    Android.onCoordinateSelected(e.latlng.lat, e.latlng.lng);
                });
            </script>
        </body>
        </html>
    """.trimIndent()

    Spacer(modifier = Modifier.height(8.dp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(270.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        AndroidView(
            factory = { ctx ->
                MapWebView(ctx).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    webViewClient = WebViewClient()
                    addJavascriptInterface(object {
                        @JavascriptInterface
                        fun onCoordinateSelected(lat: Double, lng: Double) {
                            viewModel.onLocationSelected(lat, lng)
                            viewModel.reverseGeocode(context, lat, lng)
                        }
                    }, "Android")
                    loadDataWithBaseURL(
                        "https://unpkg.com/",
                        htmlContent,
                        "text/html",
                        "UTF-8",
                        null
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Datos de ubicacion",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = colorScheme.onSurface
    )
    Text(
        text = "Mueve el mapa para ubicar la propiedad. Los campos se autocompletan pero puedes editarlos.",
        fontSize = 12.sp,
        color = colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = 4.dp)
    )

    Spacer(modifier = Modifier.height(12.dp))

    FieldLabel(icon = Icons.Outlined.Map, label = "Pais")
    FormTextField(
        value = formState.country,
        onValueChange = { viewModel.onCountryChange(it) },
        placeholder = "Ej. Mexico"
    )
    Spacer(modifier = Modifier.height(12.dp))

    FieldLabel(icon = Icons.Outlined.Map, label = "Direccion *")
    FormTextField(
        value = formState.streetAddress,
        onValueChange = { viewModel.onStreetAddressChange(it) },
        placeholder = "Calle y numero",
        isError = formState.touchedFields.contains("streetAddress") && formState.errors["streetAddress"] != null,
        errorMessage = if (formState.touchedFields.contains("streetAddress")) formState.errors["streetAddress"] else null,
        errorColor = errorColor
    )
    Spacer(modifier = Modifier.height(12.dp))

    FieldLabel(icon = Icons.Outlined.LocationCity, label = "Colonia")
    FormTextField(
        value = formState.neighborhood,
        onValueChange = { viewModel.onNeighborhoodChange(it) },
        placeholder = "Ej. Polanco"
    )
    Spacer(modifier = Modifier.height(12.dp))

    Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f)) {
            FieldLabel(icon = Icons.Outlined.Map, label = "Codigo Postal")
            FormTextField(
                value = formState.postalCode,
                onValueChange = { viewModel.onPostalCodeChange(it) },
                placeholder = "Ej. 11560",
                keyboardType = KeyboardType.Number,
                isError = formState.touchedFields.contains("postalCode") && formState.errors["postalCode"] != null,
                errorMessage = if (formState.touchedFields.contains("postalCode")) formState.errors["postalCode"] else null,
                errorColor = errorColor
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1.5f)) {
            FieldLabel(icon = Icons.Outlined.LocationCity, label = "Ciudad *")
            FormTextField(
                value = formState.city,
                onValueChange = { viewModel.onCityChange(it) },
                placeholder = "Ej. Ciudad de Mexico",
                isError = formState.touchedFields.contains("city") && formState.errors["city"] != null,
                errorMessage = if (formState.touchedFields.contains("city")) formState.errors["city"] else null,
                errorColor = errorColor
            )
        }
    }
    Spacer(modifier = Modifier.height(12.dp))

    FieldLabel(icon = Icons.Outlined.Map, label = "Estado *")
    FormTextField(
        value = formState.stateProvince,
        onValueChange = { viewModel.onStateProvinceChange(it) },
        placeholder = "Ej. CDMX",
        isError = formState.touchedFields.contains("stateProvince") && formState.errors["stateProvince"] != null,
        errorMessage = if (formState.touchedFields.contains("stateProvince")) formState.errors["stateProvince"] else null,
        errorColor = errorColor
    )

    if (formState.latitude != null && formState.longitude != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Coordenadas: ${"%.6f".format(formState.latitude)}, ${"%.6f".format(formState.longitude)}",
            fontSize = 11.sp,
            color = colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CharacteristicsAmenitiesStep(
    formState: RentPropertyFormState,
    viewModel: RentPropertyFormViewModel
) {
    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Caracteristicas",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(8.dp))

    Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f)) {
            FieldLabel(icon = Icons.Outlined.Bathtub, label = "Recamaras")
            FormNumberField(
                value = formState.bedrooms,
                onValueChange = { viewModel.onBedroomsChange(it) }
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            FieldLabel(icon = Icons.Outlined.Wc, label = "Banos compl.")
            FormNumberField(
                value = formState.fullBathrooms,
                onValueChange = { viewModel.onFullBathroomsChange(it) }
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            FieldLabel(icon = Icons.Outlined.Wc, label = "Medios banos")
            FormNumberField(
                value = formState.halfBathrooms,
                onValueChange = { viewModel.onHalfBathroomsChange(it) }
            )
        }
    }
    Spacer(modifier = Modifier.height(12.dp))

    Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f)) {
            FieldLabel(icon = Icons.Outlined.Carpenter, label = "Area construida (m2)")
            FormDecimalField(
                value = formState.builtAreaSqm,
                onValueChange = { viewModel.onBuiltAreaSqmChange(it) }
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            FieldLabel(icon = Icons.Outlined.Grass, label = "Area terreno (m2)")
            FormDecimalField(
                value = formState.lotSizeSqm,
                onValueChange = { viewModel.onLotSizeSqmChange(it) }
            )
        }
    }
    Spacer(modifier = Modifier.height(12.dp))

    Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f)) {
            FieldLabel(icon = Icons.Outlined.LocalParking, label = "Estacionamientos")
            FormNumberField(
                value = formState.parkingSpaces,
                onValueChange = { viewModel.onParkingSpacesChange(it) }
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            FieldLabel(icon = Icons.Outlined.Cabin, label = "Pisos")
            FormNumberField(
                value = formState.storiesFloors,
                onValueChange = { viewModel.onStoriesFloorsChange(it) }
            )
        }
    }
    Spacer(modifier = Modifier.height(12.dp))

    FieldLabel(icon = Icons.Outlined.CalendarMonth, label = "Antiguedad (anos)")
    FormNumberField(
        value = formState.propertyAgeYears,
        onValueChange = { viewModel.onPropertyAgeYearsChange(it) }
    )

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = "Amenidades",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(8.dp))

    if (formState.isLoadingAmenities) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = PrimaryTeal,
                strokeWidth = 2.dp
            )
        }
    } else if (formState.availableAmenities.isEmpty()) {
        Text(
            text = "No hay amenidades disponibles",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    } else {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            formState.availableAmenities.forEach { amenity ->
                AmenityChip(
                    amenity = amenity,
                    isSelected = formState.selectedAmenityIds.contains(amenity.id),
                    onToggle = { viewModel.toggleAmenity(amenity.id) }
                )
            }
        }
    }
}

@Composable
private fun PhotosStep(
    formState: RentPropertyFormState,
    viewModel: RentPropertyFormViewModel,
    onPickImages: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Fotos de la propiedad",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Sube fotos para mostrar tu propiedad. Puedes agregar varias imagenes.",
        fontSize = 12.sp,
        color = colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(12.dp))

    if (formState.isUploadingImages) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = PrimaryTeal,
                    strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Subiendo imagenes...",
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (formState.selectedImages.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .height(((formState.selectedImages.size / 3 + 1) * 120).dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(formState.selectedImages) { image ->
                ImagePreviewCard(
                    image = image,
                    onRemove = { viewModel.removeImage(image.uri) }
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }

    OutlinedButton(
        onClick = onPickImages,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryTeal)
    ) {
        Icon(
            imageVector = Icons.Outlined.AddPhotoAlternate,
            contentDescription = null,
            tint = PrimaryTeal,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Agregar fotos",
            color = PrimaryTeal,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun StepNavigationButtons(
    currentStep: Int,
    isSubmitting: Boolean,
    isUploadingImages: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (currentStep > 0) {
            OutlinedButton(
                onClick = onPrevious,
                modifier = Modifier.weight(1f).height(46.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Anterior",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (currentStep < 3) {
            Button(
                onClick = onNext,
                modifier = Modifier.weight(1f).height(46.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
            ) {
                Text(
                    text = "Siguiente",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        } else {
            Button(
                onClick = onSubmit,
                modifier = Modifier.weight(1f).height(46.dp),
                shape = RoundedCornerShape(10.dp),
                enabled = !isSubmitting && !isUploadingImages,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
            ) {
                if (isSubmitting || isUploadingImages) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = when {
                        isUploadingImages -> "Subiendo..."
                        isSubmitting -> "Publicando..."
                        else -> "Publicar propiedad"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PropertyTypeDropdown(
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

    fun getSpanishName(apiName: String): String {
        return when (apiName) {
            "house" -> "Casa"
            "apartment" -> "Departamento"
            "land" -> "Terreno"
            "office" -> "Oficina"
            "commercial_premise" -> "Local comercial"
            "warehouse" -> "Bodega"
            "industrial" -> "Nave industrial"
            "parking" -> "Estacionamiento"
            else -> apiName.replaceFirstChar { it.uppercase() }
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = getSpanishName(selectedType),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            shape = RoundedCornerShape(10.dp),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryTeal,
                focusedLabelColor = PrimaryTeal,
                cursorColor = PrimaryTeal,
                focusedContainerColor = colorScheme.surfaceContainerLow,
                unfocusedContainerColor = colorScheme.surfaceContainerLow
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            PropertyType.entries.forEach { type ->
                DropdownMenuItem(
                    text = {
                        Text(
                            getSpanishName(type.apiName),
                            fontSize = 14.sp
                        )
                    },
                    onClick = {
                        onTypeSelected(type.apiName)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun AmenityChip(
    amenity: RentAmenityItem,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    FilterChip(
        selected = isSelected,
        onClick = onToggle,
        label = {
            Text(
                text = amenity.name,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = PrimaryTeal.copy(alpha = 0.2f),
            selectedLabelColor = PrimaryTeal
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = colorScheme.outlineVariant,
            selectedBorderColor = PrimaryTeal,
            enabled = true,
            selected = isSelected,
            borderWidth = 1.dp,
            selectedBorderWidth = 1.5.dp
        )
    )
}

@Composable
private fun ImagePreviewCard(
    image: RentSelectedImage,
    onRemove: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(colorScheme.surfaceVariant)
    ) {
        AsyncImage(
            model = image.uri,
            contentDescription = "Foto de propiedad",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.6f))
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Eliminar foto",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun FieldLabel(icon: ImageVector, label: String) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onSurface
        )
    }
}

@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorMessage: String? = null,
    errorColor: Color = Color(0xFFE53E3E)
) {
    val colorScheme = MaterialTheme.colorScheme

    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 13.sp, color = colorScheme.onSurfaceVariant) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = singleLine,
            minLines = minLines,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = colorScheme.surfaceContainerLow,
                focusedContainerColor = colorScheme.surface,
                focusedBorderColor = PrimaryTeal,
                unfocusedBorderColor = if (isError) errorColor else colorScheme.outlineVariant,
                focusedTextColor = colorScheme.onSurface,
                unfocusedTextColor = colorScheme.onSurface,
                errorBorderColor = errorColor
            )
        )
        if (isError && errorMessage != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = errorMessage,
                color = errorColor,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
private fun FormNumberField(
    value: String,
    onValueChange: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = colorScheme.surfaceContainerLow,
            focusedContainerColor = colorScheme.surface,
            focusedBorderColor = PrimaryTeal,
            unfocusedBorderColor = colorScheme.outlineVariant,
            focusedTextColor = colorScheme.onSurface,
            unfocusedTextColor = colorScheme.onSurface
        )
    )
}

@Composable
private fun FormDecimalField(
    value: String,
    onValueChange: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = colorScheme.surfaceContainerLow,
            focusedContainerColor = colorScheme.surface,
            focusedBorderColor = PrimaryTeal,
            unfocusedBorderColor = colorScheme.outlineVariant,
            focusedTextColor = colorScheme.onSurface,
            unfocusedTextColor = colorScheme.onSurface
        )
    )
}

@SuppressLint("ViewConstructor")
private class MapWebView(context: Context) : WebView(context) {
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                parent?.requestDisallowInterceptTouchEvent(true)
            }
        }
        return super.onTouchEvent(event)
    }
}
