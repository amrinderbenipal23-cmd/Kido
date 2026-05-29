package com.quickfix.kidszone.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.sin
import kotlin.random.Random

private data class ConfettiPiece(
    val x: Float,
    val y: Float,
    val size: Float,
    val color: Color,
    val angle: Float,
    val speed: Float,
    val wobble: Float,
)

private val confettiColors = listOf(
    Color(0xFFFF6B6B), Color(0xFF4ECDC4), Color(0xFFFFBE0B),
    Color(0xFFFF6B9D), Color(0xFF845EC2), Color(0xFF06D6A0),
    Color(0xFFFF9671), Color(0xFF0077B6),
)

@Composable
fun ConfettiAnimation(
    modifier: Modifier = Modifier,
    pieceCount: Int = 60,
) {
    val pieces = remember {
        List(pieceCount) {
            ConfettiPiece(
                x = Random.nextFloat(),
                y = Random.nextFloat() * -0.5f,
                size = Random.nextFloat() * 14f + 8f,
                color = confettiColors.random(),
                angle = Random.nextFloat() * 360f,
                speed = Random.nextFloat() * 0.004f + 0.002f,
                wobble = Random.nextFloat() * 3f,
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "confetti_progress",
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        pieces.forEach { piece ->
            val currentY = ((piece.y + progress * (piece.speed * 1000)) % 1.4f) * size.height
            val currentX = piece.x * size.width + sin(progress * 6.28f * piece.wobble) * 30f

            drawRect(
                color = piece.color,
                topLeft = Offset(currentX, currentY),
                size = androidx.compose.ui.geometry.Size(piece.size, piece.size * 0.6f),
                alpha = (1f - (currentY / size.height)).coerceIn(0f, 1f),
            )
        }
    }
}

@Composable
fun StarBurstEffect(
    starCount: Int = 12,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "star_progress",
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        repeat(starCount) { i ->
            val angle = (i.toFloat() / starCount) * 360f + progress * 360f
            val radius = (progress * size.minDimension * 0.5f).coerceAtMost(size.minDimension * 0.45f)
            val x = center.x + kotlin.math.cos(Math.toRadians(angle.toDouble())).toFloat() * radius
            val y = center.y + sin(Math.toRadians(angle.toDouble())).toFloat() * radius
            drawCircle(
                color = confettiColors[i % confettiColors.size],
                radius = 12f * (1f - progress),
                center = Offset(x, y),
                alpha = 1f - progress,
            )
        }
    }
}
