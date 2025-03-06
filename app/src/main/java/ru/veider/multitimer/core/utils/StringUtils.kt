package ru.veider.multitimer.core.utils

fun Int.toRus(
	name: String,
	singleEnding: String = "",
	twoToFourEnding: String = "",
	fiveToTenEnding: String = ""
): String =
	"$name${
		if ((this / 10) % 10 != 1) {
			when (this % 10) {
				1       -> singleEnding
				in 2..4 -> twoToFourEnding
				else    -> fiveToTenEnding
			}
		} else fiveToTenEnding
	}"

fun Int.toMinutes(): String =
	when{
		this in 11..14                    -> "минут"
		"$this".last() == '1'                   -> "минута"
		"$this".last() in listOf('2', '3', '4') -> "минуты"
		else                                    -> "минут"
	}

fun Int.toHours(): String =
	when{
		this in 11..14                    -> "часов"
		"$this".last() == '1'                   -> "час"
		"$this".last() in listOf('2', '3', '4') -> "часа"
		else                                    -> "часов"
	}

fun Int.toDays(): String =
	when {
		this in 11..14                    -> "дней"
		"$this".last() == '1'                   -> "день"
		"$this".last() in listOf('2', '3', '4') -> "дня"
		else                                    -> "дней"
	}

fun Int.toWeeks(): String =
	when {
		this in 11..14                    -> "недель"
		"$this".last() == '1'                   -> "неделю"
		"$this".last() in listOf('2', '3', '4') -> "недели"
		else                                    -> "недель"
	}

fun Int.toMonth(): String =
	when {
		this in 11..14                    -> "месяцев"
		"$this".last() == '1'                   -> "месяц"
		"$this".last() in listOf('2', '3', '4') -> "месяца"
		else                                    -> "месяцев"
	}

fun Int.toYears(): String =
	when {
		this in 11..14                    -> "лет"
		"$this".last() == '1'                   -> "год"
		"$this".last() in listOf('2', '3', '4') -> "года"
		else                                    -> "лет"
	}

/**
 * Класс для вывода числительных с правильными окончаниями в зависимости от количества
 * */
data class RusIntPlural(
	val name: String,
	val number: Int,
	val singleEnding: String = "",
	val twoToFourEnding: String = "",
	val fiveToTenEnding: String = ""
) {
	override fun toString(): String {
		val suffix =
			if ((number / 10) % 10 != 1) {
				when (number % 10) {
					1 -> singleEnding
					in 2..4 -> twoToFourEnding
					else -> fiveToTenEnding
				}
			} else fiveToTenEnding
		return "$number $name$suffix"
	}
}