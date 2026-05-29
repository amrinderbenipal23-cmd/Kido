package com.quickfix.kidszone.ui.words

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.quickfix.kidszone.data.models.WordCategory
import com.quickfix.kidszone.ui.components.BannerAdView
import com.quickfix.kidszone.ui.theme.TextDark

@Composable
fun WordsScreen(
    onCategorySelected: (String) -> Unit,
    onSentenceMaking: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFE8FFF5), Color(0xFFF0FFF8)))),
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.95f))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("⬅️", fontSize = 28.sp, modifier = Modifier.clickable(onClick = onBack))
            Spacer(Modifier.width(12.dp))
            Text("📖 Words & Sentences", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = TextDark)
        }

        Text(
            text = "Choose a word category! 🌟",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = TextDark,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f),
        ) {
            items(WordCategory.entries) { category ->
                CategoryCard(
                    category = category,
                    onClick = { onCategorySelected(category.name) },
                )
            }
            item {
                SentenceCard(onClick = onSentenceMaking)
            }
        }

        BannerAdView()
    }
}

@Composable
private fun CategoryCard(category: WordCategory, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "cat_${category.name}")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.04f,
        animationSpec = infiniteRepeatable(tween(1000, easing = EaseInOut), RepeatMode.Reverse),
        label = "cat_scale",
    )
    val color = Color(category.colorHex)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .scale(scale)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(color, color.copy(alpha = 0.7f))))
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Text(
            text = category.emoji,
            fontSize = 32.sp,
            modifier = Modifier.align(Alignment.TopEnd),
        )
        Column(modifier = Modifier.align(Alignment.BottomStart)) {
            Text(
                text = category.displayName,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
            )
            Text(
                text = "Tap to learn!",
                color = Color.White.copy(0.8f),
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun SentenceCard(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF845EC2), Color(0xFFFF6B9D))))
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Text("✍️", fontSize = 32.sp, modifier = Modifier.align(Alignment.TopEnd))
        Column(modifier = Modifier.align(Alignment.BottomStart)) {
            Text("Sentence Making", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            Text("Build sentences!", color = Color.White.copy(0.8f), fontSize = 12.sp)
        }
    }
}
