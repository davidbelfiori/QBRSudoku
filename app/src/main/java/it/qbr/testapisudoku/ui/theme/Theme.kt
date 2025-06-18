package it.qbr.testapisudoku.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color



private val DarkColorScheme = darkColorScheme(
    primary = blue_p,         // blu chiaro
    onPrimary = Color.Black,
    secondary = blue_secondary,       // azzurro chiaro
    onSecondary = Color.Black,  // grigio chiaro per le icone delle statistiche
    background = Color(34, 40, 49),      // quasi nero
    onBackground = Color(0xFFE0E0E0),    // grigio chiaro
    surface = Color(0xFF1E1E1E),         // grigio scuro
    onSurface = Color(0xFFEEEEEE),       // quasi bianco
    error = Color(0xFFCF6679),           // rosso acceso
    onError = Color.Black,
)


private val LightColorScheme = lightColorScheme(
    primary = blue_p,
    onPrimary = Color.White,
    secondary = blue_secondary,
    onSecondary = Color.White,
    background = Color.White,
    onBackground = Color.DarkGray,
    surface = background_rows,
    onSurface = gray,
    error = quit_background,
    onError = Color.Red,
)


/*
val AppColorScheme = lightColorScheme(
    primary = blue_p,
    onPrimary = Color.White,
    secondary = blue_secondary,
    onSecondary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = background_rows,
    onSurface = gray,
    error = quit_background,
    onError = Color.Red,
)


@Composable
fun QBRSudokuTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = AppTypography,
        content = content
    )
}
 */



@Composable
fun QBRSudokuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = AppTypography,
        content = content
    )
}