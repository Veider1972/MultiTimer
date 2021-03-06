package ru.veider.multitimer.repository

import android.util.Log
import androidx.room.Room
import ru.veider.multitimer.CountersApp
import ru.veider.multitimer.const.CounterState
import ru.veider.multitimer.const.DB_NAME
import ru.veider.multitimer.const.PRIMARY_KEY
import ru.veider.multitimer.const.TAG
import ru.veider.multitimer.data.Counter
import ru.veider.multitimer.data.Counters

class CountersDataSource {
    private lateinit var db: CountersDataBase

    companion object {
        private var instance: CountersDataSource? = null
        fun getInstance() = instance?.apply { } ?: CountersDataSource().also { instance = it }
    }

    init {
        if (CountersApp.getInstance() != null) {
            db = Room.databaseBuilder(
                CountersApp.getInstance()!!.applicationContext,
                CountersDataBase::class.java,
                DB_NAME
            )
                .allowMainThreadQueries()
                .build()
        }
    }

    private fun counterFromEntity(entity: CounterEntity): Counter = Counter(
        entity.id,
        entity.currentProgress,
        entity.maxProgress,
        entity.startTime,
        enumValues<CounterState>()[entity.state],
        entity.title
    )

    private fun entityFromCounter(counter: Counter): CounterEntity = CounterEntity(
        PRIMARY_KEY,
        counter.id,
        counter.currentProgress,
        counter.maxProgress,
        counter.startTime,
        counter.state.ordinal,
        counter.title
    )

    fun getAll() =
            db
                .countersDao()
                .getAll()
                .mapTo(Counters()) { entity -> counterFromEntity(entity) }.also {
                    Log.d(TAG, "Считаны счётчики: $it")
                }

    fun updateCounter(counter: Counter) {
        counter.apply {
            db.countersDao().updateCounter(
                id,
                currentProgress,
                maxProgress,
                startTime,
                state.ordinal,
                title
            )
            Log.d(TAG, "Сохранён счётчик: $this")
        }
    }

    fun addCounter(counter: Counter) {
        db.countersDao().addCounter(entityFromCounter(counter))
        Log.d(TAG, "Добавлен счётчик: $counter")
    }

    fun deleteCounter(id: Int) {
        db.countersDao().deleteCounter(id)
        Log.d(TAG, "Удалён счётчик: $id")
    }

    fun deleteAllCounter() {
        db.countersDao().deleteAllCounter()
    }
}