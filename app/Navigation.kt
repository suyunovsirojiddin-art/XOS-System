package com.xos.personalsystem.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.xos.personalsystem.presentation.screens.*
import com.xos.personalsystem.presentation.viewmodels.MainViewModel

@Composable
fun XOSNavHost(
    mainViewModel: MainViewModel,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "personalities"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.fillMaxSize()
    ) {
        composable("personalities") {
            PersonalitiesScreen(
                viewModel = mainViewModel,
                onPersonalitySelected = { personalityId ->
                    navController.navigate("dashboard/$personalityId")
                }
            )
        }
        
        composable("dashboard/{personalityId}") { backStackEntry ->
            val personalityId = backStackEntry.arguments?.getString("personalityId") ?: return@composable
            PersonalityDashboardScreen(
                personalityId = personalityId,
                viewModel = mainViewModel,
                onBack = { navController.popBackStack() },
                onAdminPanel = { navController.navigate("admin") },
                onDailyTasks = { navController.navigate("daily/$personalityId") },
                onJournal = { navController.navigate("journal/$personalityId") },
                onLessons = { navController.navigate("lessons/$personalityId") },
                onAchievements = { navController.navigate("achievements/$personalityId") }
            )
        }
        
        composable("daily/{personalityId}") { backStackEntry ->
            val personalityId = backStackEntry.arguments?.getString("personalityId") ?: return@composable
            DailyTasksScreen(
                personalityId = personalityId,
                viewModel = mainViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("admin") {
            AdminPanelScreen(
                viewModel = mainViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("journal/{personalityId}") { backStackEntry ->
            val personalityId = backStackEntry.arguments?.getString("personalityId") ?: return@composable
            JournalScreen(
                personalityId = personalityId,
                viewModel = mainViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("lessons/{personalityId}") { backStackEntry ->
            val personalityId = backStackEntry.arguments?.getString("personalityId") ?: return@composable
            LessonsScreen(
                personalityId = personalityId,
                viewModel = mainViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("achievements/{personalityId}") { backStackEntry ->
            val personalityId = backStackEntry.arguments?.getString("personalityId") ?: return@composable
            AchievementsScreen(
                personalityId = personalityId,
                viewModel = mainViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("alarm") {
            AlarmScreen(
                viewModel = mainViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("backup") {
            BackupScreen(
                viewModel = mainViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("focus") {
            FocusModeScreen(
                viewModel = mainViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
