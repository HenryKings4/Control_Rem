package com.example.control_rem.wear.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Modern Vibrant Palette - Battery Optimized for Moto 360 Gen 1 Round Display
val PrimaryCyan = Color(0xFF00E5FF)       // Electric Cyan for primary action (Next Slide)
val PrimaryTeal = Color(0xFF00F5D4)       // Bright Teal
val SecondaryGreen = Color(0xFF00E676)    // Emerald Green for Play / Connected status
val AccentAmber = Color(0xFFFFB703)       // Warm Amber for Prev Slide
val ActionRed = Color(0xFFFF5252)         // Coral Red for Stop / Errors
val DarkBackground = Color(0xFF000000)    // Pure Black for AMOLED/LCD battery efficiency
val DarkSurface = Color(0xFF161622)       // Dark Slate Surface
val DarkCardSurface = Color(0xFF222234)   // Slightly lighter surface for cards
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFA0A0B2)

private val Moto360ColorScheme = darkColorScheme(
    primary = PrimaryCyan,
    secondary = SecondaryGreen,
    tertiary = AccentAmber,
    error = ActionRed,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkCardSurface,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun WearTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = Moto360ColorScheme,
        content = content
    )
}
