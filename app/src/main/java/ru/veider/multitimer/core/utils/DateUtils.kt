package ru.veider.multitimer.core.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

val LocalDateTime.daysTo get()= this.toLocalDate().daysTo
val LocalDate.daysTo get() = ChronoUnit.DAYS.between(LocalDate.now(), this).toInt().let {
		when (it) {
			-2 -> "Позавчера"
			-1 -> "Вчера"
			0 -> "Сегодня"
			1 -> "Завтра"
			2 -> "Послезавтра"
			else -> (if (it > 2) " " else " -") + "${kotlin.math.abs(it)} " + kotlin.math.abs(it).toRus(
				"д", "ень", "ня", "ней"
			)
		}
	}

fun LocalDateTime.timeTo(dateTime: LocalDateTime): Long = ChronoUnit.MINUTES.between(this, dateTime)

fun Long.toRestTime(): String {
	val days = (this/1440f).toInt()
	val hours = ((this - (days*1440f))/60).toInt()
	val minutes = Math.round(this - days*1440f - hours*60)
	return when {
		days > 0 -> "$days ${days.toDays()} $hours ${hours.toHours()} $minutes ${minutes.toMinutes()}"
		hours > 0 -> "$hours ${hours.toHours()} $minutes ${minutes.toMinutes()}"
		else -> "$minutes ${minutes.toMinutes()}"
	}
}

val LocalDateTime.shortDaysTo get()= this.toLocalDate().shortDaysTo
val LocalDate.shortDaysTo
	get() = ChronoUnit.DAYS.between(LocalDate.now(), this).toInt().let {
		when (it) {
			0 -> "Сегодня"
			1 -> "Завтра"
			else -> "$it дн."
		}
	}

val LocalDateTime.homeTitle get()= this.toLocalDate().homeTitle
val LocalDate.homeTitle get() = format(DateTimeFormatter.ofPattern("dd.MM.yyyy, EEEE", Locale.getDefault()))

val LocalDateTime.ddMmYyyy get()= this.toLocalDate().ddMmYyyy
val LocalDate.ddMmYyyy get()= format(DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault()))

val LocalDateTime.ddMmmm get()= this.toLocalDate().ddMmmm
val LocalDate.ddMmmm get()= format(DateTimeFormatter.ofPattern("dd MMMM", Locale.getDefault()))

/**
 * Считая текущую дату днём рождения, вывести в словесной форме возраст к заданной дате
 * ("1 год", "5 лет", "3 года")
 * @param [dateSinceBirthday] дата, к которой выводить возраст
 * */

fun LocalDateTime.toAgeFull(dateSinceBirthday: LocalDate) = this.toLocalDate().toAgeFull(dateSinceBirthday)
fun LocalDate.toAgeFull(dateSinceBirthday: LocalDate) = RusIntPlural(
	"", ChronoUnit.YEARS.between(this, dateSinceBirthday).toInt(), "год", "года", "лет"
).toString()

fun LocalDateTime.toAgeShort(dateSinceBirthday: LocalDate) = this.toLocalDate().toAgeShort(dateSinceBirthday)
/**
 * Считая текущую дату днём рождения, вывести в словесной форме возраст к заданной дате
 * ("1 год", "5 лет", "3 года")
 * @param [dateSinceBirthday] дата, к которой выводить возраст
 * */
fun LocalDate.toAgeShort(dateSinceBirthday: LocalDate) = ChronoUnit.YEARS.between(this, dateSinceBirthday).toString()

fun Int.toZeroStr() = if (this < 10) "0$this" else "$this"

fun LocalDate.toLong() = this.toEpochDay()

fun Long.toLocalDate(): LocalDate = LocalDate.ofEpochDay(this)
fun LocalTime.toInt() = this.toSecondOfDay()

fun Int.toLocalTime(): LocalTime = LocalTime.ofSecondOfDay(this.toLong())

fun LocalDateTime.inPeriod(fromDateTime:LocalDateTime, toDateTime: LocalDateTime) = (this.isAfter(fromDateTime) || this.isEqual(fromDateTime)) && this.isBefore(toDateTime)