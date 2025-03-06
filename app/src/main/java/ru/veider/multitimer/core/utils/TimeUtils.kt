package ru.veider.multitimer.core.utils

import ru.veider.multitimer.const.firstZero
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.*

val Int.hours get() = this / 3600
val Int.minutes get() = (this - this.hours * 3600) / 60
val Int.seconds get() = this - this.hours * 3600 - this.minutes * 60

val Int.HhMmSs get() = "${firstZero(this.hours)}:${firstZero(this.minutes)}:${firstZero(this.seconds)}"

fun getTime(hours: Int, minutes: Int, seconds: Int) = hours * 3600 + minutes * 60 + seconds