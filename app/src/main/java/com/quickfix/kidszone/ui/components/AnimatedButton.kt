package com.quickfix.kidszone.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnimatedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    startColor: Color = Color(0xFF845EC2),
    endColor: Color = Color(0xFFFF6B9D),
    textColor: Color = Color.White,
    emoji: String = "",
    height: Dp = 64.dp,
    cornerRadius: Dp = 24.dp,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "button_scale"
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 8.dp,
        label = "button_shadow"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(shadowElevation, RoundedCornerShape(cornerRadius))
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                if (enabled) Brush.horizontalGradient(listOf(startColor, endColor))
                else Brush.horizontalGradient(listOf(Color.Gray, Color.DarkGray))
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            )
            .height(height)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.wrapContentSize(),
        ) {
            if (emoji.isNotEmpty()) {
                Text(text = emoji, fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
            }
            Text(
                text = text,
                color = textColor,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
            )
        }
    }
}

@Composable
fun BounceButton(
    emoji: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    backgroundColor: Color = Color(0xFFFF6B6B),
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bounce")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bounce_y",
    )

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "bounce_scale",
    )

    Box(
        modifier = modifier
            .offset(y = offsetY.dp)
            .scale(scale)
            .size(size)
            .shadow(6.dp, RoundedCornerShape(50))
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = emoji, fontSize = (size.value * 0.45f).sp)
    }
}
