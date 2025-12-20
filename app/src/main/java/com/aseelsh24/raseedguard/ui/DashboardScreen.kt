package com.aseelsh24.raseedguard.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aseelsh24.raseedguard.core.Plan
import com.aseelsh24.raseedguard.core.PlanType
import com.aseelsh24.raseedguard.core.PredictionResult
import com.aseelsh24.raseedguard.core.RiskLevel
import com.aseelsh24.raseedguard.core.Unit as PlanUnit
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToAddPlan: () -> Unit,
    onNavigateToEditPlan: (String) -> Unit,
    onNavigateToUpdate: () -> Unit,
    onNavigateToInsights: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: DashboardViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Raseed Guard") },
                actions = {
                    IconButton(onClick = onNavigateToInsights) {
                        Icon(Icons.Default.BarChart, contentDescription = "Insights")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddPlan,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Plan")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            when (val state = uiState) {
                is DashboardUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is DashboardUiState.Empty -> {
                    Text(
                        text = "No active plan. Add one!",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                is DashboardUiState.Success -> {
                    DashboardPlanCard(
                        plan = state.plan,
                        prediction = state.prediction,
                        onUpdateUsage = onNavigateToUpdate,
                        onEditPlan = { onNavigateToEditPlan(state.plan.id) }
                    )
                }
                is DashboardUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardPlanCard(
    plan: Plan,
    prediction: PredictionResult,
    onUpdateUsage: () -> Unit,
    onEditPlan: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 2.1 Plan header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: icon + label
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = getPlanIcon(plan.type),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = getPlanLabel(plan.type),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Right: Risk badge
                RiskBadge(prediction.riskLevel)
            }

            // 2.2 Metric row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left column: Remaining Balance
                Column {
                    Text(
                        text = formatBalance(prediction.remainingNormalized, plan.unit),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Remaining Balance",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Right column: Days Until Expiry
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${prediction.daysUntilEnd} days",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Days Until Expiry",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 2.3 Predicted depletion date line
            HorizontalDivider()

            val dateText = prediction.predictedDepletionAt?.let {
                it.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            } ?: "—"

            Text(
                text = "Predicted Depletion Date: $dateText",
                style = MaterialTheme.typography.bodyMedium
            )

            // 2.4 Action buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // "Update Usage"
                OutlinedButton(
                    onClick = onUpdateUsage,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Update Usage")
                }

                // "Edit Plan"
                FilledTonalButton(
                    onClick = onEditPlan,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Edit Plan")
                }
            }
        }
    }
}

@Composable
fun RiskBadge(riskLevel: RiskLevel) {
    val (containerColor, contentColor, text) = when (riskLevel) {
        RiskLevel.SAFE -> Triple(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer,
            "Safe"
        )
        RiskLevel.WARNING -> Triple(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer,
            "Warning"
        )
        RiskLevel.CRITICAL -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            "Critical"
        )
    }

    Surface(
        color = containerColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            color = contentColor,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

private fun getPlanIcon(type: PlanType): ImageVector {
    return when (type) {
        PlanType.INTERNET -> Icons.Default.Wifi
        PlanType.VOICE -> Icons.Default.Call
        PlanType.MIXED -> Icons.Default.Layers
    }
}

private fun getPlanLabel(type: PlanType): String {
    return when (type) {
        PlanType.INTERNET -> "Internet Plan"
        PlanType.VOICE -> "Voice Plan"
        PlanType.MIXED -> "Mixed Plan"
    }
}

private fun formatBalance(amount: Double, unit: PlanUnit): String {
    val unitStr = when (unit) {
        PlanUnit.MB -> "MB"
        PlanUnit.GB -> "GB"
        PlanUnit.MINUTES -> "min"
    }
    // Format to 1 decimal place or just integer if strict matching needed? Mock says "7.0 GB"
    return "%.1f %s".format(amount, unitStr)
}
