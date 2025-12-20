package com.aseelsh24.raseedguard.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aseelsh24.raseedguard.core.PlanType
import com.aseelsh24.raseedguard.core.Unit as PlanUnit
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPlanScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEditPlanViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    // State for Date Pickers
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    // State for Plan Type Dropdown
    var planTypeExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(if (uiState.planId != null) "Edit Plan" else "Add Plan") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // 1. Plan Type dropdown
            ExposedDropdownMenuBox(
                expanded = planTypeExpanded,
                onExpandedChange = { planTypeExpanded = !planTypeExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = if (uiState.type == PlanType.INTERNET) "Internet Plan" else "Voice Plan",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Plan Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = planTypeExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = planTypeExpanded,
                    onDismissRequest = { planTypeExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Internet Plan") },
                        onClick = {
                            viewModel.updateType(PlanType.INTERNET)
                            planTypeExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Voice Plan") },
                        onClick = {
                            viewModel.updateType(PlanType.VOICE)
                            planTypeExpanded = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Start Date field
            DateInput(
                label = "Start Date",
                date = uiState.startAt,
                onClick = { showStartDatePicker = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. End Date field
            DateInput(
                label = "End Date",
                date = uiState.endAt,
                onClick = { showEndDatePicker = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Initial Amount + Unit
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = uiState.initialAmount,
                    onValueChange = { viewModel.updateInitialAmount(it) },
                    label = { Text("Initial Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(0.6f)
                )

                Spacer(modifier = Modifier.padding(4.dp))

                Box(modifier = Modifier.weight(0.4f)) {
                    UnitSelector(
                        selectedUnit = uiState.unit,
                        planType = uiState.type,
                        onUnitSelected = { viewModel.updateUnit(it) }
                    )
                }
            }

            if (uiState.endAt.isBefore(uiState.startAt)) {
                Text(
                    text = "End date must be after start date",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Save Button
            Button(
                onClick = {
                    viewModel.savePlan(onSuccess = onNavigateBack)
                },
                enabled = uiState.isValid,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Text("Save Plan")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 6. Cancel Button
            TextButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel")
            }
        }
    }

    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.startAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime().with(LocalTime.MIN)
                        viewModel.updateStartAt(date)
                    }
                    showStartDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.endAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                         val date = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime().with(LocalTime.MAX)
                         viewModel.updateEndAt(date)
                    }
                    showEndDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun DateInput(
    label: String,
    date: LocalDateTime,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    Box(modifier = modifier) {
        OutlinedTextField(
            value = date.format(formatter),
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                Icon(Icons.Default.DateRange, contentDescription = "Select Date")
            },
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { onClick() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitSelector(
    selectedUnit: PlanUnit,
    planType: PlanType,
    onUnitSelected: (PlanUnit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (planType == PlanType.INTERNET) expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = if (selectedUnit == PlanUnit.MINUTES) "Minutes" else selectedUnit.name,
            onValueChange = {},
            readOnly = true,
            label = { Text("Unit") },
            trailingIcon = {
                 if (planType == PlanType.INTERNET) {
                     ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                 }
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            enabled = planType == PlanType.INTERNET
        )
        if (planType == PlanType.INTERNET) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("GB") },
                    onClick = {
                        onUnitSelected(PlanUnit.GB)
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("MB") },
                    onClick = {
                        onUnitSelected(PlanUnit.MB)
                        expanded = false
                    }
                )
            }
        }
    }
}
