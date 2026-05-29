package com.quickfix.kidszone.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
fun VoiceButton(
    isListening: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "voice_pulse")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "voice_pulse_scale",
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        if (isListening) {
            // Ripple rings
            Box(
                modifier = Modifier
                    .scale(pulseScale)
                    .size(size * 1.5f)
                    .clip(CircleShape)
                    .background(Color(0xFFFF6B6B).copy(alpha = 0.3f)),
            )
            Box(
                modifier = Modifier
                    .scale(pulseScale * 0.85f)
                    .size(size * 1.25f)
                    .clip(CircleShape)
                    .background(Color(0xFFFF6B6B).copy(alpha = 0.2f)),
            )
        }

        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(
                    if (isListening)
                        Brush.radialGradient(listOf(Color(0xFFFF4757), Color(0xFFFF6B6B)))
                    else
                        Brush.radialGradient(listOf(Color(0xFF845EC2), Color(0xFFFF6B9D)))
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onToggle,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = if (isListening) "🔴" else "🎤", fontSize = (size.value * 0.35f).sp)
                Text(
                    text = if (isListening) "Stop" else "Speak",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                )
            }
        }
    }
}
