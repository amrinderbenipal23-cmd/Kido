package com.quickfix.kidszone.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickfix.kidszone.ui.components.AnimatedMascot
import com.quickfix.kidszone.ui.components.ConfettiAnimation
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToHome: () -> Unit) {

    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        showContent = true
        delay(2500)
        onNavigateToHome()
    }

    val alpha by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = tween(600),
        label = "splash_alpha",
    )

    val scale by animateFloatAsState(
        targetValue = if (showContent) 1f else 0.6f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "splash_scale",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        ConfettiAnimation(modifier = Modifier.fillMaxSize())

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(scale)
                .alpha(alpha),
        ) {
            AnimatedMascot(
                emoji = "🦉",
                size = 120.dp,
                isExcited = true,
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Kiddo",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 64.sp,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Learn",
                color = Color(0xFFFFD60A),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 64.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Fun. Smart. Safe.",
                color = Color.White.copy(alpha = 0.85f),
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                letterSpacing = 2.sp,
            )

            Spacer(Modifier.height(48.dp))

            LoadingDots()
        }
    }
}

@Composable
private fun LoadingDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(3) { index ->
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(500),
                    repeatMode = RepeatMode.Reverse,
                    initialStartOffset = StartOffset(index * 150),
                ),
                label = "dot_$index",
            )
            Box(
                modifier = Modifier
                    .scale(scale)
                    .size(12.dp)
                    .background(Color.White, shape = androidx.compose.foundation.shape.CircleShape),
            )
        }
    }
}
