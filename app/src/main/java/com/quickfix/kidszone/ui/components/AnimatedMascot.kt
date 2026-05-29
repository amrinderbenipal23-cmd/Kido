package com.quickfix.kidszone.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnimatedMascot(
    emoji: String = "🦉",
    message: String = "",
    size: Dp = 100.dp,
    modifier: Modifier = Modifier,
    isExcited: Boolean = false,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mascot")

    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isExcited) -16f else -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "mascot_bounce",
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "mascot_rotation",
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isExcited) 1.15f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "mascot_scale",
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .offset(y = bounce.dp)
                .scale(scale)
                .size(size)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFFFFE082), Color(0xFFFFB300))
                    )
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = emoji, fontSize = (size.value * 0.55f).sp)
        }

        if (message.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                    .background(Color(0xFF1A1A2E).copy(alpha = 0.85f))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    text = message,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
            }
        }
    }
}
