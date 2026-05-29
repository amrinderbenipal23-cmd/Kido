package com.quickfix.kidszone.ui.numbers

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quickfix.kidszone.domain.model.NumberItem
import com.quickfix.kidszone.ui.components.*
import com.quickfix.kidszone.ui.theme.TextDark

@Composable
fun NumberScreen(
    onBack: () -> Unit,
    viewModel: NumberViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val current = uiState.numbers.getOrNull(uiState.currentIndex)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFE8F8F5), Color(0xFFF0FFF4)))),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.9f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("⬅️", fontSize = 28.sp, modifier = Modifier.clickable(onClick = onBack))
                Spacer(Modifier.width(12.dp))
                Text("🔢 Numbers", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = TextDark)
                Spacer(Modifier.weight(1f))
                Text(
                    "${uiState.currentIndex + 1} / ${uiState.numbers.size}",
                    color = TextDark, fontWeight = FontWeight.Bold, fontSize = 16.sp,
                )
            }

            current?.let { number ->
                AnimatedContent(
                    targetState = number,
                    transitionSpec = {
                        fadeIn() + slideInVertically { it / 2 } togetherWith
                                fadeOut() + slideOutVertically { -it / 2 }
                    },
                    label = "number_card",
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                ) { num ->
                    NumberCard(
                        number = num,
                        onCountClick = viewModel::countAnimation,
                        onSpeakClick = viewModel::speakCurrentNumber,
                        isAnimating = uiState.isCountingAnimating,
                        modifier = Modifier.padding(20.dp).fillMaxSize(),
                    )
                }
            }

            // Nav buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AnimatedButton(
                    text = "◀ Prev",
                    onClick = viewModel::previous,
                    startColor = Color(0xFF4ECDC4),
                    endColor = Color(0xFF00C9A7),
                    modifier = Modifier.weight(1f).padding(end = 6.dp),
                    height = 56.dp,
                    enabled = uiState.currentIndex > 0,
                )
                BounceButton("🔊", viewModel::speakCurrentNumber, size = 60.dp, backgroundColor = Color(0xFFFFBE0B))
                AnimatedButton(
                    text = "Next ▶",
                    onClick = viewModel::next,
                    startColor = Color(0xFF4ECDC4),
                    endColor = Color(0xFF06D6A0),
                    modifier = Modifier.weight(1f).padding(start = 6.dp),
                    height = 56.dp,
                    enabled = uiState.currentIndex < uiState.numbers.size - 1,
                )
            }

            // Number strip
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp),
            ) {
                items(uiState.numbers.size) { index ->
                    val num = uiState.numbers[index]
                    val isSelected = index == uiState.currentIndex
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) Color(num.color)
                                else Color(num.color).copy(alpha = 0.3f)
                            )
                            .clickable { viewModel.previous() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "${num.value}",
                            color = if (isSelected) Color.White else TextDark,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                        )
                    }
                }
            }
        }

        if (uiState.showReward) {
            RewardDialog(
                title = "Counting Master! 🎉",
                message = "You counted to ${uiState.numbers.size}! Amazing!",
                emoji = "🔢",
                starsEarned = 3,
                onDismiss = viewModel::dismissReward,
                onContinue = viewModel::dismissReward,
            )
        }
    }
}

@Composable
private fun NumberCard(
    number: NumberItem,
    onCountClick: () -> Unit,
    onSpeakClick: () -> Unit,
    isAnimating: Boolean,
    modifier: Modifier = Modifier,
) {
    val cardColor = Color(number.color)
    val scale by animateFloatAsState(
        targetValue = if (isAnimating) 1.06f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "num_scale",
    )

    BoxWithConstraints(
        modifier = modifier
            .scale(scale)
            .shadow(16.dp, RoundedCornerShape(36.dp))
            .clip(RoundedCornerShape(36.dp))
            .background(Brush.verticalGradient(listOf(cardColor, cardColor.copy(alpha = 0.65f))))
            .clickable(onClick = onSpeakClick),
        contentAlignment = Alignment.Center,
    ) {
        val compact = maxWidth < 380.dp
        val numberSize = if (compact) 80.sp else 120.sp
        val wordEnSize = if (compact) 26.sp else 36.sp
        val wordHiSize = if (compact) 20.sp else 28.sp
        val starSize = if (compact) 22.sp else 28.sp

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = number.value.toString(),
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = numberSize,
                textAlign = TextAlign.Center,
            )

            Text(text = number.wordEn, color = Color.White, fontWeight = FontWeight.Bold, fontSize = wordEnSize)
            Text(text = number.wordHi, color = Color.White.copy(alpha = 0.85f), fontWeight = FontWeight.Bold, fontSize = wordHiSize)

            Spacer(Modifier.height(if (compact) 8.dp else 16.dp))

            FlowRow(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                repeat(number.value.coerceAtMost(10)) {
                    Text("⭐", fontSize = starSize, modifier = Modifier.padding(2.dp))
                }
            }

            Spacer(Modifier.height(if (compact) 8.dp else 16.dp))

            AnimatedButton(
                text = if (isAnimating) "Counting... 🎵" else "Count! 🔢",
                onClick = onCountClick,
                startColor = Color.White.copy(alpha = 0.3f),
                endColor = Color.White.copy(alpha = 0.15f),
                textColor = Color.White,
                height = 52.dp,
                enabled = !isAnimating,
            )
        }
    }
}
