package com.quickfix.kidszone.ui.stories

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quickfix.kidszone.data.models.StoryPage
import com.quickfix.kidszone.ui.components.AnimatedButton
import com.quickfix.kidszone.ui.components.ConfettiAnimation
import com.quickfix.kidszone.ui.components.RewardDialog

@Composable
fun StoryReaderScreen(
    storyId: Int,
    onBack: () -> Unit,
    viewModel: StoriesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(storyId) { viewModel.openStory(storyId) }

    val story = uiState.selectedStory
    val storyColor = story?.let { Color(it.color) } ?: Color(0xFFB24592)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFFFF0F6), Color(0xFFF0E6FF)))),
    ) {
        if (uiState.showReward) ConfettiAnimation()

        if (story != null) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(listOf(storyColor, Color(0xFFF15F79))))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⬅️", fontSize = 26.sp, modifier = Modifier.clickable(onClick = onBack))
                        Spacer(Modifier.width(8.dp))
                        Text(story.emoji, fontSize = 26.sp)
                        Spacer(Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(story.titleEn, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                            Text(story.titleHi, color = Color.White.copy(0.85f), fontSize = 13.sp)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(0.2f))
                                .clickable { viewModel.toggleLanguage() }
                                .padding(horizontal = 9.dp, vertical = 5.dp),
                        ) {
                            Text(if (uiState.isHindi) "हिंदी" else "EN", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (uiState.isAutoNarrating) Color(0xFFFF6B6B).copy(0.8f) else Color.White.copy(0.2f))
                                .clickable { viewModel.toggleAutoNarrate() }
                                .padding(horizontal = 9.dp, vertical = 5.dp),
                        ) {
                            Text(
                                if (uiState.isAutoNarrating) "⏹" else "▶ Auto",
                                color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp,
                            )
                        }
                    }
                }

                // Page progress
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    story.pages.forEachIndexed { index, _ ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(5.dp)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (index <= uiState.currentPageIndex) storyColor
                                    else storyColor.copy(0.25f)
                                ),
                        )
                    }
                }

                if (uiState.showMoral) {
                    MoralCard(story.moralEn, story.moralHi, uiState.isHindi, storyColor)
                } else {
                    // Story page
                    AnimatedContent(
                        targetState = uiState.currentPage,
                        transitionSpec = {
                            slideInHorizontally { it } + fadeIn() togetherWith
                                    slideOutHorizontally { -it } + fadeOut()
                        },
                        label = "story_page",
                        modifier = Modifier.weight(1f),
                    ) { page ->
                        if (page != null) {
                            StoryPageContent(
                                page = page,
                                isHindi = uiState.isHindi,
                                storyColor = storyColor,
                                onClick = viewModel::speakCurrentPage,
                                modifier = Modifier.fillMaxSize().padding(16.dp),
                            )
                        }
                    }
                }

                // Navigation
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    AnimatedButton(
                        text = "◀ Prev",
                        onClick = viewModel::previousPage,
                        startColor = storyColor.copy(0.7f),
                        endColor = storyColor,
                        modifier = Modifier.weight(1f),
                        height = 52.dp,
                        enabled = uiState.currentPageIndex > 0 || uiState.showMoral,
                    )
                    AnimatedButton(
                        text = "🔊",
                        onClick = viewModel::speakCurrentPage,
                        startColor = Color(0xFFFFBE0B),
                        endColor = Color(0xFFFF9671),
                        modifier = Modifier.weight(0.6f),
                        height = 52.dp,
                    )
                    AnimatedButton(
                        text = if (uiState.currentPageIndex >= story.pages.size - 1 && !uiState.showMoral) "Moral ▶" else "Next ▶",
                        onClick = viewModel::nextPage,
                        startColor = storyColor,
                        endColor = storyColor.copy(0.7f),
                        modifier = Modifier.weight(1f),
                        height = 52.dp,
                        enabled = !uiState.showMoral,
                    )
                }
            }
        }

        if (uiState.showReward) {
            RewardDialog(
                title = "Story Complete! 🌟",
                message = "You read \"${story?.titleEn}\"! Great reading!",
                emoji = story?.emoji ?: "📚",
                starsEarned = uiState.starsEarned,
                onDismiss = viewModel::dismissReward,
                onContinue = viewModel::dismissReward,
            )
        }
    }
}

@Composable
private fun StoryPageContent(
    page: StoryPage,
    isHindi: Boolean,
    storyColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "page_anim")
    val emojiScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = EaseInOut), RepeatMode.Reverse),
        label = "p_emoji",
    )

    Box(
        modifier = modifier
            .shadow(12.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(rememberScrollState()).padding(24.dp),
        ) {
            // Page number
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(storyColor.copy(0.15f))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
            ) {
                Text(
                    "Page ${page.pageNumber}",
                    color = storyColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                )
            }
            Spacer(Modifier.height(16.dp))

            // Illustration emoji
            Text(
                text = page.emoji,
                fontSize = 88.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.scale(emojiScale),
            )
            Spacer(Modifier.height(20.dp))

            // Story text
            val text = if (isHindi) page.textHi else page.textEn
            Text(
                text = text,
                color = Color(0xFF1A1A2E),
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                lineHeight = 30.sp,
            )

            if (isHindi) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = page.textEn,
                    color = Color(0xFF888899),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                )
            }

            Spacer(Modifier.height(12.dp))
            Text("Tap to hear! 🔊", color = Color(0xFF888899), fontSize = 13.sp)
        }
    }
}

@Composable
private fun MoralCard(
    moralEn: String,
    moralHi: String,
    isHindi: Boolean,
    storyColor: Color,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "moral")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.03f,
        animationSpec = infiniteRepeatable(tween(1000, easing = EaseInOut), RepeatMode.Reverse),
        label = "moral_scale",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.55f)
            .padding(16.dp)
            .scale(scale)
            .shadow(16.dp, RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.verticalGradient(listOf(storyColor, Color(0xFFF15F79)))),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp),
        ) {
            Text("🌟", fontSize = 64.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                "Moral of the Story",
                color = Color.White.copy(0.85f),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = if (isHindi) moralHi else moralEn,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                lineHeight = 30.sp,
            )
            if (isHindi) {
                Spacer(Modifier.height(8.dp))
                Text(moralEn, color = Color.White.copy(0.8f), fontSize = 16.sp, textAlign = TextAlign.Center)
            }
        }
    }
}
