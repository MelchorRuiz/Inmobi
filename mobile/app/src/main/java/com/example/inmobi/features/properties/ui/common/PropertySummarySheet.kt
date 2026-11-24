package com.example.inmobi.features.properties.ui.common

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.RealEstateAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.inmobi.core.ui.theme.PrimaryTeal
import com.example.inmobi.features.properties.domain.Property

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertySummarySheet(
    property: Property,
    onDismiss: () -> Unit,
    onViewDetails: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val colorScheme = MaterialTheme.colorScheme

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colorScheme.surface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colorScheme.surfaceVariant)
            ) {
                val coverImage = property.images.firstOrNull()
                if (coverImage != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(coverImage.url)
                            .crossfade(true)
                            .build(),
                        contentDescription = property.listingTitle,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Outlined.RealEstateAgent,
                            null,
                            tint = PrimaryTeal.copy(alpha = 0.4f),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            Text(
                property.listingTitle,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            Text(
                formatPrice(property.price, property.currency, property.operationName),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryTeal
            )

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.LocationOn,
                    null,
                    tint = colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    buildList {
                        property.streetAddress?.takeIf { it.isNotBlank() }?.let { add(it) }
                        property.neighborhood?.takeIf { it.isNotBlank() }?.let { add(it) }
                    }.joinToString(", ").ifBlank {
                        "${property.city}, ${property.stateProvince}"
                    },
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (property.bedrooms != null) {
                    SheetSpecItem(Icons.Default.Bed, "${property.bedrooms} hab", "Recamaras")
                }
                val totalBaths = (property.fullBathrooms ?: 0) + (property.halfBathrooms ?: 0)
                if (totalBaths > 0) {
                    SheetSpecItem(Icons.Default.Bathtub, "$totalBaths", "Banos")
                }
                property.builtAreaSqm?.let {
                    SheetSpecItem(Icons.Default.SquareFoot, "${it.toInt()} m2", "Construccion")
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onViewDetails,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryTeal,
                    contentColor = Color.White
                )
            ) {
                Text("Ver detalles completos", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SheetSpecItem(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String) {
    val colorScheme = MaterialTheme.colorScheme
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = PrimaryTeal, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
        }
        Text(label, fontSize = 10.sp, color = colorScheme.onSurfaceVariant)
    }
}
