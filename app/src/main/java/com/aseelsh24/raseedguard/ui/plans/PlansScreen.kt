package com.aseelsh24.raseedguard.ui.plans

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aseelsh24.raseedguard.R
import com.aseelsh24.raseedguard.core.PlanCategory
import com.aseelsh24.raseedguard.core.PlanType
import com.aseelsh24.raseedguard.ui.AppViewModelProvider
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlansScreen(
    onNavigateToAddPlan: () -> Unit,
    onNavigateToEditPlan: (String) -> Unit,
    viewModel: PlansViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.title_plans)) })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddPlan) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.action_add_plan))
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.plans) { planModel ->
                    PlanCard(
                        planModel = planModel,
                        onSetActive = { viewModel.setActivePlan(planModel.plan.id) },
                        onEdit = { onNavigateToEditPlan(planModel.plan.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun PlanCard(
    planModel: PlanUiModel,
    onSetActive: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = if (planModel.isActive) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (planModel.plan.type == PlanType.INTERNET) stringResource(R.string.plan_type_internet) else stringResource(R.string.plan_type_voice),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = when (planModel.plan.category) {
                            PlanCategory.MOBILE -> stringResource(R.string.plan_category_mobile)
                            PlanCategory.HOME -> stringResource(R.string.plan_category_home)
                            PlanCategory.VOICE -> stringResource(R.string.plan_category_voice)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (planModel.isActive) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = stringResource(R.string.content_description_active_plan),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            Text(
                text = "${planModel.plan.startAt.format(formatter)} - ${planModel.plan.endAt.format(formatter)}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) {
                    Text(stringResource(R.string.action_edit_plan))
                }
                if (!planModel.isActive) {
                    TextButton(onClick = onSetActive) {
                        Text(stringResource(R.string.action_set_active))
                    }
                }
            }
        }
    }
}
