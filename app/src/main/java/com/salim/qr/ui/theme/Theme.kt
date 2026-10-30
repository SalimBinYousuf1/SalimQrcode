package com.salim.qr.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = SalimIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8EBFC),
    onPrimaryContainer = SalimNavy,
    secondary = SalimNavy,
    onSecondary = Color.White,
    background = Color(0xFFF7F5FC),
    onBackground = SalimNavy,
    surface = Color.White,
    onSurface = SalimNavy,
    surfaceVariant = Color(0xFFF0F2FA),
    onSurfaceVariant = SalimBodyGray,
    error = SalimDangerRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = SalimIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF272C5A),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF8B9BF8),
    onSecondary = Color.Black,
    background = SalimDarkBgTop,
    onBackground = Color.White,
    surface = SalimDarkBgBottom,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF252A50),
    onSurfaceVariant = Color(0xFFB9BDE0),
    error = SalimDangerRed,
    onError = Color.White
)

@Composable
fun SalimTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SalimTypography,
        content = content
    )
}
