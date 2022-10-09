package ru.veider.multitimer.const

enum class CounterState {
    PAUSED, RUN, FINISHED, ALARMED
}

const val TAG = "TAG"

const val SIMPLE_CHANNEL_ID = "SIMPLE_CHANNEL_ID"
const val SIMPLE_CHANNEL_NUM = 1
const val ALARM_CHANNEL_ID = "ALARM_CHANNEL_ID"
const val ALARM_CHANNEL_NUM = 2
const val COUNTER_ID = "COUNTER_ID"
const val COUNTER = "COUNTER"
const val COUNTERS = "COUNTERS"
const val COUNTERS_BUNDLE = "COUNTERS_BUNDLE"
const val EVENT = "EVENT"
const val ON_RUN_CLICK = "ON_RUN_CLICK"
const val ON_PAUSE_CLICK = "ON_PAUSE_CLICK"
const val ON_STOP_CLICK = "ON_STOP_CLICK"
const val ON_ALARM_TIMER = "ON_ALARM_TIMER"
const val ON_START_SERVICE = "ON_START_SERVICE"
const val ON_STOP_SERVICE = "ON_STOP_SERVICE"
const val DB_NAME = "Counters.db"


fun firstZero(n: Int) = if (n in 0..9) "0$n" else "$n"
fun Int.toTime():String{
    var progress = this
    val hours = (progress / 3600)
    progress %= 3600
    val minutes = (progress / 60)
    val seconds = (progress % 60)
    return "${firstZero(hours)}:${firstZero(minutes)}:${firstZero(seconds)}"
}

fun Int.toShortTime():String{
    var progress = this
    val hours = (progress / 3600)
    progress %= 3600
    val minutes = (progress / 60)
    val seconds = (progress % 60)
    return if (hours>=1) "${firstZero(hours)}:${firstZero(minutes)}" else "${firstZero(minutes)}:${firstZero(seconds)}"
}

var PRIMARY_KEY = 0L
val vibroPattern = arrayOf(500L, 500L, 500L, 500L, 500L, 500L, 500L, 500L, 500L).toLongArray()
