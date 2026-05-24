package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonLime,
    secondary = CyberPurple,
    tertiary = CyberCyan,
    background = ObsidianBackground,
    surface = CardSlate,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = TextWhite,
    onSurface = TextWhite,
    surfaceVariant = CardSlateElevated,
    outline = MetallicSlate,
    error = NeonCoral
)

private val LightColorScheme = lightColorScheme(
    primary = CyberPurple,
    secondary = NeonLime,
    tertiary = CyberCyan,
    background = PremiumWhiteBg,
    surface = PremiumLightCard,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = PremiumDeepSlate,
    onSurface = PremiumDeepSlate,
    surfaceVariant = PremiumLightBorder,
    outline = PremiumLightBorder,
    error = NeonCoral
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to darkTheme for that premium fintech feel
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
