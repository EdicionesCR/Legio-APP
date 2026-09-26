package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = LegioWinePrimaryDark,
    onPrimary = LegioWineDark,
    primaryContainer = LegioWineDark,
    onPrimaryContainer = LegioGoldContainer,
    secondary = LegioGoldAccentDark,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF42371E),
    onSecondaryContainer = LegioGoldContainer,
    tertiary = LegioGoldAccentDark,
    background = LegioSurfaceDark,
    onBackground = LegioTextPrimaryDark,
    surface = LegioSurfaceDarkCard,
    onSurface = LegioTextPrimaryDark,
    surfaceVariant = Color(0xFF2C2428),
    onSurfaceVariant = LegioTextSecondaryDark,
    outline = Color(0xFF554449)
)

private val LightColorScheme = lightColorScheme(
    primary = LegioWinePrimary,
    onPrimary = Color.White,
    primaryContainer = LegioWineDark,
    onPrimaryContainer = Color.White,
    secondary = LegioGoldAccent,
    onSecondary = Color(0xFF2B2005),
    secondaryContainer = LegioGoldContainer,
    onSecondaryContainer = Color(0xFF4E3A06),
    tertiary = LegioWineLight,
    background = LegioSurfaceLight,
    onBackground = LegioTextPrimary,
    surface = LegioSurfaceCard,
    onSurface = LegioTextPrimary,
    surfaceVariant = LegioSurfaceVariant,
    onSurfaceVariant = LegioTextSecondary,
    outline = LegioBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // We favor our custom dignified institutional color scheme over dynamic wallpaper colors
    dynamicColor: Boolean = false,
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
