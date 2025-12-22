package ru.veider.multitimer.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen: NavKey {
    @Serializable
    data object Counters: Screen

    @Serializable
    data object Settings: Screen

    @Serializable
    data object About: Screen
}