package ru.veider.multitimer.repository

import androidx.room.Room
import ru.veider.multitimer.CountersApp
import ru.veider.multitimer.const.CounterState
import ru.veider.multitimer.const.DB_NAME
import ru.veider.multitimer.const.PRIMARY_KEY
import ru.veider.multitimer.data.Counter
import ru.veider.multitimer.data.Counters

class CountersDataSource {
    private lateinit var db: CountersDataBase

    companion object {
        private var instance: CountersDataSource? = null
        fun getInstance() = instance?.apply {  } ?: CountersDataSource().also {instance = it}
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

    fun getAll() = db.countersDao().getAll().mapTo(Counters()){ entity -> counterFromEntity(entity) }

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
        }
    }

    fun addCounter(counter: Counter) {
        db.countersDao().addCounter(entityFromCounter(counter))
    }

    fun deleteCounter(id: Int) {
        db.countersDao().deleteCounter(id)
    }

    fun deleteAllCounter(){
        db.countersDao().deleteAllCounter()
    }
}