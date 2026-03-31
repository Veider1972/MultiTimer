package ru.veider.multitimer.repository

import android.util.Log
import ru.veider.multitimer.const.TAG
import ru.veider.multitimer.data.Counter
import ru.veider.multitimer.domain.usecases.toCounter
import ru.veider.multitimer.domain.usecases.toCounterEntity

class CountersRepositoryImpl(
    private val db: CountersDB
) : CountersRepository {

    override suspend fun getAll() = db.dao().getAll().map { it.toCounter() }
    override suspend fun get(id: Int) = db.dao().get(id)?.toCounter()

    override suspend fun upsert(counter: Counter) = db.dao().upsert(counter.toCounterEntity()).also {  Log.d(TAG, "Счётчик сохранён : $counter") }

    override suspend fun upsert(counters: List<Counter>) = db.dao().upsert(counters.map { it.toCounterEntity() }).also {  Log.d(TAG, "Счётчики сохранены: $counters") }

    override suspend fun delete(id: Int) = db.dao().delete(id)

    override suspend fun deleteAll() = db.dao().deleteAll()
}