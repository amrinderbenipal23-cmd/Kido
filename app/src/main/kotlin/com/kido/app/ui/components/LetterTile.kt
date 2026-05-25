package com.kido.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A square tile showing a single letter glyph. Animates a small squash on tap.
 */
@Composable
fun LetterTile(
    glyph: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    background: Color = Color(0xFFFFC107),
    contentColor: Color = Color.White,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.92f else 1f, label = "tile-scale")

    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            ),
        shape = RoundedCornerShape(28.dp),
        color = background,
        shadowElevation = 6.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = glyph,
                color = contentColor,
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
