package com.rork.grievai.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val LocalIsDark = staticCompositionLocalOf { false }

private val LightColors = lightColorScheme(
    primary = Indigo600,
    onPrimary = Color.White,
    primaryContainer = Indigo100,
    onPrimaryContainer = Indigo800,
    secondary = Amber600,
    onSecondary = Color.White,
    secondaryContainer = Amber300,
    onSecondaryContainer = Color(0xFF4A2E00),
    tertiary = Blue500,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE3F2FD),
    onTertiaryContainer = Color(0xFF0D47A1),
    background = SurfaceLight,
    onBackground = TextPrimaryLight,
    surface = CardLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = BorderLight,
    outlineVariant = Color(0xFFD8DDE8),
    error = PriorityCritical,
    onError = Color.White,
    errorContainer = Color(0xFFFFEDEA),
    onErrorContainer = Color(0xFFB71C1C)
)

private val DarkColors = darkColorScheme(
    primary = Indigo300,
    onPrimary = Color(0xFF0A1030),
    primaryContainer = Indigo700,
    onPrimaryContainer = Indigo100,
    secondary = Amber400,
    onSecondary = Color(0xFF2A1700),
    secondaryContainer = Amber700,
    onSecondaryContainer = Amber300,
    tertiary = Blue300,
    onTertiary = Color(0xFF06233D),
    tertiaryContainer = Color(0xFF0D3B66),
    onTertiaryContainer = Color(0xFFCFE6FF),
    background = SurfaceDark,
    onBackground = TextPrimaryDark,
    surface = CardDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderDark,
    outlineVariant = Color(0xFF353D54),
    error = Color(0xFFFF8A80),
    onError = Color(0xFF4A0000),
    errorContainer = Color(0xFF5D1A1A),
    onErrorContainer = Color(0xFFFFDAD6)
)

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    CompositionLocalProvider(LocalIsDark provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}
