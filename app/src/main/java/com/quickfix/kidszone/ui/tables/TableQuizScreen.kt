package com.quickfix.kidszone.ui.tables

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.quickfix.kidszone.data.models.TablesData
import com.quickfix.kidszone.ui.components.AnimatedButton
import com.quickfix.kidszone.ui.components.ConfettiAnimation
import com.quickfix.kidszone.ui.components.RewardDialog
import com.quickfix.kidszone.ui.theme.TextDark

@Composable
fun TableQuizScreen(
    tableNum: Int,
    onBack: () -> Unit,
    viewModel: TablesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val table = TablesData.tables.find { it.number == tableNum }
    val cardColor = table?.let { Color(it.color) } ?: Color(0xFF845EC2)

    LaunchedEffect(tableNum) { viewModel.startQuiz(tableNum) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFF0E6FF), Color(0xFFE8F4FF)))),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(cardColor, Color(0xFF845EC2))))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("⬅️", fontSize = 26.sp, modifier = Modifier.clickable(onClick = onBack))
                Spacer(Modifier.width(10.dp))
                Text(
                    "Quiz – Table of $tableNum",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    "Score: ${uiState.quizScore}/${uiState.quizTotal}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
            }

            if (uiState.quizFinished) {
                QuizResultScreen(
                    score = uiState.quizScore,
                    total = uiState.quizTotal,
                    cardColor = cardColor,
                    onRestart = { viewModel.restartQuiz(tableNum) },
                    onBack = onBack,
                )
            } else {
                val question = uiState.quizQuestion
                if (question != null) {
                    Spacer(Modifier.height(24.dp))

                    // Progress dots
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        repeat(10) { i ->
                            val done = i < uiState.quizTotal
                            val correct = done && i < uiState.quizScore
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .padding(1.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        when {
                                            correct -> Color(0xFF06D6A0)
                                            done -> Color(0xFFFF6B6B)
                                            else -> Color(0xFFDDD)
                                        }
                                    ),
                            )
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    // Question card
                    AnimatedContent(
                        targetState = question,
                        transitionSpec = { fadeIn() + slideInVertically { -it / 2 } togetherWith fadeOut() },
                        label = "question",
                    ) { q ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .shadow(16.dp, RoundedCornerShape(24.dp))
                                .clip(RoundedCornerShape(24.dp))
                                .background(Brush.verticalGradient(listOf(cardColor, cardColor.copy(0.7f))))
                                .padding(vertical = 32.dp, horizontal = 20.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🤔", fontSize = 48.sp)
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = "${q.tableNum} × ${q.multiplicand} = ?",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 40.sp,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    // Answer options
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        question.options.chunked(2).forEach { pair ->
                            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                pair.forEach { option ->
                                    OptionButton(
                                        value = option,
                                        isAnswered = uiState.quizAnswered,
                                        isCorrect = option == question.correctAnswer,
                                        wasSelected = uiState.quizAnswered && option == question.correctAnswer,
                                        cardColor = cardColor,
                                        onClick = { viewModel.answerQuiz(option) },
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                                if (pair.size == 1) Spacer(Modifier.weight(1f))
                            }
                        }
                    }

                    // Feedback
                    AnimatedVisibility(
                        visible = uiState.quizAnswered,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut(),
                    ) {
                        Text(
                            text = if (uiState.lastAnswerCorrect) "✅ Excellent! Great job!" else "❌ Oops! Try the next one!",
                            color = if (uiState.lastAnswerCorrect) Color(0xFF06D6A0) else Color(0xFFFF6B6B),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                        )
                    }
                }
            }
        }

        if (uiState.showReward) {
            ConfettiAnimation()
            RewardDialog(
                title = "Quiz Complete! 🏆",
                message = "You scored ${uiState.quizScore} out of ${uiState.quizTotal}!",
                emoji = "🎯",
                starsEarned = uiState.starsEarned,
                onDismiss = viewModel::dismissReward,
                onContinue = viewModel::dismissReward,
            )
        }
    }
}

@Composable
private fun OptionButton(
    value: Int,
    isAnswered: Boolean,
    isCorrect: Boolean,
    wasSelected: Boolean,
    cardColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor = when {
        isAnswered && isCorrect -> Color(0xFF06D6A0)
        else -> cardColor.copy(alpha = 0.15f)
    }
    val textColor = when {
        isAnswered && isCorrect -> Color.White
        else -> TextDark
    }
    val scale by animateFloatAsState(
        targetValue = if (wasSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "opt_scale",
    )

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .clickable(enabled = !isAnswered, onClick = onClick)
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = value.toString(),
            color = textColor,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 28.sp,
        )
    }
}

@Composable
private fun QuizResultScreen(
    score: Int,
    total: Int,
    cardColor: Color,
    onRestart: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = if (score >= total * 0.8) "🎉 Brilliant!" else if (score >= total * 0.5) "👍 Good Job!" else "💪 Keep Practising!",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextDark,
        )
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(70.dp))
                .background(Brush.radialGradient(listOf(cardColor, cardColor.copy(0.6f)))),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$score", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 48.sp)
                Text("/$total", color = Color.White.copy(0.8f), fontSize = 20.sp)
            }
        }
        Spacer(Modifier.height(12.dp))
        repeat(3) {
            Text(if (it < (score.toFloat() / total * 3).toInt()) "⭐" else "☆", fontSize = 36.sp)
        }
        Spacer(Modifier.height(32.dp))
        AnimatedButton(
            text = "🔄 Try Again",
            onClick = onRestart,
            startColor = cardColor,
            endColor = cardColor.copy(0.7f),
            modifier = Modifier.fillMaxWidth(),
            height = 56.dp,
        )
        Spacer(Modifier.height(12.dp))
        AnimatedButton(
            text = "🏠 Back to Tables",
            onClick = onBack,
            startColor = Color(0xFF845EC2),
            endColor = Color(0xFFFF6B9D),
            modifier = Modifier.fillMaxWidth(),
            height = 52.dp,
        )
    }
}
