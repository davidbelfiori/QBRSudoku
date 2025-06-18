package it.qbr.testapisudoku.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import it.qbr.testapisudoku.db.AppDatabase
import it.qbr.testapisudoku.db.Game
import kotlinx.coroutines.launch

@Composable
fun MainNavHost(
    navController: NavHostController,
    isDarkTheme: Boolean,
    onToggleDarkTheme: () -> Unit
) {

    NavHost(
        navController = navController,
        startDestination = "home",
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        }
    ) {
        composable("home") {
            HomeScreen(
                onStartGame = { navController.navigate("sudoku") },
                onStorico = { navController.navigate("storico") },
                onStats = { navController.navigate("stats") },
                isDarkTheme = isDarkTheme,
                onToggleDarkTheme = onToggleDarkTheme
            )
        }
        composable("sudoku") {
            SudokuScreen(navController, isDarkTheme = isDarkTheme)
        }
        composable("storico") {
            StoricoPartiteScreen(navController, isDarkTheme = isDarkTheme)
        }
        composable("stats") {
            val context = LocalContext.current
            var games by remember { mutableStateOf<List<Game>>(emptyList()) }
            val scope = rememberCoroutineScope()

            LaunchedEffect(Unit) {
                scope.launch {
                    games = AppDatabase.getDatabase(context).partitaDao().tutteLePartite()
                }
            }

            StatsScreen(
                navController,
                isDarkTheme = isDarkTheme,
                games = games
            )
        }
        composable("results"){

        }
    }
}
