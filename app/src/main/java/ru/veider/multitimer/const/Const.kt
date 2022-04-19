package ru.veider.multitimer.const

enum class CounterState {
    PAUSED, RUN, FINISHED, ALARMED
}

const val TAG = "TAG"

const val ALARM_CHANNEL_ID = "ALARM_CHANNEL_ID"
const val SIMPLE_CHANNEL_ID = "SIMPLE_CHANNEL_ID"
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
var PRIMARY_KEY = 0L
val vibroPattern = arrayOf(500L, 500L, 500L, 500L, 500L, 500L, 500L, 500L, 500L).toLongArray()
