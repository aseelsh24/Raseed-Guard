package com.aseelsh24.raseedguard.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToAddPlan: () -> Unit,
    onNavigateToUpdate: () -> Unit,
    onNavigateToInsights: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("لوحة التحكم") }) // Arabic title for Dashboard
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Text(text = "قائمة الباقات") // List of plans
            Button(onClick = onNavigateToAddPlan) {
                Text("إضافة باقة") // Add Plan
            }
            Button(onClick = onNavigateToUpdate) {
                Text("تحديث أسبوعي") // Weekly Update
            }
            Button(onClick = onNavigateToInsights) {
                Text("إحصائيات") // Insights
            }
        }
    }
}
