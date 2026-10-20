package com.example.smokeless.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Sage,
    onPrimary = Color.White,
    primaryContainer = SageLight,
    onPrimaryContainer = Color(0xFF0C211A),
    secondary = Clay,
    onSecondary = Color.White,
    secondaryContainer = ClayLight,
    onSecondaryContainer = Color(0xFF29180A),
    tertiary = Color(0xFF5B6B78),
    onTertiary = Color.White,
    background = WarmCanvas,
    onBackground = Color(0xFF1E1F1C),
    surface = WarmSurface,
    onSurface = Color(0xFF1E1F1C),
    surfaceVariant = WarmSurfaceVariant,
    onSurfaceVariant = Color(0xFF56564E),
    outline = WarmOutline,
    error = Color(0xFF8F4A44),
    onError = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = SageDark,
    onPrimary = Color(0xFF0B2118),
    primaryContainer = SageContainerDark,
    onPrimaryContainer = Color(0xFFC2E8D8),
    secondary = ClayDark,
    onSecondary = Color(0xFF2A1C10),
    secondaryContainer = Color(0xFF423224),
    onSecondaryContainer = Color(0xFFF0DFC9),
    tertiary = Color(0xFFB4C5D2),
    onTertiary = Color(0xFF1D2A33),
    background = NightCanvas,
    onBackground = Color(0xFFE5E7E1),
    surface = NightSurface,
    onSurface = Color(0xFFE5E7E1),
    surfaceVariant = NightSurfaceVariant,
    onSurfaceVariant = Color(0xFFC0C7C1),
    outline = NightOutline,
    error = Color(0xFFE0A39D),
    onError = Color(0xFF3D0905),
)

@Composable
fun SmokeLessTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = SmokeLessTypography,
        content = content,
    )
}
