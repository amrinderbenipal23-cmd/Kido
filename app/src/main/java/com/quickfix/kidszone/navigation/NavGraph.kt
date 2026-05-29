package com.quickfix.kidszone.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.quickfix.kidszone.ui.abc.AbcScreen
import com.quickfix.kidszone.ui.animals.AnimalScreen
import com.quickfix.kidszone.ui.drawing.DrawingScreen
import com.quickfix.kidszone.ui.games.BalloonGameScreen
import com.quickfix.kidszone.ui.games.GamesScreen
import com.quickfix.kidszone.ui.games.MemoryGameScreen
import com.quickfix.kidszone.ui.home.HomeScreen
import com.quickfix.kidszone.ui.numbers.NumberScreen
import com.quickfix.kidszone.ui.parent.ParentDashboardScreen
import com.quickfix.kidszone.ui.poems.PoemPlayerScreen
import com.quickfix.kidszone.ui.poems.PoemsScreen
import com.quickfix.kidszone.ui.rewards.RewardsScreen
import com.quickfix.kidszone.ui.settings.SettingsScreen
import com.quickfix.kidszone.ui.splash.SplashScreen
import com.quickfix.kidszone.ui.stories.StoriesScreen
import com.quickfix.kidszone.ui.stories.StoryReaderScreen
import com.quickfix.kidszone.ui.tables.TableDetailScreen
import com.quickfix.kidszone.ui.tables.TableQuizScreen
import com.quickfix.kidszone.ui.tables.TablesScreen
import com.quickfix.kidszone.ui.words.SentenceMakingScreen
import com.quickfix.kidszone.ui.words.WordCategoryScreen
import com.quickfix.kidszone.ui.words.WordsScreen

@Composable
fun KiddoNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onNavigateToHome = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToAbc = { navController.navigate(Screen.AbcLearning.route) },
                onNavigateToNumbers = { navController.navigate(Screen.NumberLearning.route) },
                onNavigateToAnimals = { navController.navigate(Screen.AnimalLearning.route) },
                onNavigateToDrawing = { navController.navigate(Screen.Drawing.route) },
                onNavigateToGames = { navController.navigate(Screen.Games.route) },
                onNavigateToRewards = { navController.navigate(Screen.Rewards.route) },
                onNavigateToParent = { navController.navigate(Screen.ParentDashboard.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToTables = { navController.navigate(Screen.TablesLearning.route) },
                onNavigateToWords = { navController.navigate(Screen.WordsLearning.route) },
                onNavigateToStories = { navController.navigate(Screen.StoriesLearning.route) },
                onNavigateToPoems = { navController.navigate(Screen.PoemsLearning.route) },
            )
        }

        composable(Screen.AbcLearning.route) {
            AbcScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.NumberLearning.route) {
            NumberScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.AnimalLearning.route) {
            AnimalScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Drawing.route) {
            DrawingScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Games.route) {
            GamesScreen(
                onBack = { navController.popBackStack() },
                onMemoryGame = { navController.navigate(Screen.MemoryGame.route) },
                onBalloonGame = { navController.navigate(Screen.BalloonGame.route) },
            )
        }

        composable(Screen.MemoryGame.route) {
            MemoryGameScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.BalloonGame.route) {
            BalloonGameScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Rewards.route) {
            RewardsScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.ParentDashboard.route) {
            ParentDashboardScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }

        // ── Tables ───────────────────────────────────────────────────────────

        composable(Screen.TablesLearning.route) {
            TablesScreen(
                onTableSelected = { tableNum ->
                    navController.navigate(Screen.TableDetail.createRoute(tableNum))
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Screen.TableDetail.route,
            arguments = listOf(navArgument("tableNum") { type = NavType.IntType }),
        ) { backStack ->
            val tableNum = backStack.arguments?.getInt("tableNum") ?: 2
            TableDetailScreen(
                tableNum = tableNum,
                onStartQuiz = { navController.navigate(Screen.TableQuiz.createRoute(it)) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Screen.TableQuiz.route,
            arguments = listOf(navArgument("tableNum") { type = NavType.IntType }),
        ) { backStack ->
            val tableNum = backStack.arguments?.getInt("tableNum") ?: 2
            TableQuizScreen(
                tableNum = tableNum,
                onBack = { navController.popBackStack() },
            )
        }

        // ── Words ────────────────────────────────────────────────────────────

        composable(Screen.WordsLearning.route) {
            WordsScreen(
                onCategorySelected = { navController.navigate(Screen.WordCategory.createRoute(it)) },
                onSentenceMaking = { navController.navigate(Screen.SentenceMaking.route) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Screen.WordCategory.route,
            arguments = listOf(navArgument("category") { type = NavType.StringType }),
        ) { backStack ->
            val category = backStack.arguments?.getString("category") ?: "BASIC"
            WordCategoryScreen(
                categoryName = category,
                onBack = { navController.popBackStack() },
            )
        }

        composable(Screen.SentenceMaking.route) {
            SentenceMakingScreen(onBack = { navController.popBackStack() })
        }

        // ── Stories ──────────────────────────────────────────────────────────

        composable(Screen.StoriesLearning.route) {
            StoriesScreen(
                onStorySelected = { navController.navigate(Screen.StoryReader.createRoute(it)) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Screen.StoryReader.route,
            arguments = listOf(navArgument("storyId") { type = NavType.IntType }),
        ) { backStack ->
            val storyId = backStack.arguments?.getInt("storyId") ?: 1
            StoryReaderScreen(
                storyId = storyId,
                onBack = { navController.popBackStack() },
            )
        }

        // ── Poems ────────────────────────────────────────────────────────────

        composable(Screen.PoemsLearning.route) {
            PoemsScreen(
                onPoemSelected = { navController.navigate(Screen.PoemPlayer.createRoute(it)) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Screen.PoemPlayer.route,
            arguments = listOf(navArgument("poemId") { type = NavType.IntType }),
        ) { backStack ->
            val poemId = backStack.arguments?.getInt("poemId") ?: 1
            PoemPlayerScreen(
                poemId = poemId,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
