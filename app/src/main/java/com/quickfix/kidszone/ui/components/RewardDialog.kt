package com.quickfix.kidszone.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun RewardDialog(
    title: String,
    message: String,
    emoji: String,
    starsEarned: Int,
    onDismiss: () -> Unit,
    onContinue: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "dialog_scale",
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .scale(scale)
                .padding(24.dp)
                .clip(RoundedCornerShape(36.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF845EC2), Color(0xFFFF6B9D))
                    )
                )
                .padding(32.dp),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Stars row
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    repeat(starsEarned.coerceAtMost(5)) {
                        Text("⭐", fontSize = 32.sp, modifier = Modifier.padding(2.dp))
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(text = emoji, fontSize = 72.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = message,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(24.dp))
                AnimatedButton(
                    text = "Continue! 🚀",
                    onClick = onContinue,
                    startColor = Color(0xFFFFBE0B),
                    endColor = Color(0xFFFF9671),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
fun SuccessBanner(
    message: String,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "banner")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "banner_scale",
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(listOf(Color(0xFF06D6A0), Color(0xFF4ECDC4))))
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp,
        )
    }
}
