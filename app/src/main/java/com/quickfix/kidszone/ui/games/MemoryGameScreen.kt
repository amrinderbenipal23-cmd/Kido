package com.quickfix.kidszone.ui.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.quickfix.kidszone.ui.components.AnimatedButton
import com.quickfix.kidszone.ui.components.ConfettiAnimation
import com.quickfix.kidszone.ui.components.RewardDialog
import com.quickfix.kidszone.ui.theme.TextDark
import kotlinx.coroutines.delay

private val cardEmojis = listOf("🐶","🐱","🐘","🦁","🐧","🦜","🐬","🐰")

data class MemoryCard(
    val id: Int,
    val pairId: Int,
    val emoji: String,
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false,
)

@Composable
fun MemoryGameScreen(
    onBack: () -> Unit,
    viewModel: GameViewModel = hiltViewModel(),
) {
    var cards by remember { mutableStateOf(buildCards()) }
    var flippedCards by remember { mutableStateOf<List<Int>>(emptyList()) }
    var score by remember { mutableIntStateOf(0) }
    var moves by remember { mutableIntStateOf(0) }
    var showReward by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }

    fun resetGame() {
        cards = buildCards()
        flippedCards = emptyList()
        score = 0
        moves = 0
        showReward = false
        isProcessing = false
    }

    LaunchedEffect(flippedCards) {
        if (flippedCards.size == 2 && !isProcessing) {
            isProcessing = true
            val first = cards.first { it.id == flippedCards[0] }
            val second = cards.first { it.id == flippedCards[1] }
            moves++
            delay(800)
            if (first.pairId == second.pairId) {
                cards = cards.map {
                    if (it.id == first.id || it.id == second.id) it.copy(isMatched = true) else it
                }
                score += 10
                viewModel.playMatch()
            } else {
                cards = cards.map {
                    if (it.id == first.id || it.id == second.id) it.copy(isFlipped = false) else it
                }
                viewModel.playMiss()
            }
            flippedCards = emptyList()
            isProcessing = false
            if (cards.all { it.isMatched }) {
                showReward = true
                viewModel.onGameCompleted(3)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF1A1A2E), Color(0xFF16213E)))),
    ) {
        if (showReward) {
            ConfettiAnimation(modifier = Modifier.fillMaxSize())
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("⬅️", fontSize = 28.sp, modifier = Modifier.clickable(onClick = onBack))
                Spacer(Modifier.width(12.dp))
                Text("🃏 Memory Cards", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                Spacer(Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.End) {
                    Text("⭐ $score", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Moves: $moves", color = Color.White.copy(0.7f), fontSize = 14.sp)
                }
            }

            // Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f),
            ) {
                items(cards, key = { it.id }) { card ->
                    MemoryCardItem(
                        card = card,
                        onClick = {
                            if (!isProcessing && !card.isFlipped && !card.isMatched && flippedCards.size < 2) {
                                cards = cards.map { if (it.id == card.id) it.copy(isFlipped = true) else it }
                                flippedCards = flippedCards + card.id
                            }
                        },
                    )
                }
            }

            // Reset
            AnimatedButton(
                text = "New Game 🔄",
                onClick = ::resetGame,
                startColor = Color(0xFF845EC2),
                endColor = Color(0xFFFF6B9D),
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
            )
        }

        if (showReward) {
            RewardDialog(
                title = "Memory Master! 🎉",
                message = "Score: $score in $moves moves!",
                emoji = "🃏",
                starsEarned = 3,
                onDismiss = ::resetGame,
                onContinue = ::resetGame,
            )
        }
    }
}

@Composable
private fun MemoryCardItem(card: MemoryCard, onClick: () -> Unit) {
    val rotation by animateFloatAsState(
        targetValue = if (card.isFlipped || card.isMatched) 180f else 0f,
        animationSpec = tween(400, easing = EaseInOut),
        label = "card_flip",
    )

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (card.isMatched) Color(0xFF06D6A0)
                else if (card.isFlipped) Color(0xFF845EC2)
                else Color(0xFF2D2D60)
            )
            .clickable(enabled = !card.isMatched && !card.isFlipped, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (rotation > 90f) {
            Text(card.emoji, fontSize = 36.sp, textAlign = TextAlign.Center)
        } else {
            Text("❓", fontSize = 36.sp, textAlign = TextAlign.Center)
        }
    }
}

private fun buildCards(): List<MemoryCard> {
    val pairs = cardEmojis.mapIndexed { index, emoji ->
        listOf(
            MemoryCard(id = index * 2, pairId = index, emoji = emoji),
            MemoryCard(id = index * 2 + 1, pairId = index, emoji = emoji),
        )
    }.flatten()
    return pairs.shuffled()
}
