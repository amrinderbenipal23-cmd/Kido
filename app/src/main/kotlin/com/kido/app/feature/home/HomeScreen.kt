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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kido.app.R
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
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onParentGate) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = stringResource(R.string.home_cd_parent_settings),
                        )
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
                text = if (profile.name.isBlank()) {
                    stringResource(R.string.home_greeting_default)
                } else {
                    stringResource(R.string.home_greeting_named, profile.name)
                },
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.home_stars_count, profile.stars),
                style = MaterialTheme.typography.headlineSmall,
            )
            BigButton(
                label = stringResource(R.string.home_action_play_alphabets),
                onClick = onPlay,
                color = KidoColors.Berry,
            )
        }
    }
}
