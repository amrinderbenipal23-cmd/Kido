package com.quickfix.kidszone.ui.abc

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quickfix.kidszone.domain.model.Alphabet
import com.quickfix.kidszone.ui.components.*
import com.quickfix.kidszone.ui.theme.TextDark

@Composable
fun AbcScreen(
    onBack: () -> Unit,
    viewModel: AbcViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentAlphabet = uiState.alphabets.getOrNull(uiState.currentIndex)

    LaunchedEffect(uiState.currentIndex) {
        if (uiState.currentIndex == 0 && !uiState.isAutoPlaying) {
            viewModel.speakCurrentLetter()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFF0E6FF), Color(0xFFFFF0F6)))),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            AbcTopBar(
                onBack = onBack,
                currentIndex = uiState.currentIndex,
                total = uiState.alphabets.size,
                isAutoPlaying = uiState.isAutoPlaying,
                onAutoPlayToggle = viewModel::toggleAutoPlay,
            )

            // Main letter card
            currentAlphabet?.let { alphabet ->
                AnimatedContent(
                    targetState = alphabet,
                    transitionSpec = {
                        slideInHorizontally(initialOffsetX = { it }) togetherWith
                                slideOutHorizontally(targetOffsetX = { -it })
                    },
                    label = "letter_card",
                    modifier = Modifier.weight(1f),
                ) { alpha ->
                    LetterCard(
                        alphabet = alpha,
                        onClick = viewModel::speakCurrentLetter,
                        modifier = Modifier
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                            .fillMaxSize(),
                    )
                }
            }

            // Navigation buttons
            NavigationRow(
                onPrevious = viewModel::previous,
                onNext = viewModel::next,
                onSpeak = viewModel::speakCurrentLetter,
                canGoPrevious = uiState.currentIndex > 0,
                canGoNext = uiState.currentIndex < uiState.alphabets.size - 1,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            )

            // Alphabet strip at bottom
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp),
            ) {
                items(uiState.alphabets.size) { index ->
                    val isSelected = index == uiState.currentIndex
                    val alpha = uiState.alphabets[index]
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) Color(alpha.color)
                                else Color(alpha.color).copy(alpha = 0.3f)
                            )
                            .clickable { viewModel.navigateTo(index) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = alpha.capitalLetter,
                            color = if (isSelected) Color.White else TextDark,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                        )
                    }
                }
            }
        }

        // Reward dialog
        if (uiState.showReward) {
            RewardDialog(
                title = "Amazing! 🎉",
                message = "You learned all 26 alphabets! You are a superstar!",
                emoji = "🔤",
                starsEarned = 3,
                onDismiss = viewModel::dismissReward,
                onContinue = viewModel::dismissReward,
            )
        }
    }
}

@Composable
private fun LetterCard(
    alphabet: Alphabet,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cardColor = Color(alphabet.color)
    val infiniteTransition = rememberInfiniteTransition(label = "letter")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.04f,
        animationSpec = infiniteRepeatable(tween(900, easing = EaseInOut), RepeatMode.Reverse),
        label = "letter_scale",
    )

    BoxWithConstraints(
        modifier = modifier
            .shadow(16.dp, RoundedCornerShape(36.dp))
            .clip(RoundedCornerShape(36.dp))
            .background(Brush.verticalGradient(listOf(cardColor, cardColor.copy(alpha = 0.7f))))
            .clickable(onClick = onClick)
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        val compact = maxWidth < 380.dp
        val emojiSize: TextUnit = if (compact) 56.sp else 80.sp
        val capitalSize: TextUnit = if (compact) 64.sp else 90.sp
        val smallSize: TextUnit = if (compact) 52.sp else 72.sp
        val hindiSize: TextUnit = if (compact) 28.sp else 40.sp
        val exampleSize: TextUnit = if (compact) 17.sp else 22.sp

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        ) {
            Text(text = alphabet.imageEmoji, fontSize = emojiSize, textAlign = TextAlign.Center)

            Spacer(Modifier.height(if (compact) 6.dp else 12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = alphabet.capitalLetter,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = capitalSize,
                )
                Text(
                    text = alphabet.smallLetter,
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = smallSize,
                )
            }

            Spacer(Modifier.height(if (compact) 4.dp else 8.dp))

            Text(
                text = alphabet.hindiLetter,
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.Bold,
                fontSize = hindiSize,
            )

            Spacer(Modifier.height(if (compact) 8.dp else 12.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.25f))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${alphabet.capitalLetter} for ${alphabet.exampleWordEn}",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = exampleSize,
                    )
                    Text(
                        text = alphabet.exampleWordHi,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = if (compact) 14.sp else 18.sp,
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Tap to hear! 🔊",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
private fun AbcTopBar(
    onBack: () -> Unit,
    currentIndex: Int,
    total: Int,
    isAutoPlaying: Boolean,
    onAutoPlayToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.9f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("⬅️", fontSize = 28.sp, modifier = Modifier.clickable(onClick = onBack))
        Spacer(Modifier.width(12.dp))
        Text("🔤 ABC Learn", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = TextDark)
        Spacer(Modifier.weight(1f))
        Text(
            text = "${currentIndex + 1} / $total",
            color = TextDark,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        )
        Spacer(Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isAutoPlaying) Color(0xFFFF6B6B) else Color(0xFF06D6A0))
                .clickable(onClick = onAutoPlayToggle)
                .padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Text(
                text = if (isAutoPlaying) "⏹ Stop" else "▶ Auto",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
private fun NavigationRow(
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSpeak: () -> Unit,
    canGoPrevious: Boolean,
    canGoNext: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimatedButton(
            text = "◀ Prev",
            onClick = onPrevious,
            startColor = Color(0xFF845EC2),
            endColor = Color(0xFFFF6B9D),
            modifier = Modifier.weight(1f).padding(end = 6.dp),
            height = 56.dp,
            enabled = canGoPrevious,
        )
        BounceButton(
            emoji = "🔊",
            onClick = onSpeak,
            size = 60.dp,
            backgroundColor = Color(0xFFFFBE0B),
        )
        AnimatedButton(
            text = "Next ▶",
            onClick = onNext,
            startColor = Color(0xFF4ECDC4),
            endColor = Color(0xFF06D6A0),
            modifier = Modifier.weight(1f).padding(start = 6.dp),
            height = 56.dp,
            enabled = canGoNext,
        )
    }
}
