package com.aseelsh24.raseedguard.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aseelsh24.raseedguard.core.PlanType
import com.aseelsh24.raseedguard.core.Unit
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPlanScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEditPlanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إضافة باقة جديدة") },
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
            // Plan Type Dropdown
            PlanTypeDropdown(
                selectedType = uiState.planType,
                onTypeSelected = { viewModel.onPlanTypeChanged(it) }
            )

            // Start Date Picker
            OutlinedTextField(
                value = uiState.startDate?.format(dateFormatter) ?: "",
                onValueChange = {},
                label = { Text("تاريخ البداية") },
                readOnly = true,
                isError = uiState.startDateError != null,
                supportingText = uiState.startDateError?.let { { Text(it) } },
                trailingIcon = {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "اختر التاريخ",
                        modifier = Modifier.clickable { showStartDatePicker = true }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showStartDatePicker = true }
            )

            // End Date Picker
            OutlinedTextField(
                value = uiState.endDate?.format(dateFormatter) ?: "",
                onValueChange = {},
                label = { Text("تاريخ الانتهاء") },
                readOnly = true,
                isError = uiState.endDateError != null,
                supportingText = uiState.endDateError?.let { { Text(it) } },
                trailingIcon = {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "اختر التاريخ",
                        modifier = Modifier.clickable { showEndDatePicker = true }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showEndDatePicker = true }
            )

            // Unit Dropdown
            UnitDropdown(
                selectedUnit = uiState.unit,
                planType = uiState.planType,
                onUnitSelected = { viewModel.onUnitChanged(it) }
            )

            // Initial Amount
            OutlinedTextField(
                value = uiState.initialAmount,
                onValueChange = { viewModel.onInitialAmountChanged(it) },
                label = { Text("الكمية الأولية") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = uiState.initialAmountError != null,
                supportingText = uiState.initialAmountError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            // Error message
            if (uiState.saveError != null) {
                Text(
                    text = uiState.saveError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Save Button
            Button(
                onClick = { viewModel.savePlan(onNavigateBack) },
                enabled = !uiState.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (uiState.isSaving) "جاري الحفظ..." else "حفظ الباقة")
            }
        }
    }

    // Date Pickers
    if (showStartDatePicker) {
        SimpleDatePickerDialog(
            onDateSelected = { date ->
                viewModel.onStartDateChanged(date)
                showStartDatePicker = false
            },
            onDismiss = { showStartDatePicker = false }
        )
    }

    if (showEndDatePicker) {
        SimpleDatePickerDialog(
            onDateSelected = { date ->
                viewModel.onEndDateChanged(date)
                showEndDatePicker = false
            },
            onDismiss = { showEndDatePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanTypeDropdown(
    selectedType: PlanType,
    onTypeSelected: (PlanType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = when (selectedType) {
                PlanType.INTERNET -> "إنترنت"
                PlanType.VOICE -> "مكالمات"
                PlanType.MIXED -> "مختلط (إنترنت + مكالمات)"
            },
            onValueChange = {},
            readOnly = true,
            label = { Text("نوع الباقة") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("إنترنت") },
                onClick = {
                    onTypeSelected(PlanType.INTERNET)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("مكالمات") },
                onClick = {
                    onTypeSelected(PlanType.VOICE)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("مختلط (إنترنت + مكالمات)") },
                onClick = {
                    onTypeSelected(PlanType.MIXED)
                    expanded = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitDropdown(
    selectedUnit: Unit,
    planType: PlanType,
    onUnitSelected: (Unit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // Available units based on plan type
    val availableUnits = when (planType) {
        PlanType.INTERNET -> listOf(Unit.MB, Unit.GB)
        PlanType.VOICE -> listOf(Unit.MINUTES)
        PlanType.MIXED -> listOf(Unit.MB, Unit.GB, Unit.MINUTES)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = when (selectedUnit) {
                Unit.MB -> "ميجابايت (MB)"
                Unit.GB -> "جيجابايت (GB)"
                Unit.MINUTES -> "دقائق"
            },
            onValueChange = {},
            readOnly = true,
            label = { Text("الوحدة") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            availableUnits.forEach { unit ->
                DropdownMenuItem(
                    text = {
                        Text(
                            when (unit) {
                                Unit.MB -> "ميجابايت (MB)"
                                Unit.GB -> "جيجابايت (GB)"
                                Unit.MINUTES -> "دقائق"
                            }
                        )
                    },
                    onClick = {
                        onUnitSelected(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleDatePickerDialog(
    onDateSelected: (LocalDateTime) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val instant = java.time.Instant.ofEpochMilli(millis)
                        val date = LocalDateTime.ofInstant(
                            instant,
                            java.time.ZoneId.systemDefault()
                        )
                        onDateSelected(date)
                    }
                }
            ) {
                Text("تأكيد")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

