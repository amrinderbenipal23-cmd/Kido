package com.quickfix.kidszone.ui.words

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.quickfix.kidszone.ui.components.AnimatedButton
import com.quickfix.kidszone.ui.components.ConfettiAnimation
import com.quickfix.kidszone.ui.theme.TextDark

@Composable
fun SentenceMakingScreen(
    onBack: () -> Unit,
    viewModel: WordsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sentence = uiState.sentences.getOrNull(uiState.currentSentenceIndex)

    LaunchedEffect(Unit) { viewModel.loadSentence(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFF0E6FF), Color(0xFFE8FFF5)))),
    ) {
        if (uiState.sentenceResult == SentenceResult.CORRECT) ConfettiAnimation()

        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(Color(0xFF845EC2), Color(0xFF11998E))))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("⬅️", fontSize = 26.sp, modifier = Modifier.clickable(onClick = onBack))
                Spacer(Modifier.width(10.dp))
                Text("✍️ Sentence Making", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                Spacer(Modifier.weight(1f))
                Text(
                    "${uiState.currentSentenceIndex + 1}/${uiState.sentences.size}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
            }

            Spacer(Modifier.height(16.dp))

            // Emoji and meaning
            if (sentence != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(sentence.emoji, fontSize = 56.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = sentence.meaningHi,
                        color = TextDark,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Answer tray — words placed by user
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .shadow(8.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            when (uiState.sentenceResult) {
                                SentenceResult.CORRECT -> Color(0xFF06D6A0).copy(0.15f)
                                SentenceResult.WRONG -> Color(0xFFFF6B6B).copy(0.12f)
                                else -> Color.White
                            }
                        )
                        .padding(16.dp)
                        .defaultMinSize(minHeight = 64.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    if (uiState.selectedWords.isEmpty()) {
                        Text(
                            "Tap words below to build the sentence ✨",
                            color = Color(0xFF888899),
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                        )
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp),
                        ) {
                            itemsIndexed(uiState.selectedWords) { _, word ->
                                WordChip(
                                    word = word,
                                    color = Color(0xFF845EC2),
                                    enabled = uiState.sentenceResult == SentenceResult.NONE,
                                    onClick = { viewModel.removeWord(word) },
                                )
                            }
                        }
                    }
                }

                // Feedback
                AnimatedVisibility(
                    visible = uiState.sentenceResult != SentenceResult.NONE,
                    enter = fadeIn() + expandVertically(),
                ) {
                    Text(
                        text = when (uiState.sentenceResult) {
                            SentenceResult.CORRECT -> "🎉 Excellent! \"${sentence.sentence}\""
                            SentenceResult.WRONG -> "❌ Not quite! Try again!"
                            else -> ""
                        },
                        color = if (uiState.sentenceResult == SentenceResult.CORRECT) Color(0xFF06D6A0) else Color(0xFFFF6B6B),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Word bank — shuffled
                Text(
                    "Word Bank",
                    color = TextDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
                Spacer(Modifier.height(8.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    itemsIndexed(uiState.shuffledWords) { _, word ->
                        WordChip(
                            word = word,
                            color = Color(0xFF11998E),
                            enabled = uiState.sentenceResult == SentenceResult.NONE,
                            onClick = { viewModel.selectWord(word) },
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                // Action buttons
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AnimatedButton(
                            text = "🔊 Hear",
                            onClick = viewModel::speakSentence,
                            startColor = Color(0xFFFFBE0B),
                            endColor = Color(0xFFFF9671),
                            modifier = Modifier.weight(1f),
                            height = 50.dp,
                        )
                        AnimatedButton(
                            text = "🔄 Reset",
                            onClick = viewModel::resetSentence,
                            startColor = Color(0xFF4ECDC4),
                            endColor = Color(0xFF06D6A0),
                            modifier = Modifier.weight(1f),
                            height = 50.dp,
                        )
                    }
                    if (uiState.sentenceResult == SentenceResult.NONE) {
                        AnimatedButton(
                            text = "✅ Check Answer",
                            onClick = viewModel::checkSentence,
                            startColor = Color(0xFF845EC2),
                            endColor = Color(0xFFFF6B9D),
                            modifier = Modifier.fillMaxWidth(),
                            height = 56.dp,
                            enabled = uiState.selectedWords.isNotEmpty(),
                        )
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            AnimatedButton(
                                text = "◀ Prev",
                                onClick = viewModel::previousSentence,
                                startColor = Color(0xFF845EC2),
                                endColor = Color(0xFFFF6B9D),
                                modifier = Modifier.weight(1f),
                                height = 52.dp,
                                enabled = uiState.currentSentenceIndex > 0,
                            )
                            AnimatedButton(
                                text = "Next ▶",
                                onClick = viewModel::nextSentence,
                                startColor = Color(0xFF11998E),
                                endColor = Color(0xFF06D6A0),
                                modifier = Modifier.weight(1f),
                                height = 52.dp,
                                enabled = uiState.currentSentenceIndex < uiState.sentences.size - 1,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WordChip(
    word: String,
    color: Color,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.95f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "chip_scale",
    )
    Box(
        modifier = Modifier
            .scale(scale)
            .shadow(6.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.horizontalGradient(listOf(color, color.copy(0.8f))))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(word, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
    }
}
