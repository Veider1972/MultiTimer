package ru.veider.multitimer.utils

import android.content.Context
import ru.veider.multitimer.R
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun firstZero(data: Int) = if (data < 10) "0$data" else "$data"

fun Int.toMinSec(context: Context): String {
    val hours: Int = this / 3600
    val minutes: Int = (this - hours * 3600) / 60
    val seconds: Int = this - hours * 3600 - 60 * minutes
    return if (hours == 0)
        String.format(context.resources.getString(R.string.time_min_sec_pattern), minutes, seconds)
    else
        String.format(context.resources.getString(R.string.time_hours_min_sec_pattern), hours, minutes, seconds)
}