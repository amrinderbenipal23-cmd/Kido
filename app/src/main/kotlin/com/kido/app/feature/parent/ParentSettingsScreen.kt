package com.kido.app.feature.parent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kido.app.ui.components.BigButton
import com.kido.app.ui.theme.KidoColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentSettingsScreen(
    onBack: () -> Unit,
    viewModel: ParentSettingsViewModel = viewModel(),
) {
    val profile by viewModel.profile.collectAsState()
    val languages by viewModel.availableLanguages.collectAsState()

    var nameDraft by remember { mutableStateOf(profile.name) }
    LaunchedEffect(profile.name) { nameDraft = profile.name }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Parent settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            SectionTitle("Child's name")
            OutlinedTextField(
                value = nameDraft,
                onValueChange = { nameDraft = it.take(20) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            BigButton(
                label = "Save name",
                onClick = { viewModel.setName(nameDraft) },
                color = KidoColors.DeepPurple,
                enabled = nameDraft.isNotBlank() && nameDraft != profile.name,
            )

            HorizontalDivider()

            SectionTitle("Alphabet language")
            Text(
                text = "Current: ${profile.languageCode}",
                style = MaterialTheme.typography.bodyLarge,
            )
            LanguageChips(
                available = languages,
                selected = profile.languageCode,
                onSelect = viewModel::setLanguage,
            )
            Text(
                text = "More languages arrive as content packs are added to assets/content/alphabets/.",
                style = MaterialTheme.typography.bodySmall,
            )

            HorizontalDivider()

            SectionTitle("Progress")
            Text(
                text = "Stars earned: ${profile.stars}\nLetters completed: ${profile.lettersCompleted.size}",
                style = MaterialTheme.typography.bodyLarge,
            )
            BigButton(
                label = "Reset progress",
                onClick = { viewModel.resetProgress() },
                color = KidoColors.Cherry,
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LanguageChips(
    available: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
) {
    if (available.isEmpty()) {
        Text("Loading…", style = MaterialTheme.typography.bodyMedium)
        return
    }
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        available.forEach { code ->
            AssistChip(
                onClick = { onSelect(code) },
                label = { Text(if (code == selected) "✓ $code" else code) },
            )
        }
    }
}
