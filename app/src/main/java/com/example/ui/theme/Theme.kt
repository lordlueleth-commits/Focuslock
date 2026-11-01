package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BlockItColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = Color(0xFF00232B),
    primaryContainer = Color(0xFF003844),
    onPrimaryContainer = Color(0xFF99F5FF),
    secondary = FocusAmber,
    onSecondary = Color(0xFF2A1B00),
    secondaryContainer = Color(0xFF452E00),
    onSecondaryContainer = Color(0xFFFFDF9E),
    tertiary = ZenGreen,
    onTertiary = Color(0xFF002910),
    background = PitchBlack,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder,
    outlineVariant = DarkSurfaceVariant,
    error = DangerRed,
    onError = Color(0xFF37000B)
)

@Composable
fun BlockItTheme(
    content: @Composable () -> Unit
) {
    // BlockIt is explicitly dark-mode only for zero-distraction deep work
    MaterialTheme(
        colorScheme = BlockItColorScheme,
        typography = Typography,
        content = content
    )
}

// Backward compatibility alias if needed
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    BlockItTheme(content = content)
}
