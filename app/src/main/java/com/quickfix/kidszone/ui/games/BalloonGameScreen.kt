package com.quickfix.kidszone.ui.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.quickfix.kidszone.ui.components.AnimatedButton
import com.quickfix.kidszone.ui.components.ConfettiAnimation
import com.quickfix.kidszone.ui.components.RewardDialog
import com.quickfix.kidszone.ui.theme.TextDark
import kotlinx.coroutines.delay
import kotlin.random.Random

private val balloonColors = listOf(
    Color(0xFFFF6B6B), Color(0xFF4ECDC4), Color(0xFFFFBE0B),
    Color(0xFFFF6B9D), Color(0xFF845EC2), Color(0xFF06D6A0),
)
private val balloonEmojis = listOf("A", "B", "C", "D", "E", "1", "2", "3", "4", "5")

data class Balloon(
    val id: Int,
    val label: String,
    val color: Color,
    val x: Float,
    val isPopped: Boolean = false,
)

@Composable
fun BalloonGameScreen(
    onBack: () -> Unit,
    viewModel: GameViewModel = hiltViewModel(),
) {
    var targetLabel by remember { mutableStateOf(balloonEmojis.random()) }
    var balloons by remember { mutableStateOf(generateBalloons()) }
    var score by remember { mutableIntStateOf(0) }
    var showReward by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    fun resetRound() {
        targetLabel = balloonEmojis.random()
        balloons = generateBalloons()
        message = ""
    }

    fun popBalloon(balloon: Balloon) {
        if (balloon.isPopped) return
        balloons = balloons.map { if (it.id == balloon.id) it.copy(isPopped = true) else it }
        if (balloon.label == targetLabel) {
            score += 10
            message = "🎉 Correct! Great pop!"
            if (score >= 50) {
                showReward = true
                viewModel.onGameCompleted(3)
            } else {
                resetRound()
            }
        } else {
            message = "💨 Wrong balloon! Try again!"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF87CEEB), Color(0xFFE0F7FF)))),
    ) {
        if (showReward) ConfettiAnimation(modifier = Modifier.fillMaxSize())

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.8f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("⬅️", fontSize = 28.sp, modifier = Modifier.clickable(onClick = onBack))
                Spacer(Modifier.width(12.dp))
                Text("🎈 Balloon Pop", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = TextDark)
                Spacer(Modifier.weight(1f))
                Text("⭐ $score", color = Color(0xFFFF6B35), fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
            }

            // Target label
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Pop the balloon with:", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextDark)
                    Text(
                        text = targetLabel,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 64.sp,
                        color = Color(0xFFFF6B6B),
                    )
                }
            }

            if (message.isNotEmpty()) {
                Text(
                    text = message,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = TextDark,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(4.dp),
                )
            }

            // Balloons area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                balloons.forEach { balloon ->
                    FloatingBalloon(
                        balloon = balloon,
                        onClick = { popBalloon(balloon) },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            AnimatedButton(
                text = "New Round 🔄",
                onClick = ::resetRound,
                startColor = Color(0xFF845EC2),
                endColor = Color(0xFFFF6B9D),
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
            )
        }

        if (showReward) {
            RewardDialog(
                title = "Balloon Master! 🎈",
                message = "You scored $score! Pop champion!",
                emoji = "🎈",
                starsEarned = 3,
                onDismiss = {
                    showReward = false
                    score = 0
                    resetRound()
                },
                onContinue = {
                    showReward = false
                    score = 0
                    resetRound()
                },
            )
        }
    }
}

@Composable
private fun FloatingBalloon(balloon: Balloon, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "balloon_${balloon.id}")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -20f,
        animationSpec = infiniteRepeatable(
            animation = tween((800 + balloon.id * 150), easing = EaseInOut),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "balloon_float",
    )

    val alpha by animateFloatAsState(
        targetValue = if (balloon.isPopped) 0f else 1f,
        animationSpec = tween(300),
        label = "balloon_alpha",
    )

    val scale by animateFloatAsState(
        targetValue = if (balloon.isPopped) 2f else 1f,
        animationSpec = tween(300),
        label = "balloon_pop_scale",
    )

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .offset(
                    x = (balloon.x * 280).dp,
                    y = (200 + balloon.id * 40 + offsetY).dp,
                )
                .scale(scale)
                .alpha(alpha)
                .size(75.dp)
                .background(balloon.color, CircleShape)
                .clickable(enabled = !balloon.isPopped, onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = balloon.label,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
            )
        }
    }
}

private fun generateBalloons(): List<Balloon> {
    val chosen = balloonEmojis.shuffled().take(6)
    return chosen.mapIndexed { index, label ->
        Balloon(
            id = index,
            label = label,
            color = balloonColors[index % balloonColors.size],
            x = Random.nextFloat() * 0.8f,
        )
    }
}
