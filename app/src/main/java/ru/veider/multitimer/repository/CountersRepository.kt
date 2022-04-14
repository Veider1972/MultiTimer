package ru.veider.multitimer.repository

import ru.veider.multitimer.data.Counter
import ru.veider.multitimer.data.Counters

interface CountersRepository {

    fun getAll(): Counters

    fun updateCounter(counter: Counter)

    fun addCounter(counter: Counter)

    fun deleteCounter(id: Int)

    fun deleteAllCounter()
}