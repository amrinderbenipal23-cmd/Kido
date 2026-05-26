package com.kido.app.feature.parent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kido.app.R
import com.kido.app.ui.components.BigButton
import com.kido.app.ui.theme.KidoColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentGateScreen(
    onPassed: () -> Unit,
    onCancel: () -> Unit,
    viewModel: ParentGateViewModel = viewModel(),
) {
    var answer by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.parent_gate_title)) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_navigate_back),
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
            Text(
                text = stringResource(R.string.parent_gate_heading),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.parent_gate_question, viewModel.a, viewModel.b),
                style = MaterialTheme.typography.displaySmall,
            )
            OutlinedTextField(
                value = answer,
                onValueChange = {
                    answer = it.filter { ch -> ch.isDigit() }.take(3)
                    showError = false
                },
                singleLine = true,
                isError = showError,
                supportingText = if (showError) {
                    { Text(stringResource(R.string.parent_gate_error)) }
                } else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            BigButton(
                label = stringResource(R.string.parent_gate_action_continue),
                color = KidoColors.DeepPurple,
                onClick = {
                    if (viewModel.verify(answer)) onPassed() else {
                        showError = true
                        answer = ""
                    }
                },
            )
        }
    }
}
