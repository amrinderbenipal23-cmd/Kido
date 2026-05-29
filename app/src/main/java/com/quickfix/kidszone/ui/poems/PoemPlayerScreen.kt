package com.quickfix.kidszone.ui.poems

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.quickfix.kidszone.ui.components.ConfettiAnimation
import com.quickfix.kidszone.ui.components.RewardDialog
import com.quickfix.kidszone.ui.theme.TextDark

@Composable
fun PoemPlayerScreen(
    poemId: Int,
    onBack: () -> Unit,
    viewModel: PoemsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(poemId) { viewModel.openPoem(poemId) }

    val poem = uiState.selectedPoem
    val poemColor = poem?.let { Color(it.color) } ?: Color(0xFFFA709A)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFFFF9E6), Color(0xFFFFF0F6)))),
    ) {
        if (uiState.showReward) ConfettiAnimation()

        if (poem != null) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(listOf(poemColor, Color(0xFFFEE140))))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⬅️", fontSize = 26.sp, modifier = Modifier.clickable {
                            viewModel.stopPoem(); onBack()
                        })
                        Spacer(Modifier.width(8.dp))
                        Text(poem.emoji, fontSize = 26.sp)
                        Spacer(Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(poem.titleEn, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                            Text(poem.titleHi, color = Color.White.copy(0.85f), fontSize = 13.sp)
                        }
                        // Repeat toggle
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (uiState.isRepeatOn) Color.White.copy(0.4f) else Color.White.copy(0.2f))
                                .clickable { viewModel.toggleRepeat() }
                                .padding(horizontal = 9.dp, vertical = 5.dp),
                        ) {
                            Text("🔁", fontSize = 16.sp)
                        }
                        Spacer(Modifier.width(8.dp))
                        // Favourite
                        Text(
                            if (poem.id in uiState.favoriteIds) "❤️" else "🤍",
                            fontSize = 22.sp,
                            modifier = Modifier.clickable { viewModel.toggleFavorite(poem.id) },
                        )
                    }
                }

                // Floating notes animation
                FloatingMusicNotes(poemColor)

                // Karaoke lyrics
                val listState = rememberLazyListState()
                LaunchedEffect(uiState.currentLineIndex) {
                    if (uiState.currentLineIndex >= 0) {
                        listState.animateScrollToItem(uiState.currentLineIndex)
                    }
                }

                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    itemsIndexed(poem.lines) { index, line ->
                        KaraokeLine(
                            text = line.text,
                            isActive = index == uiState.currentLineIndex,
                            isDone = index < uiState.currentLineIndex,
                            poemColor = poemColor,
                            onClick = { viewModel.speakLine(index) },
                        )
                    }
                }

                // Controls
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Stop
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .shadow(8.dp, RoundedCornerShape(14.dp))
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFFF6B6B))
                            .clickable { viewModel.stopPoem() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("⏹", fontSize = 22.sp)
                    }

                    // Play / Pause
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(58.dp)
                            .shadow(12.dp, RoundedCornerShape(18.dp))
                            .clip(RoundedCornerShape(18.dp))
                            .background(Brush.horizontalGradient(listOf(poemColor, poemColor.copy(0.7f))))
                            .clickable { viewModel.playPoem() },
                        contentAlignment = Alignment.Center,
                    ) {
                        val playScale by animateFloatAsState(
                            targetValue = if (uiState.isPlaying) 1.1f else 1f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                            label = "play_scale",
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.scale(playScale),
                        ) {
                            Text(if (uiState.isPlaying) "⏸" else "▶", fontSize = 24.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                if (uiState.isPlaying) "Pause" else "Play Poem",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                            )
                        }
                    }
                }
            }
        }

        if (uiState.showReward) {
            RewardDialog(
                title = "Poem Complete! 🎵",
                message = "You listened to \"${poem?.titleEn}\"! Wonderful!",
                emoji = poem?.emoji ?: "🎶",
                starsEarned = 2,
                onDismiss = viewModel::dismissReward,
                onContinue = viewModel::dismissReward,
            )
        }
    }
}

@Composable
private fun KaraokeLine(
    text: String,
    isActive: Boolean,
    isDone: Boolean,
    poemColor: Color,
    onClick: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.06f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "line_scale",
    )
    val bgBrush = when {
        isActive -> Brush.horizontalGradient(listOf(poemColor, poemColor.copy(0.75f)))
        isDone -> Brush.horizontalGradient(listOf(poemColor.copy(0.2f), poemColor.copy(0.1f)))
        else -> Brush.horizontalGradient(listOf(Color.White, Color(0xFFF8F8FF)))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(if (isActive) 10.dp else 3.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(bgBrush)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isActive) Text("🎵 ", fontSize = 18.sp)
            Text(
                text = text,
                color = when {
                    isActive -> Color.White
                    isDone -> poemColor.copy(0.6f)
                    else -> TextDark
                },
                fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium,
                fontSize = if (isActive) 20.sp else 17.sp,
                textAlign = TextAlign.Center,
            )
            if (isActive) Text(" 🎵", fontSize = 18.sp)
        }
    }
}

@Composable
private fun FloatingMusicNotes(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "music_notes")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -10f,
        animationSpec = infiniteRepeatable(tween(1000, easing = EaseInOut), RepeatMode.Reverse),
        label = "notes_y",
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .offset(y = offset.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        listOf("🎵", "🎶", "🎼", "🎵", "🎶", "⭐").forEach {
            Text(it, fontSize = 18.sp, color = color)
        }
    }
}
