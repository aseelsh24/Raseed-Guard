package com.aseelsh24.raseedguard.ui.insights

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aseelsh24.raseedguard.core.BalanceLog
import com.aseelsh24.raseedguard.core.PredictionResult
import com.aseelsh24.raseedguard.core.RiskLevel
import com.aseelsh24.raseedguard.ui.AppViewModelProvider
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    viewModel: InsightsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("إحصائيات") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (val state = uiState) {
                is InsightsUiState.Loading -> {
                    Text("جاري التحميل...")
                }
                is InsightsUiState.NoActivePlan -> {
                    Text("لا توجد خطة نشطة. يرجى إضافة خطة أو تحديد خطة نشطة.")
                }
                is InsightsUiState.Success -> {
                    if (state.prediction != null) {
                        PredictionCard(state.prediction)
                    } else {
                        Text("بيانات غير كافية لتقديم التوقعات.")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "تطور الرصيد",
                        style = MaterialTheme.typography.titleMedium
                    )
                    SimpleTrendChart(
                        logs = state.logs,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PredictionCard(prediction: PredictionResult) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "مستوى الخطر: ${riskLevelToString(prediction.riskLevel)}",
                color = riskLevelToColor(prediction.riskLevel),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("تاريخ النفاذ المتوقع: ${formatDate(prediction.predictedDepletionAt)}")
            Text("الاستهلاك اليومي (المعدل): ${formatRate(prediction.smoothedDailyRate)}")
            Text("الاستهلاك اليومي الآمن: ${formatRate(prediction.safeDailyUsageTarget)}")
        }
    }
}

@Composable
fun SimpleTrendChart(logs: List<BalanceLog>, modifier: Modifier = Modifier) {
    if (logs.size < 2) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text("أضف المزيد من السجلات لرؤية الرسم البياني.")
        }
        return
    }

    val sortedLogs = logs.sortedBy { it.loggedAt }
    // Normalize logic is inside UsagePredictor but for chart we just want to see trend.
    // Assuming simple doubling for visual if unit is different is tricky without Unit info available easily here
    // without Plan. But logs have raw amount.
    // Visualizing raw amounts might be misleading if mixed?
    // But BalanceLogs are usually for the same plan.
    // Let's assume raw amounts are fine for trend line of a single plan.

    val minAmount = sortedLogs.minOf { it.remainingAmount }
    val maxAmount = sortedLogs.maxOf { it.remainingAmount }
    val range = maxAmount - minAmount

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val points = sortedLogs.mapIndexed { index, log ->
            val x = if (sortedLogs.size > 1) {
                index * (width / (sortedLogs.size - 1))
            } else {
                0f
            }

            val y = if (range == 0.0) {
                height / 2
            } else {
                height - ((log.remainingAmount - minAmount) / range * height).toFloat()
            }
            Offset(x, y)
        }

        val path = Path().apply {
            if (points.isNotEmpty()) {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    lineTo(points[i].x, points[i].y)
                }
            }
        }

        drawPath(
            path = path,
            color = Color.Blue,
            style = Stroke(width = 4.dp.toPx())
        )
    }
}

fun riskLevelToString(level: RiskLevel): String {
    return when (level) {
        RiskLevel.SAFE -> "آمن"
        RiskLevel.WARNING -> "تحذير"
        RiskLevel.CRITICAL -> "حرج"
    }
}

fun riskLevelToColor(level: RiskLevel): Color {
    return when (level) {
        RiskLevel.SAFE -> Color.Green
        RiskLevel.WARNING -> Color(0xFFFF9800) // Orange
        RiskLevel.CRITICAL -> Color.Red
    }
}

fun formatDate(date: java.time.LocalDateTime?): String {
    if (date == null) return "غير متوفر"
    return try {
        date.format(DateTimeFormatter.ofPattern("dd MMM", Locale("ar")))
    } catch (e: Exception) {
        date.toString()
    }
}

fun formatRate(rate: Double): String {
    return "%.2f".format(rate)
}
