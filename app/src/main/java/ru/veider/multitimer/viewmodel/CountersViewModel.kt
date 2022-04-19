package ru.veider.multitimer.viewmodel

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import ru.veider.multitimer.CountersApp
import ru.veider.multitimer.R
import ru.veider.multitimer.const.*
import ru.veider.multitimer.data.Counter
import ru.veider.multitimer.data.Counters
import ru.veider.multitimer.repository.CountersDataSource
import ru.veider.multitimer.service.CountersService
import java.util.*

@OptIn(DelicateCoroutinesApi::class) class CountersViewModel : ViewModel() {

    private var counters: Counters
    val getCounters get() = counters
    private val context get() = CountersApp.getInstance()?.applicationContext

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

        if (counters.size == 0) {
            addCounter()
            saveCounters()
        }
        countersLiveData.postValue(counters)
        onStartProgram()
    }

    fun saveCounters() {
        GlobalScope.run {
            db.deleteAllCounter()
            for (counter in counters) {
                db.addCounter(counter)
            }
        }
    }

    private fun updateCounters() {
        countersLiveData.postValue(counters)
        saveCounters()
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
            storeCounter(this)
        }
    }

    fun updateMaxProgress(id: Int, time: Int) {
        counters[counters.getIndexByID(id)].apply {
            maxProgress = time
            currentProgress = time
            counterLiveData.postValue(this)
            storeCounter(this)
        }
    }

    fun startCounter(id: Int) {
        with(counters[counters.getIndexByID(id)]) {
            if (this.state == CounterState.PAUSED || this.state == CounterState.FINISHED) {
                state = CounterState.RUN
                startTime = Date().time
                storeCounter(this)
                sendToService(this, ON_RUN_CLICK)
            }
        }
    }

    fun pauseCounter(id: Int) {
        with(counters[counters.getIndexByID(id)]) {
            if (this.state == CounterState.RUN) {
                state = CounterState.PAUSED
                storeCounter(this)
                sendToService(this, ON_PAUSE_CLICK)
            }
        }
    }

    fun stopCounter(id: Int) {
        with(counters[counters.getIndexByID(id)]) {
            if (this.state != CounterState.FINISHED) {
                state = CounterState.FINISHED
                currentProgress = maxProgress
                storeCounter(this)
                sendToService(this, ON_STOP_CLICK)
            }
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
        GlobalScope.run {
            db.deleteCounter(id)
        }
    }

    private fun storeCounter(counter: Counter) {
        GlobalScope.run {
            db.updateCounter(counter)
        }
    }

    private fun onStartProgram() {
        val intent = Intent(context, CountersService::class.java).apply {
            putExtra(EVENT, ON_START_SERVICE)
            putExtra(COUNTERS, Bundle().apply {
                putSerializable(COUNTERS_BUNDLE, counters)
            })
        }
        startService(intent)
    }

    private fun sendToService(counter: Counter, event: String) {
        if (event != ON_RUN_CLICK && event != ON_PAUSE_CLICK && event != ON_STOP_CLICK && event != ON_ALARM_TIMER)
            throw Exception(context?.resources?.getString(R.string.error_service_event))
        startService(Intent(context, CountersService::class.java).apply {
            putExtra(EVENT, event)
            putExtra(COUNTER, counter)
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