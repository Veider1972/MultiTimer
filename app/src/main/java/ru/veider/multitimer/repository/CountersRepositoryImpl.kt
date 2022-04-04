package ru.veider.multitimer.repository

import androidx.room.Room
import ru.veider.multitimer.CountersApp
import ru.veider.multitimer.data.Counter
import ru.veider.multitimer.data.CounterState
import ru.veider.multitimer.data.Counters
import java.util.*
import kotlin.collections.ArrayList

private const val DB_NAME = "Counters.db"

class CountersRepositoryImpl : CountersRepository {

    private lateinit var db: CountersDataBase

    companion object {

        private var instance: CountersRepositoryImpl? = null

        @JvmStatic
        fun getInstance(): CountersRepositoryImpl {
            if (instance == null)
                instance = CountersRepositoryImpl()
            return instance!!
        }

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
        0,
        counter.id,
        counter.currentProgress,
        counter.maxProgress,
        counter.startTime,
        counter.state.ordinal,
        counter.title
    )

    override fun getAll() = db.countersDao().getAll().mapTo(Counters()){ entity -> counterFromEntity(entity) }

    override fun updateCounter(counter: Counter) {
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

    override fun addCounter(counter: Counter) {
        db.countersDao().addCounter(entityFromCounter(counter))
    }

    override fun deleteCounter(id: Int) {
        db.countersDao().deleteCounter(id)
    }

    override fun deleteAllCounter(){
        db.countersDao().deleteAllCounter()
    }
}