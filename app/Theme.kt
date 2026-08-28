package com.xos.personalsystem.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// XOS Solo Leveling inspired colors
object XOSColors {
    // Dark theme colors
    val Background = Color(0xFF0A0A0F)
    val Surface = Color(0xFF1A1A2E)
    val SurfaceLight = Color(0xFF2A2A45)
    val SurfaceDark = Color(0xFF0D0D1A)
    
    // Glowing accent colors
    val SystemBlue = Color(0xFF00B4FF)
    val SystemBlueGlow = Color(0xFF0088FF)
    val SystemPurple = Color(0xFF7B2FBE)
    val SystemGold = Color(0xFFFFD700)
    val SystemRed = Color(0xFFFF1744)
    val SystemGreen = Color(0xFF00E676)
    
    // Text colors
    val TextPrimary = Color.White
    val TextSecondary = Color(0xFFB0B0C0)
    val TextMuted = Color(0xFF666680)
    
    // Status colors
    val Success = Color(0xFF00E676)
    val Warning = Color(0xFFFFAB00)
    val Danger = Color(0xFFFF1744)
    val Info = Color(0xFF00B4FF)
}

@Composable
fun XOSPersonalSystemTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = XOSColors.SystemBlue,
            secondary = XOSColors.SystemPurple,
            tertiary = XOSColors.SystemGold,
            background = XOSColors.Background,
            surface = XOSColors.Surface,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onTertiary = Color.Black,
            onBackground = XOSColors.TextPrimary,
            onSurface = XOSColors.TextPrimary,
        )
    } else {
        lightColorScheme(
            primary = XOSColors.SystemBlue,
            secondary = XOSColors.SystemPurple,
            tertiary = XOSColors.SystemGold,
            background = Color(0xFFF5F5F5),
            surface = Color.White,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onTertiary = Color.Black,
            onBackground = Color(0xFF1A1A2E),
            onSurface = Color(0xFF1A1A2E),
        )
    }
    
    val typography = Typography(
        headlineLarge = MaterialTheme.typography.headlineLarge.copy(
            fontFamily = XOSFonts.monospace
        ),
        headlineMedium = MaterialTheme.typography.headlineMedium.copy(
            fontFamily = XOSFonts.monospace
        ),
        headlineSmall = MaterialTheme.typography.headlineSmall.copy(
            fontFamily = XOSFonts.monospace
        ),
        titleLarge = MaterialTheme.typography.titleLarge.copy(
            fontFamily = XOSFonts.monospace
        ),
        titleMedium = MaterialTheme.typography.titleMedium.copy(
            fontFamily = XOSFonts.monospace
        ),
        titleSmall = MaterialTheme.typography.titleSmall.copy(
            fontFamily = XOSFonts.monospace
        ),
        bodyLarge = MaterialTheme.typography.bodyLarge.copy(
            fontFamily = XOSFonts.sansSerif
        ),
        bodyMedium = MaterialTheme.typography.bodyMedium.copy(
            fontFamily = XOSFonts.sansSerif
        ),
        bodySmall = MaterialTheme.typography.bodySmall.copy(
            fontFamily = XOSFonts.sansSerif
        )
    )
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
