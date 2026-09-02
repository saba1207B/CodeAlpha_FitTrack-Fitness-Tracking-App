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
    primary = FitnessPrimaryDark,
    onPrimary = FitnessOnPrimaryDark,
    primaryContainer = FitnessPrimaryContainerDark,
    onPrimaryContainer = FitnessOnPrimaryContainerDark,
    secondary = FitnessSecondaryDark,
    onSecondary = FitnessOnSecondaryDark,
    secondaryContainer = FitnessSecondaryContainerDark,
    onSecondaryContainer = FitnessOnSecondaryContainerDark,
    tertiary = FitnessTertiaryDark,
    onTertiary = FitnessOnTertiaryDark,
    tertiaryContainer = FitnessTertiaryContainerDark,
    onTertiaryContainer = FitnessOnTertiaryContainerDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onBackground = OnBackgroundDark,
    onSurface = OnSurfaceDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = FitnessPrimaryLight,
    onPrimary = FitnessOnPrimaryLight,
    primaryContainer = FitnessPrimaryContainerLight,
    onPrimaryContainer = FitnessOnPrimaryContainerLight,
    secondary = FitnessSecondaryLight,
    onSecondary = FitnessOnSecondaryLight,
    secondaryContainer = FitnessSecondaryContainerLight,
    onSecondaryContainer = FitnessOnSecondaryContainerLight,
    tertiary = FitnessTertiaryLight,
    onTertiary = FitnessOnTertiaryLight,
    tertiaryContainer = FitnessTertiaryContainerLight,
    onTertiaryContainer = FitnessOnTertiaryContainerLight,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onBackground = OnBackgroundLight,
    onSurface = OnSurfaceLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight
)

@Composable
fun FitTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our handcrafted fitness theme by default
    content: @Composable () -> Unit,
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

// Alias for compatibility
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    FitTrackTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}

