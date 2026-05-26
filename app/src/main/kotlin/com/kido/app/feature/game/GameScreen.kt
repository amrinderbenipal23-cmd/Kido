package com.kido.app.feature.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.kido.app.ui.components.LetterTile
import com.kido.app.ui.components.Mascot
import com.kido.app.ui.components.MascotMood
import com.kido.app.ui.components.StarRow
import com.kido.app.ui.theme.KidoColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    onExit: () -> Unit,
    viewModel: GameViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.start() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.game_title)) },
                navigationIcon = {
                    IconButton(onClick = onExit) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.game_cd_back_home),
                        )
                    }
                },
            )
        },
    ) { padding ->
        val s = state
        if (s == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.game_round_indicator, s.round, s.totalRounds),
                style = MaterialTheme.typography.titleLarge,
            )

            val mood = when (s.phase) {
                GamePhase.Correct -> MascotMood.Cheering
                GamePhase.Incorrect -> MascotMood.Thinking
                GamePhase.Complete -> MascotMood.Cheering
                GamePhase.Asking -> MascotMood.Happy
            }
            Mascot(
                modifier = Modifier.size(120.dp),
                mood = mood,
            )

            Text(
                text = when (s.phase) {
                    GamePhase.Asking -> stringResource(R.string.game_prompt_listen)
                    GamePhase.Correct -> stringResource(
                        R.string.game_feedback_correct,
                        s.currentLetter.glyph,
                        s.currentLetter.word,
                    )
                    GamePhase.Incorrect -> stringResource(R.string.game_feedback_incorrect)
                    GamePhase.Complete -> stringResource(R.string.game_feedback_complete)
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )

            if (s.phase == GamePhase.Asking) {
                IconButton(onClick = { viewModel.repeatPrompt() }) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = stringResource(R.string.game_cd_hear_again),
                        modifier = Modifier.size(48.dp),
                    )
                }
            }

            if (s.phase != GamePhase.Complete) {
                val palette = KidoColors.tilePalette
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    itemsIndexed(s.choices) { idx, letter ->
                        LetterTile(
                            glyph = letter.glyph,
                            onClick = { viewModel.onChoice(letter) },
                            background = palette[idx % palette.size],
                            enabled = s.phase == GamePhase.Asking,
                        )
                    }
                }
            } else {
                StarRow(
                    filled = s.starsThisSession.coerceAtMost(s.totalRounds),
                    total = s.totalRounds,
                )
                BigButton(
                    label = stringResource(R.string.game_action_done),
                    onClick = onExit,
                    color = KidoColors.Grass,
                )
            }
        }
    }
}
