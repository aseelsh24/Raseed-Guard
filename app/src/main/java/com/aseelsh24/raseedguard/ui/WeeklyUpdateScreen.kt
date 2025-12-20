package com.aseelsh24.raseedguard.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aseelsh24.raseedguard.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyUpdateScreen(
    onNavigateBack: () -> Unit,
    viewModel: WeeklyUpdateViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.title_weekly_update)) })
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Text(stringResource(R.string.prompt_enter_remaining_balance))

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.remainingAmount,
                onValueChange = { viewModel.updateRemainingAmount(it) },
                label = { Text(stringResource(R.string.label_remaining_balance_input)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = androidx.compose.ui.graphics.Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = {
                    viewModel.saveUpdate(onSuccess = onNavigateBack)
                },
                enabled = uiState.canSubmit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.action_confirm))
            }
        }
    }
}
