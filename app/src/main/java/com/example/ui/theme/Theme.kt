package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CyanNeon,
    onPrimary = Slate950,
    primaryContainer = Slate800,
    onPrimaryContainer = CyanNeon,
    secondary = AmberOrange,
    onSecondary = Slate950,
    secondaryContainer = Slate850,
    onSecondaryContainer = AmberOrange,
    tertiary = TrackGreen,
    background = Slate950,
    onBackground = Slate200,
    surface = Slate900,
    onSurface = Slate200,
    surfaceVariant = Slate850,
    onSurfaceVariant = Slate400,
    outline = Slate700
)

private val LightColorScheme = lightColorScheme(
    primary = CyanNeonGlow,
    onPrimary = White,
    primaryContainer = LightCard,
    onPrimaryContainer = CyanNeonGlow,
    secondary = AmberOrange,
    onSecondary = White,
    secondaryContainer = LightCard,
    onSecondaryContainer = AmberOrange,
    tertiary = TrackGreen,
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightCard,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder
)

@Composable
fun MotoScopeTheme(
    darkTheme: Boolean = true, // Default to sleek dark automotive aesthetic
    dynamicColor: Boolean = false, // Preserve brand identity
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
