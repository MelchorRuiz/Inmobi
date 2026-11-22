package com.example.inmobi.features.properties.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inmobi.core.ui.theme.PrimaryTeal

enum class StatusFilter(val apiValue: String?) {
    ALL(null),
    ACTIVE("active"),
    CANCELLED("cancelled"),
    CLOSED("closed");
}

@Composable
fun StatusFilterChipRow(
    selected: StatusFilter,
    labels: Map<StatusFilter, String>,
    onFilterChange: (StatusFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatusFilter.entries.forEach { filter ->
            val isSelected = filter == selected
            val containerColor = if (isSelected) PrimaryTeal else colorScheme.surface
            val contentColor = if (isSelected) Color.White else colorScheme.onSurfaceVariant
            val borderColor = if (isSelected) PrimaryTeal else colorScheme.outlineVariant

            Surface(
                onClick = { onFilterChange(filter) },
                shape = RoundedCornerShape(20.dp),
                color = containerColor,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier
            ) {
                Text(
                    labels[filter] ?: filter.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}
