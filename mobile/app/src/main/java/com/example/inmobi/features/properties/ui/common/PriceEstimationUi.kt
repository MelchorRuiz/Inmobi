package com.example.inmobi.features.properties.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.example.inmobi.core.ui.theme.PrimaryTeal
import com.example.inmobi.features.properties.domain.PriceEstimation

private val DeviationGreen = Color(0xFF059669)
private val DeviationAmber = Color(0xFFD97706)
private val DeviationRed = Color(0xFFDC2626)

fun formatPrice(price: Double, currency: String, operationName: String): String {
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

fun formatPriceShort(price: Double): String {
    val formatted = if (price == price.toLong().toDouble()) {
        String.format("%,d", price.toLong())
    } else {
        String.format("%,.2f", price)
    }.replace(",", "X").replace(".", ",").replace("X", ".")
    return "$$formatted"
}

data class DeviationInfo(val label: String, val color: Color)

fun deviationLabel(listingPrice: Double, estimatedPrice: Double): DeviationInfo? {
    if (estimatedPrice <= 0) return null
    val pct = ((listingPrice - estimatedPrice) / estimatedPrice) * 100
    return when {
        pct <= -15 -> DeviationInfo("Por debajo del precio justo", DeviationGreen)
        pct <= -5 -> DeviationInfo("Competitivo", DeviationGreen)
        pct <= 5 -> DeviationInfo("Precio adecuado", PrimaryTeal)
        pct <= 15 -> DeviationInfo("Por encima del precio justo", DeviationAmber)
        else -> DeviationInfo("Muy por encima del precio justo", DeviationRed)
    }
}

@Composable
fun PriceEstimationCompact(
    priceEstimation: PriceEstimation?,
    listingPrice: Double,
    currency: String,
    operationName: String,
    modifier: Modifier = Modifier
) {
    if (priceEstimation == null) return

    val colorScheme = MaterialTheme.colorScheme
    val deviation = deviationLabel(listingPrice, priceEstimation.estimatedPrice)

    Column(modifier = modifier) {
        if (deviation != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(deviation.color.copy(alpha = 0.1f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Icon(
                    Icons.Outlined.AutoGraph,
                    null,
                    tint = deviation.color,
                    modifier = Modifier.size(10.dp)
                )
                Spacer(Modifier.width(3.dp))
                Text(
                    deviation.label,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = deviation.color,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.height(2.dp))
        }
        Text(
            "Precio justo: ${formatPrice(priceEstimation.estimatedPrice, currency, operationName)}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun PriceEstimationBlock(
    priceEstimation: PriceEstimation?,
    listingPrice: Double,
    currency: String,
    operationName: String,
    modifier: Modifier = Modifier
) {
    if (priceEstimation == null) return

    val colorScheme = MaterialTheme.colorScheme
    val deviation = deviationLabel(listingPrice, priceEstimation.estimatedPrice)
    val confidencePercent = (priceEstimation.confidenceScore * 100).toInt().coerceIn(0, 100)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(12.dp)
    ) {
        if (deviation != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(deviation.color.copy(alpha = 0.1f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    Icons.Outlined.AutoGraph,
                    null,
                    tint = deviation.color,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    deviation.label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = deviation.color
                )
            }
            Spacer(Modifier.height(8.dp))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Outlined.AutoGraph,
                null,
                tint = PrimaryTeal,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                "Precio justo sugerido",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            formatPrice(priceEstimation.estimatedPrice, currency, operationName),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryTeal
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Entre ${formatPriceShort(priceEstimation.minPrice)} y ${formatPriceShort(priceEstimation.maxPrice)} $currency",
            fontSize = 11.sp,
            color = colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Confianza del modelo",
                fontSize = 10.sp,
                color = colorScheme.onSurfaceVariant
            )
            Text(
                "$confidencePercent%",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTeal
            )
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { priceEstimation.confidenceScore.toFloat().coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = PrimaryTeal,
            trackColor = colorScheme.surfaceVariant,
        )

        Spacer(Modifier.height(6.dp))
        Text(
            "Modelo ${priceEstimation.modelVersion}",
            fontSize = 9.sp,
            color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}
