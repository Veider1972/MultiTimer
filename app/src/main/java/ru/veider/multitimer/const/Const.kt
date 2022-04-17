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
const val EVENT = "EVENT"
const val EVENT_BUTTON = "EVENT_BUTTON"
const val EVENT_TIMER = "EVENT_TIMER"
const val EVENT_STOP = "EVENT_STOP"
const val EVENT_START = "EVENT_STOP"
const val DB_NAME = "Counters.db"
const val PRIMARY_KEY = 1L
val vibroPattern = arrayOf(500L, 500L, 500L, 500L, 500L, 500L, 500L, 500L, 500L).toLongArray()
