package com.kido.app.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kido.app.ui.components.BigButton
import com.kido.app.ui.components.Mascot
import com.kido.app.ui.components.MascotMood
import com.kido.app.ui.theme.KidoColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onPlay: () -> Unit,
    onParentGate: () -> Unit,
    viewModel: HomeViewModel = viewModel(),
) {
    val profile by viewModel.profile.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kido") },
                actions = {
                    IconButton(onClick = onParentGate) {
                        Icon(Icons.Default.Settings, contentDescription = "Parent settings")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Mascot(
                modifier = Modifier.size(180.dp),
                mood = MascotMood.Happy,
            )
            Text(
                text = if (profile.name.isBlank()) "Hi, friend!" else "Hi, ${profile.name}!",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "You have ${profile.stars} ⭐",
                style = MaterialTheme.typography.headlineSmall,
            )
            BigButton(
                label = "Play Alphabets",
                onClick = onPlay,
                color = KidoColors.Berry,
            )
        }
    }
}
