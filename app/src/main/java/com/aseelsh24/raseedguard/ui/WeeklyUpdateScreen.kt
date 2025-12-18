package com.aseelsh24.raseedguard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aseelsh24.raseedguard.core.Plan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyUpdateScreen(
    onNavigateBack: () -> Unit,
    viewModel: WeeklyUpdateViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val plans by viewModel.plans.collectAsState()
    var selectedPlan by remember { mutableStateOf<Plan?>(null) }
    var currentBalance by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // Default to first plan if available and none selected
    if (selectedPlan == null && plans.isNotEmpty()) {
        selectedPlan = plans.first()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("تحديث أسبوعي") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (plans.isEmpty()) {
                Text("لا توجد باقات نشطة. أضف باقة أولاً.")
                Button(onClick = onNavigateBack) {
                    Text("عودة")
                }
            } else {
                // Dropdown for Plan Selection
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        readOnly = true,
                        value = "Plan started: ${selectedPlan?.startAt?.toLocalDate()}",
                        onValueChange = {},
                        label = { Text("الباقة") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        plans.forEach { plan ->
                            DropdownMenuItem(
                                text = { Text("Plan started: ${plan.startAt.toLocalDate()}") },
                                onClick = {
                                    selectedPlan = plan
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = currentBalance,
                    onValueChange = { currentBalance = it },
                    label = { Text("الرصيد المتبقي") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        val balance = currentBalance.toDoubleOrNull()
                        val plan = selectedPlan
                        if (balance != null && plan != null) {
                            viewModel.addBalanceLog(plan.id, balance)
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = currentBalance.isNotEmpty() && selectedPlan != null
                ) {
                    Text("تحديث الرصيد")
                }
            }
        }
    }
}
