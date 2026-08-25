package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Semantic Theme Colors (Locked - Never modified by device wallpaper/dynamic colors)
val SemanticFreshGreen = Color(0xFF16A34A) // 3-day or longer safe
val SemanticFreshGreenBg = Color(0xFFDCFCE7)
val SemanticExpiringSoonAmber = Color(0xFFD97706) // 3-day warning
val SemanticExpiringSoonAmberBg = Color(0xFFFEF3C7)
val SemanticExpiredRed = Color(0xFFDC2626) // Expired
val SemanticExpiredRedBg = Color(0xFFFEE2E2)
val SemanticFrozenBlue = Color(0xFF0284C7) // Frozen
val SemanticFrozenBlueBg = Color(0xFFE0F2FE)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF2DD4BF), // Glowing Mint / Teal Accent
    onPrimary = Color(0xFF003833),
    primaryContainer = Color(0xFF134E4A),
    onPrimaryContainer = Color(0xFF99F6E4),
    secondary = Color(0xFF38BDF8),
    onSecondary = Color(0xFF082F49),
    secondaryContainer = Color(0xFF0C4A6E),
    onSecondaryContainer = Color(0xFFBAE6FD),
    tertiary = Color(0xFFFBBF24),
    onTertiary = Color(0xFF451A03),
    background = Color(0xFF0B1120), // Deep rich slate dark
    surface = Color(0xFF1E293B),
    surfaceVariant = Color(0xFF334155),
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFF8FAFC),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF475569),
    outlineVariant = Color(0xFF334155)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0D9488), // Fresh modern Teal / Mint
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE6FFFA), // Luminous glassy mint glow
    onPrimaryContainer = Color(0xFF0F766E),
    secondary = Color(0xFF0284C7), // Bright Sky Azure
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE), // Soft glassy sky
    onSecondaryContainer = Color(0xFF0369A1),
    tertiary = Color(0xFFF59E0B), // Sunny Amber
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFEF3C7),
    onTertiaryContainer = Color(0xFFB45309),
    background = Color(0xFFF3F5F9), // Crisp, bright modern off-white background
    surface = Color(0xFFFFFFFF), // Pure white cards for elevated shadow contrast
    surfaceVariant = Color(0xFFE9EEF5), // Soft subtle light grey
    onBackground = Color(0xFF0F172A), // Crisp high-contrast Slate
    onSurface = Color(0xFF0F172A),
    onSurfaceVariant = Color(0xFF64748B),
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Defaults strictly to Light Theme as requested
    // Fixed: dynamicColor is forced to false so app color never changes with device theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
