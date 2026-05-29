package com.quickfix.kidszone.ui.games

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quickfix.kidszone.domain.model.GameItem
import com.quickfix.kidszone.domain.model.GameType
import com.quickfix.kidszone.ui.theme.TextDark

@Composable
fun GamesScreen(
    onBack: () -> Unit,
    onMemoryGame: () -> Unit,
    onBalloonGame: () -> Unit,
    viewModel: GameViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFE8EAF6), Color(0xFFF3E5F5)))),
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
                Text("🎮 Games", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = TextDark)
            }

            Text(
                text = "Choose a game to play!",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextDark,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(uiState.games) { game ->
                    GameCard(
                        game = game,
                        onClick = {
                            when (game.type) {
                                GameType.MEMORY_CARDS -> onMemoryGame()
                                GameType.BALLOON_POP -> onBalloonGame()
                                else -> onMemoryGame()
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun GameCard(game: GameItem, onClick: () -> Unit) {
    val cardColor = Color(game.color)
    val difficultyText = when (game.difficulty) {
        com.quickfix.kidszone.domain.model.GameDifficulty.EASY -> "⭐ Easy"
        com.quickfix.kidszone.domain.model.GameDifficulty.MEDIUM -> "⭐⭐ Medium"
        com.quickfix.kidszone.domain.model.GameDifficulty.HARD -> "⭐⭐⭐ Hard"
    }

    Box(
        modifier = Modifier
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.verticalGradient(listOf(cardColor, cardColor.copy(alpha = 0.65f))))
            .clickable(onClick = onClick)
            .padding(16.dp)
            .height(140.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(game.emoji, fontSize = 44.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(6.dp))
            Text(game.title, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, textAlign = TextAlign.Center)
            Text(difficultyText, color = Color.White.copy(0.85f), fontSize = 13.sp)
        }
    }
}
