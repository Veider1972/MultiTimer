package ru.veider.multitimer.ui.compose.assets

import androidx.compose.foundation.isSystemInDarkTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import ru.veider.multitimer.R
import ru.veider.multitimer.ui.theme.colorPrimary
import ru.veider.multitimer.ui.theme.colorPrimaryDark

@Composable
fun SetSystemBarsContrast() {
    val systemUiController = rememberSystemUiController()
    val isDarkTheme = isSystemInDarkTheme()
    val darkColor = colorPrimary

    SideEffect {
        // Устанавливаем контрастные цвета для статус-бара и навигации
        systemUiController.setStatusBarColor(
            color = darkColor,
            darkIcons = !isDarkTheme
        )
        systemUiController.setNavigationBarColor(
            color = darkColor,
            darkIcons = !isDarkTheme
        )
    }
}