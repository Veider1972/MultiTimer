package ru.veider.multitimer.repository

import ru.veider.multitimer.data.Counter

class CountersRepositoryImpl : CountersRepository {

    override fun getAll() = CountersDataSource.getInstance().getAll()

    override fun updateCounter(counter: Counter) {
        CountersDataSource.getInstance().updateCounter(counter)
    }

    override fun addCounter(counter: Counter) {
        CountersDataSource.getInstance().addCounter(counter)
    }

    override fun deleteCounter(id: Int) {
        CountersDataSource.getInstance().deleteCounter(id)
    }

    override fun deleteAllCounter(){
        CountersDataSource.getInstance().deleteAllCounter()
    }
}