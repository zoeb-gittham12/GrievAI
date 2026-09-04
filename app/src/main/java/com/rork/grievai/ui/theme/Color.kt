package com.rork.grievai.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ── Brand: University-inspired deep indigo + amber gold ───────────────────────

val Indigo900 = Color(0xFF1A237E)
val Indigo800 = Color(0xFF283593)
val Indigo700 = Color(0xFF303F9F)
val Indigo600 = Color(0xFF3949AB)
val Indigo500 = Color(0xFF3F51B5)
val Indigo400 = Color(0xFF5C6BC0)
val Indigo300 = Color(0xFF7986CB)
val Indigo200 = Color(0xFF9FA8DA)
val Indigo100 = Color(0xFFC5CAE9)
val Indigo50 = Color(0xFFE8EAF6)

val Blue500 = Color(0xFF2196F3)
val Blue400 = Color(0xFF42A5F5)
val Blue300 = Color(0xFF64B5F6)

val Amber500 = Color(0xFFFFB300)
val Amber400 = Color(0xFFFFCA28)
val Amber300 = Color(0xFFFFD54F)
val Amber600 = Color(0xFFFFA000)
val Amber700 = Color(0xFFFF8F00)

// ── Semantic / status colors ──────────────────────────────────────────────────

val PriorityLow = Color(0xFF4CAF50)
val PriorityMedium = Color(0xFF2196F3)
val PriorityHigh = Color(0xFFFF9800)
val PriorityCritical = Color(0xFFE53935)

val StatusSubmitted = Color(0xFF607D8B)
val StatusReview = Color(0xFF7E57C2)
val StatusAssigned = Color(0xFF29B6F6)
val StatusInProgress = Color(0xFFFFA726)
val StatusProgress = StatusInProgress
val StatusResolved = Color(0xFF66BB6A)

// ── Neutral / surface (light) ─────────────────────────────────────────────────

val SurfaceLight = Color(0xFFF7F8FC)
val SurfaceVariantLight = Color(0xFFEEF1F8)
val CardLight = Color(0xFFFFFFFF)
val CardElevatedLight = Color(0xFFFFFFFF)
val BorderLight = Color(0xFFE2E6F0)
val OnSurfaceMutedLight = Color(0xFF6B7280)
val TextPrimaryLight = Color(0xFF1A1F36)
val TextSecondaryLight = Color(0xFF5B6173)

// ── Neutral / surface (dark) ──────────────────────────────────────────────────

val SurfaceDark = Color(0xFF0F1320)
val SurfaceVariantDark = Color(0xFF181D2E)
val CardDark = Color(0xFF1A2030)
val CardElevatedDark = Color(0xFF212844)
val BorderDark = Color(0xFF2A3146)
val OnSurfaceMutedDark = Color(0xFF9CA3B5)
val TextPrimaryDark = Color(0xFFF2F4FA)
val TextSecondaryDark = Color(0xFFAEB4C6)

// ── Gradients ─────────────────────────────────────────────────────────────────

val HeroGradient = Brush.linearGradient(
    colors = listOf(Indigo700, Indigo500, Blue500)
)

val HeroGradientAmber = Brush.linearGradient(
    colors = listOf(Indigo700, Amber600)
)

val CardAccentGradient = Brush.linearGradient(
    colors = listOf(Indigo600, Blue400)
)

val CriticalGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFEF5350), Color(0xFFE53935))
)

val HighGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFFFA726), Color(0xFFFF9800))
)

val MediumGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF42A5F5), Color(0xFF2196F3))
)

val LowGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF66BB6A), Color(0xFF4CAF50))
)

val ResolvedGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF66BB6A), Color(0xFF43A047))
)

val ProgressGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFFFA726), Color(0xFFFB8C00))
)

val SubmittedGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF78909C), Color(0xFF607D8B))
)

val ReviewGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF9575CD), Color(0xFF7E57C2))
)

val AssignedGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF4FC3F7), Color(0xFF29B6F6))
)

val SplashGradient = Brush.verticalGradient(
    colors = listOf(Indigo900, Indigo700, Indigo500)
)

val AIGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF7E57C2), Color(0xFF5C6BC0), Blue400)
)

val AnalyticsGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF26C6DA), Color(0xFF42A5F5))
)
