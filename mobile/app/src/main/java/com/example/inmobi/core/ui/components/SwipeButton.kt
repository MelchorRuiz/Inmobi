package com.example.inmobi.core.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inmobi.core.ui.theme.PrimaryTeal
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val thumbSize = 48.dp
private val trackHeight = 48.dp

@Composable
fun SwipeButton(
    text: String,
    completedText: String,
    isCompleted: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    var trackWidthPx by remember { mutableStateOf(0) }
    val thumbSizePx = with(density) { thumbSize.toPx() }
    val maxOffset = (trackWidthPx - thumbSizePx).coerceAtLeast(0f)

    val offsetX = remember { Animatable(0f) }

    LaunchedEffect(maxOffset, isCompleted) {
        val target = if (isCompleted) maxOffset else 0f
        offsetX.animateTo(target, tween(250))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(trackHeight)
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (isCompleted) PrimaryTeal else PrimaryTeal.copy(alpha = 0.25f)
            )
            .onSizeChanged { trackWidthPx = it.width }
            .pointerInput(isCompleted, maxOffset) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        scope.launch {
                            val current = offsetX.value
                            if (!isCompleted) {
                                if (current >= maxOffset * 0.65f) {
                                    offsetX.animateTo(maxOffset, tween(200))
                                    onToggle(true)
                                } else {
                                    offsetX.animateTo(0f, tween(200))
                                }
                            } else {
                                if (current <= maxOffset * 0.35f) {
                                    offsetX.animateTo(0f, tween(200))
                                    onToggle(false)
                                } else {
                                    offsetX.animateTo(maxOffset, tween(200))
                                }
                            }
                        }
                    },
                    onDragCancel = {
                        scope.launch {
                            val target = if (isCompleted) maxOffset else 0f
                            offsetX.animateTo(target, tween(200))
                        }
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        scope.launch {
                            val newOffset = (offsetX.value + dragAmount).coerceIn(0f, maxOffset)
                            offsetX.snapTo(newOffset)
                        }
                    }
                )
            }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isCompleted) completedText else text,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .size(thumbSize)
                .background(
                    color = if (isCompleted) Color.White else PrimaryTeal,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    Icons.Default.Undo,
                    null,
                    tint = PrimaryTeal,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Icon(
                    Icons.Default.Check,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
