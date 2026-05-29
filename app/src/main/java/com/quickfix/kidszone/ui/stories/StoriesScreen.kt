package com.quickfix.kidszone.ui.stories

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quickfix.kidszone.data.models.StoryItem
import com.quickfix.kidszone.ui.components.BannerAdView
import com.quickfix.kidszone.ui.theme.TextDark

@Composable
fun StoriesScreen(
    onStorySelected: (Int) -> Unit,
    onBack: () -> Unit,
    viewModel: StoriesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFFFF0F6), Color(0xFFF0E6FF)))),
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(Color(0xFFB24592), Color(0xFFF15F79))))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("⬅️", fontSize = 26.sp, modifier = Modifier.clickable(onClick = onBack))
            Spacer(Modifier.width(10.dp))
            Text("📚 Kids Stories", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
            Spacer(Modifier.weight(1f))
            Text("${uiState.stories.size} stories", color = Color.White.copy(0.85f), fontSize = 14.sp)
        }

        Text(
            text = "Choose a story to read! 🌙",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = TextDark,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        )

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.weight(1f),
        ) {
            items(uiState.stories) { story ->
                StoryCard(
                    story = story,
                    isFavorite = story.id in uiState.favoriteIds,
                    onFavorite = { viewModel.toggleFavorite(story.id) },
                    onClick = { onStorySelected(story.id) },
                )
            }
        }

        BannerAdView()
    }
}

@Composable
private fun StoryCard(
    story: StoryItem,
    isFavorite: Boolean,
    onFavorite: () -> Unit,
    onClick: () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "story_${story.id}")
    val emojiScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(1200, easing = EaseInOut), RepeatMode.Reverse),
        label = "s_scale",
    )
    val storyColor = Color(story.color)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(storyColor, storyColor.copy(0.7f))))
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Emoji
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(0.25f))
                    .scale(emojiScale),
                contentAlignment = Alignment.Center,
            ) {
                Text(story.emoji, fontSize = 38.sp)
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = story.titleEn,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                )
                Text(
                    text = story.titleHi,
                    color = Color.White.copy(0.85f),
                    fontSize = 15.sp,
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(0.25f))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(
                            "${story.pages.size} pages",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(0.25f))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text("📖 Read", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Favourite
            Text(
                text = if (isFavorite) "❤️" else "🤍",
                fontSize = 24.sp,
                modifier = Modifier.clickable(onClick = onFavorite),
            )
        }

        // Moral teaser
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(top = 56.dp),
        ) {
            Text(
                text = "Moral: ${story.moralEn}",
                color = Color.White.copy(0.75f),
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
