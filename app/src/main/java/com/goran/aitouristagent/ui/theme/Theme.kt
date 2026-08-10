package com.goran.aitouristagent.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AiTouristAgentColorScheme = darkColorScheme(
    primary = AccentGreen,
    onPrimary = BackgroundBase,
    primaryContainer = AccentGreenDark,
    onPrimaryContainer = TextPrimary,
    secondary = GoldDark,
    onSecondary = BackgroundBase,
    secondaryContainer = GoldLight,
    onSecondaryContainer = BackgroundBase,
    background = BackgroundBase,
    onBackground = TextPrimary,
    surface = BackgroundElevated,
    onSurface = TextPrimary,
    surfaceVariant = BackgroundCard,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    error = ErrorRed,
    onError = TextPrimary,
    tertiary = FerryBlue,
    onTertiary = BackgroundBase,
)

// App design system is dark-only (§1.4 of ANDROID_APP_ARCHITECTURE.md).
@Composable
fun AiTouristAgentTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = AiTouristAgentColorScheme,
        typography = Typography,
        content = content,
    )
}
