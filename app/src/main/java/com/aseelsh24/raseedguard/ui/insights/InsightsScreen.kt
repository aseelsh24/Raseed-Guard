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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aseelsh24.raseedguard.R
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
            TopAppBar(
                title = { Text(stringResource(R.string.title_usage_insights)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
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
                    Text(stringResource(R.string.loading))
                }
                is InsightsUiState.NoActivePlan -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.insights_no_active_plan))
                            Text(stringResource(R.string.insights_add_plan_hint), style = MaterialTheme.typography.bodySmall)
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
                            title = stringResource(R.string.label_average_daily_usage),
                            value = formatRateWithUnit(prediction?.smoothedDailyRate, unit),
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = stringResource(R.string.label_safe_daily_limit),
                            value = formatRateWithUnit(prediction?.safeDailyUsageTarget, unit),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    MetricCard(
                        title = stringResource(R.string.label_predicted_depletion_date_short),
                        value = formatDate(prediction?.predictedDepletionAt),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Trend Chart
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(R.string.title_balance_trend),
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
                                    text = stringResource(R.string.warning_usage_rate_high),
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
                text = stringResource(R.string.chart_not_enough_data),
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

@Composable
fun formatRateWithUnit(rate: Double?, unit: PlanUnit): String {
    if (rate == null) return "—"
    val displayRate = com.aseelsh24.raseedguard.core.UnitConverter.rateFromNormalizedMbPerDay(rate, unit)
    val unitString = when (unit) {
        PlanUnit.MB -> stringResource(R.string.unit_mb)
        PlanUnit.GB -> stringResource(R.string.unit_gb)
        PlanUnit.MINUTES -> stringResource(R.string.unit_min)
    }
    return stringResource(R.string.format_rate_per_day, displayRate, unitString)
}
