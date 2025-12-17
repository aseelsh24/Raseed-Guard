package com.aseelsh24.raseedguard.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aseelsh24.raseedguard.core.Plan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyUpdateScreen(
    onNavigateBack: () -> Unit,
    viewModel: WeeklyUpdateViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var remainingAmount by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    var selectedPlan by remember { mutableStateOf<Plan?>(null) }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("تحديث أسبوعي") })
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {

            when (val state = uiState) {
                is WeeklyUpdateUiState.Loading -> {
                    Text("جاري التحميل...")
                }
                is WeeklyUpdateUiState.Success -> {
                    val plans = state.plans
                    if (plans.isEmpty()) {
                        Text("لا توجد باقات متاحة. الرجاء إضافة باقة أولاً.")
                    } else {
                        // Dropdown for Plan Selection
                         Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = selectedPlan?.let { "${it.type.name} - ${it.unit.name}" } ?: "اختر الباقة",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Expand",
                                        modifier = Modifier.clickable { expanded = true }
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                plans.forEach { plan ->
                                    DropdownMenuItem(
                                        text = { Text("${plan.type.name} - ${plan.unit.name} (${plan.initialAmount} ${plan.unit.name})") },
                                        onClick = {
                                            selectedPlan = plan
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Set initial selection if not set and plans exist
                        LaunchedEffect(plans) {
                             if (selectedPlan == null && plans.isNotEmpty()) {
                                 selectedPlan = plans.first()
                             }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = remainingAmount,
                            onValueChange = { remainingAmount = it },
                            label = { Text("الرصيد المتبقي") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                selectedPlan?.let { plan ->
                                    viewModel.saveLog(plan.id, remainingAmount)
                                    onNavigateBack()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = selectedPlan != null && remainingAmount.isNotEmpty()
                        ) {
                            Text("تأكيد")
                        }
                    }
                }
            }
        }
    }
}
