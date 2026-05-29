package com.quickfix.kidszone.ui.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quickfix.kidszone.ui.theme.TextDark

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFF0E6FF), Color(0xFFFFF0F6)))),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
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
                Text("⚙️ Settings", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = TextDark)
            }

            Spacer(Modifier.height(16.dp))

            // Audio section
            SettingsSection(title = "🔊 Audio") {
                SettingsRow("Sound Effects", uiState.soundEnabled, viewModel::setSoundEnabled)
                SettingsRow("Background Music 🎵", uiState.musicEnabled, viewModel::setMusicEnabled)
                SettingsRow("Voice Interaction 🎤", uiState.voiceEnabled, viewModel::setVoiceEnabled)
            }

            Spacer(Modifier.height(12.dp))

            // Language section
            SettingsSection(title = "🌍 Language") {
                LanguageSelector(
                    selected = uiState.language,
                    onSelect = viewModel::setLanguage,
                )
            }

            Spacer(Modifier.height(12.dp))

            // Child name section
            SettingsSection(title = "👦 Child Name") {
                ChildNameField(
                    name = uiState.childName,
                    onNameChange = viewModel::setChildName,
                )
            }

            Spacer(Modifier.height(12.dp))

            // App info
            SettingsSection(title = "ℹ️ About") {
                InfoRow("Version", "1.0.0")
                InfoRow("For Kids", "Age 3–8 years")
                InfoRow("COPPA", "Compliant ✅")
                InfoRow("Content", "100% Safe 🛡️")
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.9f))
            .padding(16.dp),
    ) {
        Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = TextDark)
        Spacer(Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun SettingsRow(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = TextDark, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF845EC2)),
        )
    }
}

@Composable
private fun LanguageSelector(selected: String, onSelect: (String) -> Unit) {
    val languages = listOf("en" to "🇬🇧 English", "hi" to "🇮🇳 हिंदी")
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        languages.forEach { (code, label) ->
            val isSelected = selected == code
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) Color(0xFF845EC2) else Color(0xFFF0E6FF))
                    .clickable { onSelect(code) }
                    .padding(horizontal = 20.dp, vertical = 10.dp),
            ) {
                Text(label, color = if (isSelected) Color.White else TextDark, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun ChildNameField(name: String, onNameChange: (String) -> Unit) {
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("Child's name") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF845EC2),
            focusedLabelColor = Color(0xFF845EC2),
        ),
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = TextDark.copy(alpha = 0.6f), fontSize = 15.sp, modifier = Modifier.weight(1f))
        Text(value, color = TextDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}
