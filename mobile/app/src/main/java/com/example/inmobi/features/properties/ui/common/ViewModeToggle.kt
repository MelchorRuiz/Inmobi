package com.example.inmobi.features.properties.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inmobi.core.ui.theme.PrimaryTeal

enum class ViewMode { LIST, MAP }

@Composable
fun ViewModeToggle(
    selected: ViewMode,
    onModeChange: (ViewMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.outlineVariant)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ToggleButton(
                label = "Lista",
                icon = Icons.Default.List,
                isSelected = selected == ViewMode.LIST,
                onClick = { onModeChange(ViewMode.LIST) },
                modifier = Modifier.weight(1f)
            )
            ToggleButton(
                label = "Mapa",
                icon = Icons.Default.Map,
                isSelected = selected == ViewMode.MAP,
                onClick = { onModeChange(ViewMode.MAP) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ToggleButton(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .padding(4.dp)
            .height(38.dp)
            .background(
                if (isSelected) PrimaryTeal else Color.Transparent,
                RoundedCornerShape(10.dp)
            )
            .then(
                if (isSelected) Modifier
                else Modifier
            )
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.TextButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth().height(38.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                Icon(
                    icon,
                    null,
                    tint = if (isSelected) Color.White else colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                androidx.compose.foundation.layout.Spacer(Modifier.size(6.dp))
                Text(
                    label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
