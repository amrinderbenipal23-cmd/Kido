package com.quickfix.kidszone.ui.parent

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quickfix.kidszone.domain.model.Progress
import com.quickfix.kidszone.ui.components.AnimatedButton
import com.quickfix.kidszone.ui.theme.TextDark

@Composable
fun ParentDashboardScreen(
    onBack: () -> Unit,
    viewModel: ParentViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF1A1A2E), Color(0xFF2D2D44)))),
    ) {
        if (!uiState.isUnlocked) {
            MathLockScreen(
                question = uiState.mathQuestion,
                answer = uiState.inputAnswer,
                showError = uiState.showError,
                onAnswerChange = viewModel::onAnswerChange,
                onSubmit = viewModel::submitAnswer,
                onBack = onBack,
            )
        } else {
            ParentDashboard(
                uiState = uiState,
                onChildNameChange = viewModel::setChildName,
                onSoundToggle = viewModel::setSoundEnabled,
                onMusicToggle = viewModel::setMusicEnabled,
                onAdsToggle = viewModel::setAdsEnabled,
                onLock = viewModel::lock,
                onBack = onBack,
            )
        }
    }
}

@Composable
private fun MathLockScreen(
    question: String,
    answer: String,
    showError: Boolean,
    onAnswerChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp),
        ) {
            Text("⬅️", fontSize = 28.sp, modifier = Modifier.align(Alignment.Start).clickable(onClick = onBack))
            Spacer(Modifier.height(16.dp))
            Text("👨‍👩‍👧 Parent Zone", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp)
            Spacer(Modifier.height(8.dp))
            Text("Solve the math to continue", color = Color.White.copy(0.7f), fontSize = 16.sp)
            Spacer(Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF845EC2))
                    .padding(24.dp),
            ) {
                Text(question, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 48.sp)
            }

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = answer,
                onValueChange = onAnswerChange,
                label = { Text("Your answer", color = Color.White.copy(0.7f)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = showError,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF4ECDC4),
                    unfocusedBorderColor = Color.White.copy(0.5f),
                    errorBorderColor = Color(0xFFFF6B6B),
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            if (showError) {
                Text("❌ Wrong! Try again with a new question.", color = Color(0xFFFF6B6B), fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
            }

            Spacer(Modifier.height(16.dp))

            AnimatedButton(
                text = "Unlock 🔓",
                onClick = onSubmit,
                startColor = Color(0xFF4ECDC4),
                endColor = Color(0xFF06D6A0),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ParentDashboard(
    uiState: ParentUiState,
    onChildNameChange: (String) -> Unit,
    onSoundToggle: (Boolean) -> Unit,
    onMusicToggle: (Boolean) -> Unit,
    onAdsToggle: (Boolean) -> Unit,
    onLock: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("⬅️", fontSize = 28.sp, modifier = Modifier.clickable(onClick = onBack))
            Spacer(Modifier.width(12.dp))
            Text("👨‍👩‍👧 Parent Dashboard", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            Spacer(Modifier.weight(1f))
            Text("🔒 Lock", color = Color(0xFFFF6B6B), fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.clickable(onClick = onLock))
        }

        // Stats
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF2D2D60))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            StatItem("⭐", "${uiState.totalStars}", "Stars")
            StatItem("🪙", "${uiState.totalCoins}", "Coins")
            StatItem("⏱️", "${uiState.screenTimeMinutes}m", "Screen Time")
        }

        Spacer(Modifier.height(16.dp))

        // Progress section
        Text("📊 Learning Progress", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(8.dp))

        uiState.allProgress.forEach { progress ->
            ProgressItem(progress = progress, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
        }
        if (uiState.allProgress.isEmpty()) {
            Text("No progress yet — start learning! 🚀", color = Color.White.copy(0.6f), fontSize = 14.sp, modifier = Modifier.padding(16.dp))
        }

        Spacer(Modifier.height(16.dp))

        // Settings
        Text("⚙️ Settings", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(8.dp))

        SettingsToggle("🔊 Sound Effects", uiState.soundEnabled, onSoundToggle)
        SettingsToggle("🎵 Background Music", uiState.musicEnabled, onMusicToggle)
        SettingsToggle("📢 Show Ads", uiState.adsEnabled, onAdsToggle)

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun StatItem(emoji: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 24.sp)
        Text(value, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
        Text(label, color = Color.White.copy(0.7f), fontSize = 12.sp)
    }
}

@Composable
private fun ProgressItem(progress: Progress, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF2D2D60))
            .padding(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(progress.moduleName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f))
            Text("${progress.completedItems}/${progress.totalItems}", color = Color.White.copy(0.7f), fontSize = 14.sp)
            Spacer(Modifier.width(8.dp))
            repeat(progress.stars) { Text("⭐", fontSize = 14.sp) }
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress.percentage },
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(50)),
            color = Color(0xFF4ECDC4),
            trackColor = Color.White.copy(0.2f),
        )
    }
}

@Composable
private fun SettingsToggle(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF2D2D60))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF4ECDC4)),
        )
    }
}
