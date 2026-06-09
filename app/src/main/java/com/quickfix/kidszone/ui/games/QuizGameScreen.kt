package com.quickfix.kidszone.ui.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import com.quickfix.kidszone.domain.model.GameType
import com.quickfix.kidszone.ui.components.AnimatedButton
import com.quickfix.kidszone.ui.components.ConfettiAnimation
import com.quickfix.kidszone.ui.components.RewardDialog

@Composable
fun QuizGameScreen(
    gameType: GameType,
    onBack: () -> Unit,
    viewModel: QuizGameViewModel = hiltViewModel(),
) {
    LaunchedEffect(gameType) { viewModel.start(gameType) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val round = state.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF1A1A2E), Color(0xFF16213E)))),
    ) {
        if (state.finished) {
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
                Text(
                    "${state.headerEmoji} ${state.title}",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                )
                Spacer(Modifier.weight(1f))
                Text("⭐ ${state.score}", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            // Progress
            if (state.total > 0) {
                LinearProgressIndicatorBar(
                    progress = (state.currentIndex + if (state.answered) 1 else 0).toFloat() / state.total,
                )
                Text(
                    "Question ${state.currentIndex + 1} of ${state.total}",
                    color = Color.White.copy(0.7f),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                )
            }

            if (round != null) {
                // Prompt
                Text(
                    round.prompt,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                )

                // Display panel
                AnimatedContent(
                    targetState = state.currentIndex,
                    transitionSpec = {
                        (slideInHorizontally { it } + fadeIn()) togetherWith
                                (slideOutHorizontally { -it } + fadeOut())
                    },
                    label = "quiz_display",
                ) { _ ->
                    DisplayPanel(content = round.display, isRow = round.displayIsRow)
                }

                Spacer(Modifier.height(8.dp))

                // Options (2x2)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    round.options.chunked(2).forEachIndexed { rowIdx, rowOpts ->
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            rowOpts.forEachIndexed { colIdx, label ->
                                val index = rowIdx * 2 + colIdx
                                OptionButton(
                                    label = label,
                                    state = optionState(state, index, round.correctIndex),
                                    onClick = { viewModel.answer(index) },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }

                // Feedback line
                if (state.answered) {
                    Text(
                        if (state.lastCorrect) "🎉 Correct!" else "💪 Keep trying!",
                        color = if (state.lastCorrect) Color(0xFF06D6A0) else Color(0xFFFFBE0B),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Spacer(Modifier.weight(1f))
            }
        }

        if (state.finished) {
            RewardDialog(
                title = "Well Done! 🎉",
                message = "You scored ${state.score} out of ${state.total}!",
                emoji = state.headerEmoji,
                starsEarned = state.starsEarned,
                onDismiss = viewModel::playAgain,
                onContinue = viewModel::playAgain,
            )
        }
    }
}

private enum class OptState { IDLE, CORRECT, WRONG, DIMMED }

private fun optionState(state: QuizUiState, index: Int, correctIndex: Int): OptState {
    if (!state.answered) return OptState.IDLE
    return when {
        index == correctIndex -> OptState.CORRECT
        index == state.selectedIndex -> OptState.WRONG
        else -> OptState.DIMMED
    }
}

@Composable
private fun LinearProgressIndicatorBar(progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(8.dp)
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(0.15f)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .clip(RoundedCornerShape(50))
                .background(Brush.horizontalGradient(listOf(Color(0xFF4ECDC4), Color(0xFF06D6A0)))),
        )
    }
}

@Composable
private fun DisplayPanel(content: String, isRow: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF2D2D60), Color(0xFF1F1F44))))
            .padding(20.dp)
            .heightIn(min = 140.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (isRow) {
            // Wrap emojis so larger counts stay on screen
            FlowEmojis(content)
        } else {
            Text(content, fontSize = 96.sp, textAlign = TextAlign.Center)
        }
    }
}

/** Simple wrapping row of emoji characters. */
@Composable
private fun FlowEmojis(content: String) {
    val items = content.map { it.toString() }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items.chunked(3).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowItems.forEach { Text(it, fontSize = 52.sp) }
            }
        }
    }
}

@Composable
private fun OptionButton(
    label: String,
    state: OptState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scale by animateFloatAsState(
        targetValue = if (state == OptState.CORRECT) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "opt_scale",
    )
    val colors = when (state) {
        OptState.IDLE -> listOf(Color(0xFF845EC2), Color(0xFFFF6B9D))
        OptState.CORRECT -> listOf(Color(0xFF06D6A0), Color(0xFF4ECDC4))
        OptState.WRONG -> listOf(Color(0xFFFF6B6B), Color(0xFFFF4757))
        OptState.DIMMED -> listOf(Color(0xFF3A3A55), Color(0xFF2D2D44))
    }

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(colors))
            .clickable(enabled = state == OptState.IDLE, onClick = onClick)
            .heightIn(min = 72.dp)
            .padding(12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = if (label.length <= 2) 34.sp else 20.sp,
            textAlign = TextAlign.Center,
        )
    }
}
