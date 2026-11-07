package ru.veider.multitimer.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
private fun dynamicScheme(darkTheme: Boolean) = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> dynamicLightColorScheme(LocalContext.current)
    darkTheme -> DarkColors
    else -> LightColors
}

private val LightColors = lightColorScheme(
    primary = Color(0xFF6F9C3B),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD4EDBA),
    onPrimaryContainer = Color(0xFF132904),
    secondary = Color(0xFF567E3D),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD8E8BC),
    onSecondaryContainer = Color(0xFF142202),
    tertiary = Color(0xFF3E6A66),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFC1F1EC),
    onTertiaryContainer = Color(0xFF00201E),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF7FBEF),
    onBackground = Color(0xFF191D16),
    surface = Color(0xFFF7FBEF),
    onSurface = Color(0xFF191D16),
    surfaceVariant = Color(0xFFDFE5D6),
    onSurfaceVariant = Color(0xFF43493E),
    outline = Color(0xFF73796C),
    outlineVariant = Color(0xFFC3C9B8),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF1F5EA),
    surfaceContainer = Color(0xFFEBEFE4),
    surfaceContainerHigh = Color(0xFFE6EADF),
    surfaceContainerHighest = Color(0xFFE0E5D9),
    inverseSurface = Color(0xFF2E332A),
    inverseOnSurface = Color(0xFFF0F2EB),
    inversePrimary = Color(0xFFA7D372),
    scrim = Color(0xFF000000)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFA7D372),
    onPrimary = Color(0xFF132904),
    primaryContainer = Color(0xFF3B5722),
    onPrimaryContainer = Color(0xFFD4EDBA),
    secondary = Color(0xFFBACEA1),
    onSecondary = Color(0xFF253318),
    secondaryContainer = Color(0xFF3B492D),
    onSecondaryContainer = Color(0xFFD6E5BB),
    tertiary = Color(0xFF8FD0CB),
    onTertiary = Color(0xFF003735),
    tertiaryContainer = Color(0xFF164B48),
    onTertiaryContainer = Color(0xFFA8ECE7),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF101410),
    onBackground = Color(0xFFE2E7DD),
    surface = Color(0xFF101410),
    onSurface = Color(0xFFE2E7DD),
    surfaceVariant = Color(0xFF42493C),
    onSurfaceVariant = Color(0xFFC3C9B8),
    outline = Color(0xFF8B9382),
    outlineVariant = Color(0xFF42493C),
    surfaceContainerLowest = Color(0xFF0A0E0A),
    surfaceContainerLow = Color(0xFF171C17),
    surfaceContainer = Color(0xFF191E18),
    surfaceContainerHigh = Color(0xFF232922),
    surfaceContainerHighest = Color(0xFF2E342C),
    inverseSurface = Color(0xFFE2E7DD),
    inverseOnSurface = Color(0xFF31352F),
    inversePrimary = Color(0xFF6F9C3B),
    scrim = Color(0xFF000000)
)

@Composable
fun MultiTimerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Material You is opt-in now: the app keeps its own green palette unless a screen
    // explicitly asks for wallpaper colours, otherwise the brand look is unstable.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> dynamicScheme(darkTheme)
        darkTheme -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
