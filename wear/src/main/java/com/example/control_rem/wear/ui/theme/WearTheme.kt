package com.example.control_rem.wear.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme

val PrimaryCyan = Color(0xFF00E5FF)
val SecondaryGreen = Color(0xFF00E676)
val AccentAmber = Color(0xFFFFC107)
val ActionRed = Color(0xFFFF5252)
val DarkBackground = Color(0xFF000000)
val DarkSurface = Color(0xFF1E1E2E)

private val WearColorPalette = Colors(
    primary = PrimaryCyan,
    primaryVariant = Color(0xFF00B0FF),
    secondary = SecondaryGreen,
    secondaryVariant = Color(0xFF00C853),
    error = ActionRed,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onError = Color.White,
    background = DarkBackground,
    onBackground = Color.White,
    surface = DarkSurface,
    onSurface = Color.White
)

@Composable
fun WearTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = WearColorPalette,
        content = content
    )
}
