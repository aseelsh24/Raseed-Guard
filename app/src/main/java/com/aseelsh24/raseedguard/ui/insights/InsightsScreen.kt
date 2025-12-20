package com.aseelsh24.raseedguard.ui.insights

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aseelsh24.raseedguard.core.BalanceLog
import com.aseelsh24.raseedguard.core.PredictionResult
import com.aseelsh24.raseedguard.core.Unit as PlanUnit
import com.aseelsh24.raseedguard.ui.AppViewModelProvider
import com.aseelsh24.raseedguard.ui.components.MetricCard
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    viewModel: InsightsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Usage Insights") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (val state = uiState) {
                is InsightsUiState.Loading -> {
                    Text("Loading...")
                }
                is InsightsUiState.NoActivePlan -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No active plan selected.")
                            Text("Add a plan first.", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                is InsightsUiState.Success -> {
                    val prediction = state.prediction
                    val unit = state.plan.unit

                    // Metric Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        MetricCard(
                            title = "Average Daily Usage",
                            value = formatRateWithUnit(prediction?.smoothedDailyRate, unit),
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Safe Daily Limit",
                            value = formatRateWithUnit(prediction?.safeDailyUsageTarget, unit),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    MetricCard(
                        title = "Predicted Depletion Date",
                        value = formatDate(prediction?.predictedDepletionAt),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Trend Chart
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Balance Trend",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            SimpleTrendChart(
                                logs = state.logs,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        }
                    }

                    // Warning Box
                    if (prediction != null && prediction.smoothedDailyRate > prediction.safeDailyUsageTarget) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Warning: Your usage rate is above the safe daily limit. Consider reducing usage.",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleTrendChart(logs: List<BalanceLog>, modifier: Modifier = Modifier) {
    if (logs.size < 2) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(
                text = "Not enough logs to show trend.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val sortedLogs = logs.sortedBy { it.loggedAt }
    val minAmount = sortedLogs.minOf { it.remainingAmount }
    val maxAmount = sortedLogs.maxOf { it.remainingAmount }
    val range = maxAmount - minAmount

    val primaryColor = MaterialTheme.colorScheme.primary

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
            color = primaryColor,
            style = Stroke(width = 3.dp.toPx())
        )
    }
}

fun formatDate(date: java.time.LocalDateTime?): String {
    if (date == null) return "—"
    return try {
        date.format(DateTimeFormatter.ofPattern("dd MMM", Locale.getDefault()))
    } catch (e: Exception) {
        date.toString()
    }
}

fun formatRateWithUnit(rate: Double?, unit: PlanUnit): String {
    if (rate == null) return "—"
    val unitString = when (unit) {
        PlanUnit.MB -> "MB"
        PlanUnit.GB -> "GB"
        PlanUnit.MINUTES -> "Min"
    }
    return "%.1f %s/day".format(rate, unitString)
}
