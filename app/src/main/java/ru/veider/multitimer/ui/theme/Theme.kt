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
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme
    @Composable
    get() = darkColorScheme(
        primary = colorPrimary,
        onPrimary = colorOnPrimary,
        primaryContainer = colorPrimaryContainer,
        onPrimaryContainer = colorOnPrimaryContainer,
        secondary = colorSecondary,
        onSecondary = colorOnSecondary,
        onSecondaryContainer = colorOnSecondaryContainer,
    )

private val LightColorScheme
    @Composable
    get() = lightColorScheme(
        primary = colorPrimary,
        onPrimary = colorOnPrimary,
        primaryContainer = colorPrimaryContainer,
        onPrimaryContainer = colorOnPrimaryContainer,
        secondary = colorSecondary,
        onSecondary = colorOnSecondary,
        onSecondaryContainer = colorOnSecondaryContainer,
    )

@Composable
fun MultiTimerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
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