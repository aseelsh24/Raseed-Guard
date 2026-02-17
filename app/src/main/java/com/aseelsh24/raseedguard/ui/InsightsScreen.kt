package com.aseelsh24.raseedguard.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aseelsh24.raseedguard.core.RiskLevel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    onNavigateBack: () -> Unit,
    viewModel: InsightsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إحصائيات تفصيلية") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "رجوع")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (val state = uiState) {
                is InsightsUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is InsightsUiState.Empty -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "لا توجد بيانات",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "يرجى إضافة باقة وتحديث الرصيد لعرض الإحصائيات",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                is InsightsUiState.Success -> {
                    // Risk Level Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = when (state.prediction.riskLevel) {
                                RiskLevel.SAFE -> androidx.compose.ui.graphics.Color(0xFFE8F5E9)
                                RiskLevel.WARNING -> androidx.compose.ui.graphics.Color(0xFFFFF3E0)
                                RiskLevel.CRITICAL -> androidx.compose.ui.graphics.Color(0xFFFFEBEE)
                            }
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "حالة الباقة",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = when (state.prediction.riskLevel) {
                                    RiskLevel.SAFE -> "✅ آمن - استهلاكك ضمن الحد الطبيعي"
                                    RiskLevel.WARNING -> "⚠️ تحذير - استهلاكك أعلى من المعتاد"
                                    RiskLevel.CRITICAL -> "🚨 خطر - رصيدك سينفد قريباً!"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = when (state.prediction.riskLevel) {
                                    RiskLevel.SAFE -> androidx.compose.ui.graphics.Color(0xFF2E7D32)
                                    RiskLevel.WARNING -> androidx.compose.ui.graphics.Color(0xFFEF6C00)
                                    RiskLevel.CRITICAL -> androidx.compose.ui.graphics.Color(0xFFC62828)
                                }
                            )
                        }
                    }

                    // Statistics Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            title = "إجمالي المستهلك",
                            value = "%.2f".format(state.totalConsumed),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "نسبة الاستهلاك",
                            value = "%.1f%%".format(state.consumptionPercentage),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            title = "المتبقي",
                            value = "%.2f".format(state.prediction.remainingNormalized),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "الأيام المتبقية",
                            value = "${state.prediction.daysUntilEnd}",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Daily Usage Card
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "الاستهلاك اليومي",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "الحالي",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "%.2f".format(state.prediction.dailyRate),
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "الآمن",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "%.2f".format(state.prediction.safeDailyUsageTarget),
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            if (state.prediction.dailyRate > state.prediction.safeDailyUsageTarget) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "💡 نصيحة: قلل استهلاكك اليومي إلى %.2f لتجنب نفاد الرصيد قبل نهاية الباقة".format(
                                        state.prediction.safeDailyUsageTarget
                                    ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    // Data Points Card
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "عدد القراءات",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = "${state.logsCount} قراءة",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                is InsightsUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "خطأ: ${state.message}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

