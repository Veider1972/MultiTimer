package ru.veider.multitimer.utils

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun firstZero(data: Int) = if (data < 10) "0$data" else "$data"

//fun Int.toTime(): String {
//    val hours = this / 3600
//    val minutes = (this - hours*3600)/60
//    val seconds = this - hours*3600 - minutes*60
//    return "${firstZero(hours)}:${firstZero(minutes)}:${firstZero(seconds)}"
//}