package com.aseelsh24.raseedguard.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.compose.ui.Modifier
import com.aseelsh24.raseedguard.ui.insights.InsightsScreen
import com.aseelsh24.raseedguard.ui.plans.PlansScreen
import com.aseelsh24.raseedguard.ui.settings.SettingsScreen

object Destinations {
    const val DASHBOARD = "dashboard"
    const val ADD_EDIT_PLAN = "add_edit_plan"
    const val WEEKLY_UPDATE = "weekly_update"
    const val INSIGHTS = "insights"
    const val SETTINGS = "settings"
    const val PLANS = "plans"
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
                onNavigateToEditPlan = { planId ->
                    navController.navigate("${Destinations.ADD_EDIT_PLAN}?planId=$planId")
                },
                onNavigateToUpdate = { navController.navigate(Destinations.WEEKLY_UPDATE) },
                onNavigateToInsights = { navController.navigate(Destinations.INSIGHTS) },
                onNavigateToSettings = { navController.navigate(Destinations.SETTINGS) },
                onNavigateToPlans = { navController.navigate(Destinations.PLANS) }
            )
        }
        composable(
            route = "${Destinations.ADD_EDIT_PLAN}?planId={planId}",
            arguments = listOf(navArgument("planId") {
                type = NavType.StringType
                nullable = true
            })
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
        composable(Destinations.SETTINGS) {
            SettingsScreen()
        }
        composable(Destinations.PLANS) {
            PlansScreen(
                onNavigateToAddPlan = { navController.navigate(Destinations.ADD_EDIT_PLAN) },
                onNavigateToEditPlan = { planId ->
                    navController.navigate("${Destinations.ADD_EDIT_PLAN}?planId=$planId")
                }
            )
        }
    }
}
