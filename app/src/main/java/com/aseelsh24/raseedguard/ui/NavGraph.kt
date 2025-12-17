package com.aseelsh24.raseedguard.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.compose.ui.Modifier

object Destinations {
    const val DASHBOARD = "dashboard"
    const val ADD_EDIT_PLAN = "add_edit_plan"
    const val WEEKLY_UPDATE = "weekly_update"
    const val INSIGHTS = "insights"

    // Arguments
    const val PLAN_ID_ARG = "planId"
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
                onNavigateToEdit = { planId -> navController.navigate("${Destinations.ADD_EDIT_PLAN}?${Destinations.PLAN_ID_ARG}=$planId") },
                onNavigateToInsights = { navController.navigate(Destinations.INSIGHTS) }
            )
        }
        composable(
            route = "${Destinations.ADD_EDIT_PLAN}?${Destinations.PLAN_ID_ARG}={${Destinations.PLAN_ID_ARG}}",
            arguments = listOf(
                navArgument(Destinations.PLAN_ID_ARG) {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
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
