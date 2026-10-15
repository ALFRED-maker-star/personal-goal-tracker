package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary deep purple from user prompt: #7C3AED
val DeepPurple = Color(0xFF7C3AED)
val DeepPurpleDark = Color(0xFF5B21B6)
val DeepPurpleLight = Color(0xFF8B5CF6)
val PurplePastel = Color(0xFFEDE9FE)

// Progress bar gradient purple-to-pink:
val GradientStart = Color(0xFF7C3AED)
val GradientMid = Color(0xFFA855F7)
val GradientEnd = Color(0xFFEC4899)

val ProgressGradient = Brush.horizontalGradient(
    colors = listOf(GradientStart, GradientMid, GradientEnd)
)

val ProgressGradientVertical = Brush.verticalGradient(
    colors = listOf(GradientStart, GradientEnd)
)

// Background from user prompt: #FAFAFA
val NeutralBackground = Color(0xFFFAFAFA)
val NeutralSurface = Color(0xFFFFFFFF)
val NeutralSurfaceVariant = Color(0xFFF3F4F6)

// Success gold accent from user prompt: #F59E0B for completed goals
val SuccessGold = Color(0xFFF59E0B)
val SuccessGoldDark = Color(0xFFD97706)
val SuccessGoldLight = Color(0xFFFEF3C7)

// Text & Borders
val TextPrimary = Color(0xFF111827)
val TextSecondary = Color(0xFF6B7280)
val TextTertiary = Color(0xFF9CA3AF)
val BorderSubtle = Color(0xFFE5E7EB)

// Dark Theme Variants
val DarkBackground = Color(0xFF0F0D17)
val DarkSurface = Color(0xFF191627)
val DarkSurfaceVariant = Color(0xFF26203B)
val DarkTextPrimary = Color(0xFFF9FAFB)
val DarkTextSecondary = Color(0xFF9CA3AF)
val DarkBorderSubtle = Color(0xFF2E2648)
