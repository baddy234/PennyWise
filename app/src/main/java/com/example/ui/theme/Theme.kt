package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppThemeMode(val displayName: String) {
    SYSTEM("System Default"),
    DARK("Dark Glass"),
    LIGHT("Light Glass")
}

private val DarkColorScheme = darkColorScheme(
    primary = AccentCyan,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF075985),
    onPrimaryContainer = Color(0xFFE0F2FE),
    secondary = AccentViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4C1D95),
    onSecondaryContainer = Color(0xFFEDE9FE),
    tertiary = AccentEmerald,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF064E3B),
    onTertiaryContainer = Color(0xFFD1FAE5),
    error = AccentRose,
    onError = Color.White,
    errorContainer = Color(0xFF881337),
    onErrorContainer = Color(0xFFFFE4E6),
    background = DarkBackground,
    onBackground = Slate50,
    surface = DarkSurface,
    onSurface = Slate50,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Slate300,
    outline = Slate700,
    outlineVariant = Slate800
)

private val LightColorScheme = lightColorScheme(
    primary = AccentIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = Color(0xFF312E81),
    secondary = AccentCyan,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0C4A6E),
    tertiary = AccentEmerald,
    onTertiary = Color.White,
    background = Color(0xFFF1F5F9), // Frosted light slate
    onBackground = Color.Black,
    surface = Color(0xFFFFFFFF),
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF0F172A),
    outline = Slate300
)

@Composable
fun isAppInDarkTheme(): Boolean {
    val bg = MaterialTheme.colorScheme.background
    return bg == DarkBackground || bg == Color(0xFF030712)
}

@Composable
fun textPrimaryColor(): Color = if (isAppInDarkTheme()) Slate50 else Color.Black

@Composable
fun textSecondaryColor(): Color = if (isAppInDarkTheme()) Slate300 else Color(0xFF0F172A)

@Composable
fun textMutedColor(): Color = if (isAppInDarkTheme()) Slate400 else Color(0xFF334155)

@Composable
fun glassModalContainerColor(): Color = if (isAppInDarkTheme()) Slate900 else Color(0xF5F8FAFC)

@Composable
fun glassSurfaceBgColor(): Color = if (isAppInDarkTheme()) GlassSurfaceDark else Color(0xE6FFFFFF)

@Composable
fun glassBorderColor(): Color = if (isAppInDarkTheme()) GlassBorderDark else Color(0x33000000)

@Composable
fun appTextFieldColors(
    focusedBorderColor: Color = AccentCyan,
    unfocusedBorderColor: Color? = null
) = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
    focusedTextColor = if (isAppInDarkTheme()) Slate50 else Color.Black,
    unfocusedTextColor = if (isAppInDarkTheme()) Slate100 else Color.Black,
    focusedBorderColor = focusedBorderColor,
    unfocusedBorderColor = unfocusedBorderColor ?: if (isAppInDarkTheme()) Slate700 else Color(0x33000000),
    focusedContainerColor = if (isAppInDarkTheme()) GlassSurfaceDark else Color(0xF0FFFFFF),
    unfocusedContainerColor = if (isAppInDarkTheme()) GlassSurfaceDark else Color(0xE6FFFFFF),
    focusedLabelColor = focusedBorderColor,
    unfocusedLabelColor = if (isAppInDarkTheme()) Slate300 else Color(0xFF0F172A),
    focusedPlaceholderColor = if (isAppInDarkTheme()) Slate400 else Color(0xFF475569),
    unfocusedPlaceholderColor = if (isAppInDarkTheme()) Slate400 else Color(0xFF475569)
)

@Composable
fun MyApplicationTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    darkTheme: Boolean = when (themeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
    },
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}

