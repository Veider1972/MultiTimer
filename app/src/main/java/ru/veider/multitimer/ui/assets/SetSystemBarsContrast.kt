package ru.veider.multitimer.ui.assets

import androidx.compose.foundation.isSystemInDarkTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import ru.veider.multitimer.ui.theme.colorPrimary

@Composable
fun SetSystemBarsContrast() {
    val systemUiController = rememberSystemUiController()
    val isDarkTheme = isSystemInDarkTheme()
    val darkColor = colorPrimary

    SideEffect {
        // Устанавливаем контрастные цвета для статус-бара и навигации
        systemUiController.setStatusBarColor(
            color = darkColor,
            darkIcons = true
        )
//        systemUiController.setNavigationBarColor(
//            color = darkColor,
//            darkIcons = true
//        )
    }
}