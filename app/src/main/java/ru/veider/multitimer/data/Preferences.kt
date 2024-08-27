package ru.veider.multitimer.data

data class Preferences(
    var keepScreenOn: Boolean = false,
    var unlimitedCounter: Boolean = true,
    var counterLimits: Int = 20
)