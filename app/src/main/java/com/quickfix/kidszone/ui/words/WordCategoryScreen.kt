package com.quickfix.kidszone.ui.words

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quickfix.kidszone.data.models.WordCategory
import com.quickfix.kidszone.data.models.WordItem
import com.quickfix.kidszone.ui.components.AnimatedButton
import com.quickfix.kidszone.ui.components.RewardDialog
import com.quickfix.kidszone.ui.components.VoicePracticeRow
import com.quickfix.kidszone.ui.theme.TextDark

@Composable
fun WordCategoryScreen(
    categoryName: String,
    onBack: () -> Unit,
    viewModel: WordsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val category = runCatching { WordCategory.valueOf(categoryName) }.getOrNull()
    val context = LocalContext.current

    val micLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted -> if (granted) viewModel.startVoice() }

    fun onVoiceToggle() {
        if (uiState.isListening) {
            viewModel.stopVoice()
        } else {
            val granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.RECORD_AUDIO,
            ) == PackageManager.PERMISSION_GRANTED
            if (granted) viewModel.startVoice() else micLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    LaunchedEffect(categoryName) {
        category?.let { viewModel.selectCategory(it) }
    }

    val cardColor = category?.let { Color(it.colorHex) } ?: Color(0xFF11998E)
    val words = uiState.wordsInCategory
    val currentWord = words.getOrNull(uiState.currentWordIndex)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFE8FFF5), Color(0xFFF0FFF8)))),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(cardColor, cardColor.copy(0.7f))))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⬅️", fontSize = 26.sp, modifier = Modifier.clickable(onClick = onBack))
                    Spacer(Modifier.width(8.dp))
                    Text(category?.emoji ?: "📖", fontSize = 26.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        category?.displayName ?: "Words",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                    )
                    Spacer(Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(0.25f))
                            .clickable { viewModel.toggleLanguage() }
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                    ) {
                        Text(
                            if (uiState.isHindi) "हिंदी" else "EN",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (uiState.isAutoPlaying) Color(0xFFFF6B6B).copy(0.8f) else Color.White.copy(0.25f))
                            .clickable { viewModel.toggleAutoPlay() }
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                    ) {
                        Text(
                            if (uiState.isAutoPlaying) "⏹" else "▶",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                        )
                    }
                }
            }

            // Word card
            if (currentWord != null) {
                AnimatedContent(
                    targetState = currentWord,
                    transitionSpec = {
                        slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                    },
                    label = "word_card",
                    modifier = Modifier.weight(1f),
                ) { word ->
                    WordCard(
                        word = word,
                        isHindi = uiState.isHindi,
                        cardColor = cardColor,
                        onClick = viewModel::speakCurrentWord,
                        modifier = Modifier.padding(20.dp).fillMaxSize(),
                    )
                }
            }

            // Progress counter
            Text(
                text = "${uiState.currentWordIndex + 1} / ${words.size}",
                color = TextDark,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            // Word strip
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp),
            ) {
                items(words) { word ->
                    val isSelected = words.indexOf(word) == uiState.currentWordIndex
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) cardColor else cardColor.copy(0.3f))
                            .clickable {
                                val idx = words.indexOf(word)
                                if (idx >= 0) {
                                    repeat(idx - uiState.currentWordIndex) { viewModel.nextWord() }
                                }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(word.emoji, fontSize = 20.sp, textAlign = TextAlign.Center)
                    }
                }
            }

            // Nav buttons
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                AnimatedButton(
                    text = "◀ Prev",
                    onClick = viewModel::previousWord,
                    startColor = cardColor.copy(0.7f),
                    endColor = cardColor,
                    modifier = Modifier.weight(1f),
                    height = 52.dp,
                    enabled = uiState.currentWordIndex > 0,
                )
                AnimatedButton(
                    text = "🔊",
                    onClick = viewModel::speakCurrentWord,
                    startColor = Color(0xFFFFBE0B),
                    endColor = Color(0xFFFF9671),
                    modifier = Modifier.weight(0.6f),
                    height = 52.dp,
                )
                AnimatedButton(
                    text = "Next ▶",
                    onClick = viewModel::nextWord,
                    startColor = cardColor,
                    endColor = cardColor.copy(0.7f),
                    modifier = Modifier.weight(1f),
                    height = 52.dp,
                    enabled = uiState.currentWordIndex < words.size - 1,
                )
            }

            // Voice / speaking practice
            VoicePracticeRow(
                isListening = uiState.isListening,
                feedback = uiState.voiceFeedback,
                prompt = currentWord?.let { "Tap the mic and say \"${it.wordEn}\"!" } ?: "",
                onToggle = ::onVoiceToggle,
                accent = cardColor,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }

        if (uiState.showReward) {
            RewardDialog(
                title = "Category Complete! 🎉",
                message = "You learned all ${category?.displayName} words!",
                emoji = category?.emoji ?: "🌟",
                starsEarned = uiState.starsEarned,
                onDismiss = viewModel::dismissReward,
                onContinue = viewModel::dismissReward,
            )
        }
    }
}

@Composable
private fun WordCard(
    word: WordItem,
    isHindi: Boolean,
    cardColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "word_bounce")
    val emojiScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(800, easing = EaseInOut), RepeatMode.Reverse),
        label = "emoji_scale",
    )

    Box(
        modifier = modifier
            .shadow(16.dp, RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.verticalGradient(listOf(cardColor, cardColor.copy(0.7f))))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
        ) {
            Text(
                text = word.emoji,
                fontSize = 80.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.scale(emojiScale),
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = word.wordEn,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 38.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            if (isHindi) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(0.25f))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = word.wordHi,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        textAlign = TextAlign.Center,
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(0.2f))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            ) {
                Text(
                    text = word.exampleEn,
                    color = Color.White.copy(0.9f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(Modifier.height(10.dp))
            Text("Tap to hear! 🔊", color = Color.White.copy(0.7f), fontSize = 14.sp)
        }
    }
}
