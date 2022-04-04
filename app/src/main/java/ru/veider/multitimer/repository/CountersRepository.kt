package ru.veider.multitimer.repository

import androidx.room.Query
import ru.veider.multitimer.data.Counter
import ru.veider.multitimer.data.CounterState
import ru.veider.multitimer.data.Counters
import java.util.*

interface CountersRepository {

    fun getAll(): Counters

    fun updateCounter(counter: Counter)

    fun addCounter(counter: Counter)

    fun deleteCounter(id: Int)

    fun deleteAllCounter()
}