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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(if (uiState.planId != null) "تعديل الباقة" else "إضافة باقة") })
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)) {

            // Plan Type Selector
            Text("نوع الباقة")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = uiState.type == PlanType.INTERNET,
                        onClick = { viewModel.updateType(PlanType.INTERNET) }
                    )
                    Text("إنترنت")
                }
                Spacer(modifier = Modifier.padding(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = uiState.type == PlanType.VOICE,
                        onClick = { viewModel.updateType(PlanType.VOICE) }
                    )
                    Text("مكالمات")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dates
            DateInput(
                label = "تاريخ البدء",
                date = uiState.startAt,
                onClick = { showStartDatePicker = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            DateInput(
                label = "تاريخ الانتهاء",
                date = uiState.endAt,
                onClick = { showEndDatePicker = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Amount and Unit
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = uiState.initialAmount,
                    onValueChange = { viewModel.updateInitialAmount(it) },
                    label = { Text("سعة الباقة") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.padding(8.dp))

                UnitSelector(
                    selectedUnit = uiState.unit,
                    planType = uiState.type,
                    onUnitSelected = { viewModel.updateUnit(it) }
                )
            }

            if (uiState.endAt.isBefore(uiState.startAt)) {
                Text("تاريخ الانتهاء يجب أن يكون بعد تاريخ البدء", color = androidx.compose.ui.graphics.Color.Red)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.savePlan(onSuccess = onNavigateBack)
                },
                enabled = uiState.isValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("حفظ")
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
                }) { Text("موافق") }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) { Text("إلغاء") }
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
                }) { Text("موافق") }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) { Text("إلغاء") }
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
                Icon(Icons.Default.DateRange, contentDescription = "اختر تاريخ")
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

@Composable
fun UnitSelector(
    selectedUnit: PlanUnit,
    planType: PlanType,
    onUnitSelected: (PlanUnit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // If Voice, only show Minutes (or just text)
    if (planType == PlanType.VOICE) {
         Box(
             modifier = Modifier
                 .height(56.dp)
                 .padding(top = 8.dp),
             contentAlignment = Alignment.Center
         ) {
             Text("دقيقة")
         }
         return
    }

    // Dropdown for Internet
    Box {
        OutlinedTextField(
            value = selectedUnit.name,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            },
            modifier = Modifier.clickable { expanded = true }.height(64.dp).padding(top = 8.dp).fillMaxWidth(0.4f)
        )
        // Fix overlap click
        Box(modifier = Modifier.matchParentSize().clickable { expanded = true })

        DropdownMenu(
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
