package com.aseelsh24.raseedguard.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aseelsh24.raseedguard.core.PredictionResult
import com.aseelsh24.raseedguard.core.RiskLevel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToAddPlan: () -> Unit,
    onNavigateToUpdate: () -> Unit,
    onNavigateToInsights: () -> Unit,
    viewModel: DashboardViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("لوحة التحكم") }) // Dashboard
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

            when (val state = uiState) {
                is DashboardUiState.Loading -> {
                    Text("جاري التحميل...", modifier = Modifier.padding(16.dp))
                }
                is DashboardUiState.Success -> {
                    PredictionCard(state.prediction)
                }
                is DashboardUiState.Error -> {
                    Text("خطأ: ${state.message}", color = Color.Red, modifier = Modifier.padding(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "إجراءات سريعة", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(8.dp)) {
                Button(onClick = onNavigateToAddPlan) {
                    Text("إضافة باقة")
                }
                Button(onClick = onNavigateToUpdate) {
                    Text("تحديث")
                }
            }
             Button(onClick = onNavigateToInsights, modifier = Modifier.padding(8.dp)) {
                Text("إحصائيات تفصيلية")
            }
        }
    }
}

@Composable
fun PredictionCard(prediction: PredictionResult) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (prediction.riskLevel) {
                RiskLevel.SAFE -> Color(0xFFE8F5E9) // Light Green
                RiskLevel.WARNING -> Color(0xFFFFF3E0) // Light Orange
                RiskLevel.CRITICAL -> Color(0xFFFFEBEE) // Light Red
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "المتبقي: %.2f".format(prediction.remainingNormalized),
                style = MaterialTheme.typography.headlineMedium
            )
            Text(text = "الأيام المتبقية: ${prediction.daysUntilEnd}")

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "الاستهلاك اليومي: %.2f".format(prediction.dailyRate))

            if (prediction.predictedDepletionAt != null) {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                Text(text = "تاريخ النفاد المتوقع: ${prediction.predictedDepletionAt.format(formatter)}")
            } else {
                Text(text = "تاريخ النفاد المتوقع: غير متوفر (بيانات غير كافية)")
            }

            Text(
                text = "مستوى الخطر: ${prediction.riskLevel}",
                style = MaterialTheme.typography.titleMedium,
                color = when (prediction.riskLevel) {
                    RiskLevel.SAFE -> Color(0xFF2E7D32)
                    RiskLevel.WARNING -> Color(0xFFEF6C00)
                    RiskLevel.CRITICAL -> Color(0xFFC62828)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "الاستهلاك اليومي الآمن: %.2f".format(prediction.safeDailyUsageTarget))
        }
    }
}
