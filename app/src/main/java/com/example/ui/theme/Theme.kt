package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val McPlayColorScheme = lightColorScheme(
    primary = McPlayRed,
    onPrimary = McPlayTextLight,
    primaryContainer = McPlayYellow,
    onPrimaryContainer = McPlayTextDark,
    secondary = McPlayGreen,
    onSecondary = McPlayTextLight,
    secondaryContainer = McPlayLightGreen,
    onSecondaryContainer = McPlayDarkGreen,
    tertiary = McPlaySkyBlue,
    onTertiary = McPlayTextLight,
    background = McPlayPeachBg,
    onBackground = McPlayTextDark,
    surface = McPlayCardBg,
    onSurface = McPlayTextDark,
    surfaceVariant = McPlayCream,
    onSurfaceVariant = McPlayTextDark
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep authentic vibrant McPlay palette across all Android versions
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = McPlayColorScheme,
        typography = Typography,
        content = content
    )
}
