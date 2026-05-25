package com.kido.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kido.app.feature.game.GameScreen
import com.kido.app.feature.home.HomeScreen
import com.kido.app.feature.parent.ParentGateScreen
import com.kido.app.feature.parent.ParentSettingsScreen

object KidoRoutes {
    const val Home = "home"
    const val Game = "game"
    const val ParentGate = "parent_gate"
    const val ParentSettings = "parent_settings"
}

@Composable
fun KidoNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(navController = navController, startDestination = KidoRoutes.Home) {
        composable(KidoRoutes.Home) {
            HomeScreen(
                onPlay = { navController.navigate(KidoRoutes.Game) },
                onParentGate = { navController.navigate(KidoRoutes.ParentGate) },
            )
        }
        composable(KidoRoutes.Game) {
            GameScreen(onExit = { navController.popBackStack() })
        }
        composable(KidoRoutes.ParentGate) {
            ParentGateScreen(
                onPassed = {
                    navController.navigate(KidoRoutes.ParentSettings) {
                        popUpTo(KidoRoutes.ParentGate) { inclusive = true }
                    }
                },
                onCancel = { navController.popBackStack() },
            )
        }
        composable(KidoRoutes.ParentSettings) {
            ParentSettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
