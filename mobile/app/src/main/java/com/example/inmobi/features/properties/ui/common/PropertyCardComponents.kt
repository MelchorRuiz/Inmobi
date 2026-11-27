package com.example.inmobi.features.properties.ui.common

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.RealEstateAgent
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.inmobi.core.ui.theme.PrimaryTeal
import com.example.inmobi.features.properties.domain.Property

@Composable
fun PropertyCard(
    property: Property,
    tag: String,
    tagColor: Color = PrimaryTeal,
    tagBgColor: Color = Color(0xFFEBF5F4),
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: () -> Unit = {},
    onActionClick: () -> Unit = {},
    actionLabel: String = "Ver detalles",
    bottomLabel: String = "Tasación neutral verificada",
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        onClick = onActionClick,
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        border = BorderStroke(1.dp, colorScheme.outlineVariant)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
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
                Surface(
                    color = tagBgColor,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(12.dp).align(Alignment.TopStart)
                ) {
                    Text(
                        tag,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = tagColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                if (trailingIcon != null) {
                    IconButton(
                        onClick = onTrailingIconClick,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .background(Color.White.copy(alpha = 0.8f), CircleShape)
                            .size(30.dp)
                    ) {
                        Icon(trailingIcon, null, tint = Color(0xFF334155), modifier = Modifier.size(16.dp))
                    }
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    property.listingTitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface,
                    maxLines = 1
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    formatPrice(property.price, property.currency, property.operationName),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryTeal
                )
                if (property.priceEstimation != null) {
                    Spacer(Modifier.height(4.dp))
                    PriceEstimationCompact(
                        priceEstimation = property.priceEstimation,
                        listingPrice = property.price,
                        currency = property.currency,
                        operationName = property.operationName
                    )
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        null,
                        tint = colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(
                        "${property.city}, ${property.stateProvince}",
                        fontSize = 11.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (property.bedrooms != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bed, null, tint = colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(3.dp))
                            Text("${property.bedrooms} hab", fontSize = 11.sp, color = colorScheme.onSurface)
                        }
                    }
                    val baths = (property.fullBathrooms ?: 0) + (property.halfBathrooms ?: 0) * 0.5
                    if (baths > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bathtub, null, tint = colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(3.dp))
                            Text(formatBathrooms(baths), fontSize = 11.sp, color = colorScheme.onSurface)
                        }
                    }
                    if (property.builtAreaSqm != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SquareFoot, null, tint = colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(3.dp))
                            Text("${property.builtAreaSqm.toInt()} m2", fontSize = 11.sp, color = colorScheme.onSurface)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ManagePropertyCard(
    property: Property,
    statusText: String,
    statusColor: Color,
    statusBgColor: Color,
    statText: String,
    actionText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        border = BorderStroke(1.dp, colorScheme.outlineVariant)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
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
                Surface(
                    color = statusBgColor,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(12.dp).align(Alignment.TopStart)
                ) {
                    Text(
                        statusText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    formatPrice(property.price, property.currency, property.operationName),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = colorScheme.onSurface
                )
                if (property.priceEstimation != null) {
                    Spacer(Modifier.height(2.dp))
                    PriceEstimationCompact(
                        priceEstimation = property.priceEstimation,
                        listingPrice = property.price,
                        currency = property.currency,
                        operationName = property.operationName
                    )
                }
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.LocationOn, null, tint = PrimaryTeal, modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(3.dp))
                    Text(
                        "${property.city}, ${property.stateProvince}",
                        fontSize = 11.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (property.bedrooms != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bed, null, tint = colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(3.dp))
                            Text("${property.bedrooms} hab", fontSize = 11.sp, color = colorScheme.onSurface)
                        }
                    }
                    val baths = (property.fullBathrooms ?: 0) + (property.halfBathrooms ?: 0) * 0.5
                    if (baths > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bathtub, null, tint = colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(3.dp))
                            Text(formatBathrooms(baths), fontSize = 11.sp, color = colorScheme.onSurface)
                        }
                    }
                    if (property.builtAreaSqm != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SquareFoot, null, tint = colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(3.dp))
                            Text("${property.builtAreaSqm.toInt()} m2", fontSize = 11.sp, color = colorScheme.onSurface)
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(statText, fontSize = 11.sp, color = colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = PrimaryTeal)
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(message, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(12.dp))
            androidx.compose.material3.TextButton(onClick = onRetry) {
                Text("Reintentar", color = PrimaryTeal)
            }
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(message, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun formatBathrooms(baths: Double): String {
    return if (baths == baths.toLong().toDouble()) {
        baths.toLong().toString()
    } else {
        "%.1f".format(baths)
    }
}

@Composable
fun MetricCard(modifier: Modifier = Modifier, icon: ImageVector, value: String, label: String) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        border = BorderStroke(1.dp, colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.Start) {
            Box(modifier = Modifier.size(30.dp).clip(CircleShape).background(colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = PrimaryTeal, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = colorScheme.onSurface)
            Text(label, fontSize = 10.sp, color = colorScheme.onSurfaceVariant, lineHeight = 13.sp)
        }
    }
}
