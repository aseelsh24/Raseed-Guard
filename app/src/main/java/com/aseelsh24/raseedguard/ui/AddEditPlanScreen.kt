package com.aseelsh24.raseedguard.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aseelsh24.raseedguard.core.PlanType
import com.aseelsh24.raseedguard.core.Unit as PlanUnit
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPlanScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEditPlanViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var initialAmount by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(PlanType.INTERNET) }
    var selectedUnit by remember { mutableStateOf(PlanUnit.GB) }
    // Ideally use a date picker
    var startDate by remember { mutableStateOf(LocalDate.now().toString()) }
    var endDate by remember { mutableStateOf(LocalDate.now().plusDays(30).toString()) }
    var dateError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("إضافة / تعديل باقة") })
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            OutlinedTextField(
                value = initialAmount,
                onValueChange = { initialAmount = it },
                label = { Text("الرصيد الأولي") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("نوع الباقة")
            Row {
                PlanType.values().forEach { type ->
                    Button(
                        onClick = { selectedType = type },
                        modifier = Modifier.padding(end = 4.dp),
                        colors = if (selectedType == type) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()
                    ) {
                        Text(type.name)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("الوحدة")
            Row {
                PlanUnit.values().forEach { unit ->
                    Button(
                        onClick = { selectedUnit = unit },
                        modifier = Modifier.padding(end = 4.dp),
                        colors = if (selectedUnit == unit) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()
                    ) {
                        Text(unit.name)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Very basic date input
            OutlinedTextField(
                value = startDate,
                onValueChange = {
                    startDate = it
                    dateError = null
                },
                label = { Text("تاريخ البدء (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                isError = dateError != null
            )

            OutlinedTextField(
                value = endDate,
                onValueChange = {
                    endDate = it
                    dateError = null
                },
                label = { Text("تاريخ الانتهاء (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                isError = dateError != null
            )

            if (dateError != null) {
                Text(
                    text = dateError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    try {
                        val start = LocalDate.parse(startDate)
                        val end = LocalDate.parse(endDate)
                        viewModel.savePlan(
                            type = selectedType,
                            startDate = start,
                            endDate = end,
                            initialAmount = initialAmount,
                            unit = selectedUnit
                        )
                        onNavigateBack()
                    } catch (e: Exception) {
                        dateError = "Invalid date format. Please use YYYY-MM-DD."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("حفظ")
            }
        }
    }
}
