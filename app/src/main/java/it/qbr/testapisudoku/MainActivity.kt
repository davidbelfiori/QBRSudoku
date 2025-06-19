package it.qbr.testapisudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import it.qbr.testapisudoku.ui.MainNavHost
import it.qbr.testapisudoku.ui.theme.QBRSudokuTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
                        super.onCreate(savedInstanceState)
                        // Set the content view for this activity using Jetpack Compose
                        setContent {
                            // A mutable state to track whether the app is in dark theme mode
                            var isDarkTheme by rememberSaveable { mutableStateOf(false) }

                            // Create a navigation controller to manage app navigation
                            val navController = rememberNavController()

                            // Apply the app's theme (light or dark) using QBRSudokuTheme
                            QBRSudokuTheme(darkTheme = isDarkTheme) {
                                // Set up the main navigation host for the app
                                MainNavHost(
                                    navController = navController, // Pass the navigation controller
                                    isDarkTheme = isDarkTheme,     // Pass the current theme state
                                    onToggleDarkTheme = {          // Define a callback to toggle the theme
                                        isDarkTheme = !isDarkTheme
                                    }
                                )
                            }
                        }
                    }
}

