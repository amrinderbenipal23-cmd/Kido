package com.kido.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kido.app.ui.theme.KidoColors
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Star(
    filled: Boolean,
    modifier: Modifier = Modifier,
    fillColor: Color = KidoColors.Sunshine,
    emptyColor: Color = Color(0xFFE0E0E0),
) {
    val color = if (filled) fillColor else emptyColor
    Canvas(modifier = modifier.aspectRatio(1f)) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val outer = (size.minDimension / 2f) * 0.95f
        val inner = outer * 0.5f
        val path = Path().apply {
            for (i in 0 until 10) {
                val angleRad = ((i * 36.0 - 90.0) * Math.PI / 180.0).toFloat()
                val r = if (i % 2 == 0) outer else inner
                val x = cx + r * cos(angleRad)
                val y = cy + r * sin(angleRad)
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        drawPath(path, color)
    }
}

@Composable
fun StarRow(
    filled: Int,
    modifier: Modifier = Modifier,
    total: Int = 3,
    starSize: Dp = 64.dp,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(total) { index ->
            Star(
                filled = index < filled,
                modifier = Modifier.size(starSize),
            )
        }
    }
}
