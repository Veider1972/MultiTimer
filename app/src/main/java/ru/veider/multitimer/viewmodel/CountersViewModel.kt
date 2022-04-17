package ru.veider.multitimer.viewmodel

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.veider.multitimer.CountersApp
import ru.veider.multitimer.const.*
import ru.veider.multitimer.data.Counter
import ru.veider.multitimer.data.Counters
import ru.veider.multitimer.repository.CountersDataSource
import ru.veider.multitimer.service.CountersService
import java.util.*

class CountersViewModel : ViewModel() {

    private var counters: Counters
    val getCounters get() = counters

    private val db = CountersDataSource.getInstance()

    private val countersLiveData: MutableLiveData<Counters> = MutableLiveData<Counters>()
    private val counterLiveData: MutableLiveData<Counter> = MutableLiveData<Counter>()

    fun counters() = countersLiveData
    fun counter() = counterLiveData

    companion object {
        private var instance: CountersViewModel? = null
        fun getInstance() = instance?.apply {} ?: CountersViewModel().also { instance = it }
    }

    init {
        counters = db.getAll()
        if (counters.size == 0) addCounter()
        countersLiveData.postValue(counters)
        runService(counters)
    }

    fun saveCounters() {
        db.deleteAllCounter()
        for (counter in counters) {
            db.addCounter(counter)
        }
    }

    private fun updateCounters() {
        countersLiveData.postValue(counters)
    }

    fun addCounter() {
        counters.new().apply {
            updateCounters()
        }
    }

    fun deleteCounter(id: Int) {
        counters.delByID(id)
        countersLiveData.postValue(counters)
        removeCounter(id)
    }

    fun updateTitle(id: Int, title: String) {
        counters[counters.getIndexByID(id)].apply {
            this.title = title
            counterLiveData.postValue(this)
        }
    }

    fun updateMaxProgress(id: Int, time: Int) {
        counters[counters.getIndexByID(id)].apply {
            maxProgress = time
            currentProgress = time
            counterLiveData.postValue(this)
        }
    }

    fun startCounter(id: Int) {
        counters[counters.getIndexByID(id)].apply {
            if (this.state == CounterState.PAUSED || this.state == CounterState.FINISHED) {
                state = CounterState.RUN
                startTime = Date().time
            }
        }.apply {
            storeCounter(this)
            updateService(this)
        }
    }

    fun pauseCounter(id: Int) {
        counters[counters.getIndexByID(id)].apply {
            if (this.state == CounterState.RUN) {
                state = CounterState.PAUSED
            }
        }.apply {
            storeCounter(this)
            updateService(this)
        }
    }

    fun stopCounter(id: Int) {
        counters[counters.getIndexByID(id)].apply {
            if (this.state != CounterState.FINISHED) {
                state = CounterState.FINISHED
                currentProgress = maxProgress
            }
        }.apply {
            storeCounter(this)
            updateService(this)
        }
    }

    fun timerTick(id: Int, progress: Int) {
        counters[counters.getIndexByID(id)].apply {
            currentProgress = progress
            counterLiveData.postValue(this)
        }
    }

    fun timerFinish(id: Int) {
        counters[counters.getIndexByID(id)].apply {
            state = CounterState.FINISHED
            currentProgress = maxProgress
            startTime = 0
            counterLiveData.postValue(this)
        }
    }

    fun timerAlarmed(id: Int) {
        counters[counters.getIndexByID(id)].apply {
            state = CounterState.ALARMED
            currentProgress = 0
            startTime = 0
            storeCounter(this)
            counterLiveData.postValue(this)
        }
    }

    private fun removeCounter(id: Int) {
        Thread {
            db.deleteCounter(id)
            Log.d(TAG, "Удалён счётчик: $id")
        }.start()
    }

    private fun storeCounter(counter: Counter) {
        Thread {
            db.updateCounter(counter)
            Log.d(TAG, "Сохранён счётчик: $counter")
        }.start()
    }

    private fun runService(counters: Counters) {
        startService(Intent(CountersApp.getInstance()?.applicationContext, CountersService::class.java).apply {
            putExtra(EVENT, EVENT_START)
            putExtra(COUNTERS, Bundle().apply {
                putParcelable(COUNTERS, counters)
            })
        })
    }

    private fun updateService(counter: Counter) {
        startService(Intent(CountersApp.getInstance()?.applicationContext, CountersService::class.java).apply {
            putExtra(EVENT, EVENT_BUTTON)
            putExtra(COUNTER, Bundle().apply {
                putParcelable(COUNTER, counter)
            })
        })
    }

    private fun startService(intent: Intent) {
        CountersApp.getInstance()?.applicationContext?.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                ContextCompat.startForegroundService(this, intent)
            else
                this.startService(intent)
        }
    }
}