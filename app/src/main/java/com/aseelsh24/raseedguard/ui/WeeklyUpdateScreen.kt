package com.aseelsh24.raseedguard.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyUpdateScreen(
    onNavigateBack: () -> Unit,
    viewModel: WeeklyUpdateViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var remainingAmount by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("تحديث أسبوعي") })
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            OutlinedTextField(
                value = remainingAmount,
                onValueChange = { remainingAmount = it },
                label = { Text("الرصيد المتبقي") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.saveLog(remainingAmount)
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("تأكيد")
            }
        }
    }
}
