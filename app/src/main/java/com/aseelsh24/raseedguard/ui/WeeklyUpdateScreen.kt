package com.aseelsh24.raseedguard.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aseelsh24.raseedguard.core.Plan
import com.aseelsh24.raseedguard.core.PlanType
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyUpdateScreen(
    onNavigateBack: () -> Unit,
    viewModel: WeeklyUpdateViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val remainingAmount by viewModel.remainingAmount.collectAsState()
    val updateError by viewModel.updateError.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()

    var selectedPlan by remember { mutableStateOf<Plan?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تحديث الرصيد") },
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
                is WeeklyUpdateUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is WeeklyUpdateUiState.Empty -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "لا توجد باقات",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "يرجى إضافة باقة أولاً قبل تحديث الرصيد",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                is WeeklyUpdateUiState.Success -> {
                    Text(
                        text = "اختر الباقة",
                        style = MaterialTheme.typography.titleMedium
                    )

                    // Plan selection
                    state.plans.forEach { plan ->
                        PlanSelectionCard(
                            plan = plan,
                            isSelected = selectedPlan == plan,
                            onClick = {
                                selectedPlan = plan
                                viewModel.onPlanSelected(plan)
                            }
                        )
                    }

                    if (selectedPlan != null) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        Text(
                            text = "الرصيد المتبقي الحالي",
                            style = MaterialTheme.typography.titleMedium
                        )

                        OutlinedTextField(
                            value = remainingAmount,
                            onValueChange = { viewModel.onRemainingAmountChanged(it) },
                            label = { Text("الكمية المتبقية") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            isError = updateError != null,
                            supportingText = updateError?.let { { Text(it) } },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { viewModel.saveUpdate(onNavigateBack) },
                            enabled = !isSaving,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(if (isSaving) "جاري الحفظ..." else "حفظ التحديث")
                        }
                    }
                }

                is WeeklyUpdateUiState.Error -> {
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
fun PlanSelectionCard(
    plan: Plan,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = when (plan.type) {
                        PlanType.INTERNET -> "📡 إنترنت"
                        PlanType.VOICE -> "📞 مكالمات"
                        PlanType.MIXED -> "🔀 مختلط"
                    },
                    style = MaterialTheme.typography.titleMedium
                )
                
                if (isSelected) {
                    Text(
                        text = "✓ محدد",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "الكمية الأولية: ${plan.initialAmount} ${
                    when (plan.unit) {
                        com.aseelsh24.raseedguard.core.Unit.MB -> "MB"
                        com.aseelsh24.raseedguard.core.Unit.GB -> "GB"
                        com.aseelsh24.raseedguard.core.Unit.MINUTES -> "دقيقة"
                    }
                }",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "من ${plan.startAt.format(dateFormatter)} إلى ${plan.endAt.format(dateFormatter)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

