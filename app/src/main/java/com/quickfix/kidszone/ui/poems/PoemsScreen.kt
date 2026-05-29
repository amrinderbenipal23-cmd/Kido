package com.quickfix.kidszone.ui.poems

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
import com.quickfix.kidszone.data.models.PoemItem
import com.quickfix.kidszone.ui.components.BannerAdView
import com.quickfix.kidszone.ui.theme.TextDark

@Composable
fun PoemsScreen(
    onPoemSelected: (Int) -> Unit,
    onBack: () -> Unit,
    viewModel: PoemsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFFFF9E6), Color(0xFFFFF0F6)))),
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(Color(0xFFFA709A), Color(0xFFFEE140))))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("⬅️", fontSize = 26.sp, modifier = Modifier.clickable(onClick = onBack))
            Spacer(Modifier.width(10.dp))
            Text("🎵 Kids Poems", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
            Spacer(Modifier.weight(1f))
            Text("${uiState.poems.size} poems", color = Color.White.copy(0.85f), fontSize = 14.sp)
        }

        FloatingNotesRow()

        Text(
            "Choose a poem to sing! 🎶",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = TextDark,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
        )

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f),
        ) {
            items(uiState.poems) { poem ->
                PoemListCard(
                    poem = poem,
                    isFavorite = poem.id in uiState.favoriteIds,
                    onFavorite = { viewModel.toggleFavorite(poem.id) },
                    onClick = { onPoemSelected(poem.id) },
                )
            }
        }

        BannerAdView()
    }
}

@Composable
private fun FloatingNotesRow() {
    val infiniteTransition = rememberInfiniteTransition(label = "notes")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -12f,
        animationSpec = infiniteRepeatable(tween(1200, easing = EaseInOut), RepeatMode.Reverse),
        label = "notes_offset",
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .offset(y = offset.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        listOf("🎵", "🎶", "🎼", "🎵", "🎶").forEach {
            Text(it, fontSize = 20.sp)
        }
    }
}

@Composable
private fun PoemListCard(
    poem: PoemItem,
    isFavorite: Boolean,
    onFavorite: () -> Unit,
    onClick: () -> Unit,
) {
    val poemColor = Color(poem.color)
    val infiniteTransition = rememberInfiniteTransition(label = "poem_${poem.id}")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.03f,
        animationSpec = infiniteRepeatable(tween(1400, easing = EaseInOut), RepeatMode.Reverse),
        label = "p_scale",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(poemColor, poemColor.copy(0.7f))))
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(0.25f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(poem.emoji, fontSize = 32.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(poem.titleEn, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                Text(poem.titleHi, color = Color.White.copy(0.8f), fontSize = 13.sp)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(0.25f))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text("${poem.lines.size} lines", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    if (poem.isHindi) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(0.25f))
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                        ) {
                            Text("हिंदी", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (isFavorite) "❤️" else "🤍", fontSize = 22.sp, modifier = Modifier.clickable(onClick = onFavorite))
                Spacer(Modifier.height(4.dp))
                Text("▶", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
