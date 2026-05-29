package com.quickfix.kidszone.ui.animals

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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quickfix.kidszone.domain.model.Animal
import com.quickfix.kidszone.domain.model.AnimalCategory
import com.quickfix.kidszone.ui.components.*
import com.quickfix.kidszone.ui.theme.TextDark

@Composable
fun AnimalScreen(
    onBack: () -> Unit,
    viewModel: AnimalViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFFFF8E1), Color(0xFFFFF3E0)))),
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
                Text("🐾 Animals", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = TextDark)
                Spacer(Modifier.weight(1f))
                if (!uiState.isQuizMode) {
                    AnimatedButton(
                        text = "Quiz! 🧠",
                        onClick = viewModel::startQuiz,
                        startColor = Color(0xFFFF6B9D),
                        endColor = Color(0xFF845EC2),
                        height = 44.dp,
                    )
                } else {
                    AnimatedButton(
                        text = "Browse",
                        onClick = viewModel::stopQuiz,
                        startColor = Color(0xFF4ECDC4),
                        endColor = Color(0xFF06D6A0),
                        height = 44.dp,
                    )
                }
            }

            // Category filter
            if (!uiState.isQuizMode) {
                CategoryFilterRow(
                    selected = uiState.selectedCategory,
                    onSelect = viewModel::filterByCategory,
                )
            }

            // Content
            AnimatedContent(
                targetState = uiState.isQuizMode,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "animal_content",
            ) { isQuiz ->
                if (isQuiz) {
                    QuizContent(
                        question = uiState.quizQuestion,
                        options = uiState.quizOptions,
                        result = uiState.quizResult,
                        score = uiState.score,
                        onAnswer = viewModel::answerQuiz,
                        modifier = Modifier.fillMaxSize().padding(20.dp),
                    )
                } else {
                    AnimalGrid(
                        animals = uiState.filteredAnimals,
                        playingId = uiState.playingSound,
                        onAnimalClick = viewModel::selectAnimal,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }

        // Animal detail dialog
        uiState.selectedAnimal?.let { animal ->
            AnimalDetailDialog(animal = animal, onDismiss = viewModel::clearSelected)
        }

        // Reward dialog
        if (uiState.showReward) {
            RewardDialog(
                title = "Animal Expert! 🐾",
                message = "Score: ${uiState.score}/5 — Brilliant!",
                emoji = "🦁",
                starsEarned = 2,
                onDismiss = viewModel::dismissReward,
                onContinue = viewModel::dismissReward,
            )
        }
    }
}

@Composable
private fun CategoryFilterRow(
    selected: AnimalCategory?,
    onSelect: (AnimalCategory?) -> Unit,
) {
    val categories = listOf(
        null to "All 🐾",
        AnimalCategory.FARM to "Farm 🐄",
        AnimalCategory.WILD to "Wild 🦁",
        AnimalCategory.BIRDS to "Birds 🐦",
        AnimalCategory.SEA to "Sea 🐬",
        AnimalCategory.PETS to "Pets 🐶",
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        categories.forEach { (cat, label) ->
            val isSelected = cat == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) Color(0xFFFFBE0B) else Color.White.copy(alpha = 0.8f))
                    .clickable { onSelect(cat) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun AnimalGrid(
    animals: List<Animal>,
    playingId: Int?,
    onAnimalClick: (Animal) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 130.dp),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier,
    ) {
        items(animals, key = { it.id }) { animal ->
            AnimalCardItem(
                animal = animal,
                isPlaying = playingId == animal.id,
                onClick = { onAnimalClick(animal) },
            )
        }
    }
}

@Composable
private fun AnimalCardItem(
    animal: Animal,
    isPlaying: Boolean,
    onClick: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (isPlaying) 1.08f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "animal_scale",
    )
    val cardColor = Color(animal.color)

    Box(
        modifier = Modifier
            .scale(scale)
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.verticalGradient(listOf(cardColor, cardColor.copy(alpha = 0.6f))))
            .clickable(onClick = onClick)
            .padding(12.dp)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(animal.emoji, fontSize = 48.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(4.dp))
            Text(animal.nameEn, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.Center)
            Text(animal.nameHi, color = Color.White.copy(0.85f), fontSize = 12.sp)
            if (isPlaying) Text("🔊", fontSize = 16.sp)
        }
    }
}

@Composable
private fun QuizContent(
    question: Animal?,
    options: List<Animal>,
    result: Boolean?,
    score: Int,
    onAnswer: (Animal) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Score: $score 🌟", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextDark)
        Spacer(Modifier.height(16.dp))
        question?.let {
            Text("Who says...", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = TextDark)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFFF9671))
                    .padding(horizontal = 24.dp, vertical = 12.dp),
            ) {
                Text("\"${it.soundDescription}\"", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp)
            }
        }
        Spacer(Modifier.height(24.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(options.size) { index ->
                val animal = options[index]
                val isCorrect = result == true && animal.id == question?.id
                val isWrong = result == false && animal.id == question?.id
                Box(
                    modifier = Modifier
                        .shadow(6.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            when {
                                isCorrect -> Color(0xFF06D6A0)
                                isWrong -> Color(0xFFFF4757)
                                else -> Color(animal.color)
                            }
                        )
                        .clickable(enabled = result == null) { onAnswer(animal) }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(animal.emoji, fontSize = 40.sp)
                        Text(animal.nameEn, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
        result?.let {
            Spacer(Modifier.height(12.dp))
            SuccessBanner(if (it) "Correct! Great job! 🎉" else "Try again! 💪")
        }
    }
}

@Composable
private fun AnimalDetailDialog(animal: Animal, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(32.dp))
                .background(Brush.verticalGradient(listOf(Color(animal.color), Color(animal.color).copy(0.7f))))
                .padding(28.dp),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(animal.emoji, fontSize = 80.sp, textAlign = TextAlign.Center)
                Text(animal.nameEn, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp)
                Text(animal.nameHi, color = Color.White.copy(0.85f), fontSize = 22.sp)
                Spacer(Modifier.height(8.dp))
                Text("Says: \"${animal.soundDescription}\"", color = Color.White, fontSize = 18.sp)
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(0.2f))
                        .padding(12.dp),
                ) {
                    Text("💡 ${animal.funFact}", color = Color.White, fontSize = 15.sp, textAlign = TextAlign.Center)
                }
                Spacer(Modifier.height(16.dp))
                AnimatedButton("Close ✕", onDismiss, startColor = Color.White.copy(0.3f), endColor = Color.White.copy(0.15f), textColor = Color.White)
            }
        }
    }
}
