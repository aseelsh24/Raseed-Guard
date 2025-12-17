package com.aseelsh24.raseedguard.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier

object Destinations {
    const val DASHBOARD = "dashboard"
    const val ADD_EDIT_PLAN = "add_edit_plan"
    const val WEEKLY_UPDATE = "weekly_update"
    const val INSIGHTS = "insights"
}

@Composable
fun RaseedGuardNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Destinations.DASHBOARD,
        modifier = modifier
    ) {
        composable(Destinations.DASHBOARD) {
            DashboardScreen(
                onNavigateToAddPlan = { navController.navigate(Destinations.ADD_EDIT_PLAN) },
                onNavigateToUpdate = { navController.navigate(Destinations.WEEKLY_UPDATE) },
                onNavigateToInsights = { navController.navigate(Destinations.INSIGHTS) }
            )
        }
        composable(Destinations.ADD_EDIT_PLAN) {
            AddEditPlanScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Destinations.WEEKLY_UPDATE) {
            WeeklyUpdateScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Destinations.INSIGHTS) {
            InsightsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
