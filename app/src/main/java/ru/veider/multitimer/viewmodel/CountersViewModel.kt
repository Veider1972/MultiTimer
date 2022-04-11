package ru.veider.multitimer.viewmodel

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.veider.multitimer.CountersApp
import ru.veider.multitimer.data.Counter
import ru.veider.multitimer.data.CounterState
import ru.veider.multitimer.data.Counters
import ru.veider.multitimer.repository.CountersRepositoryImpl
import ru.veider.multitimer.service.CountersService
import java.util.*

class CountersViewModel : ViewModel() {

    private lateinit var counters: Counters
    val getCounters get() = counters
    val context: Context? = CountersApp.getInstance()?.applicationContext

    private val db = CountersRepositoryImpl.getInstance()

    private val countersLiveData: MutableLiveData<Counters> = MutableLiveData<Counters>()
    private val counterLiveData: MutableLiveData<Counter> = MutableLiveData<Counter>()
    private val serviceLiveData: MutableLiveData<Counter> = MutableLiveData<Counter>()

    fun counters() = countersLiveData
    fun counter() = counterLiveData
    fun serviceCounter() = serviceLiveData

    companion object {
        private var instance: CountersViewModel? = null
        fun getInstance() =
                instance ?: synchronized(CountersViewModel::class.java) {
                    instance ?: CountersViewModel().also { instance = it }
                }
    }

    init {
        counters = db.getAll()
        if (counters.size == 0) addCounter()
        countersLiveData.postValue(counters)
    }

    fun saveCounters() {
        db.deleteAllCounter()
        for (counter in counters) {
            db.addCounter(counter)
        }
    }

    fun updateCounters() {
        countersLiveData.postValue(counters)
    }

    fun addCounter() {
        counters.new().apply {
            updateCounters()
        }
    }

    fun updateCounter(counter: Counter) {
        counterLiveData.postValue(counter)
    }

    fun deleteCounter(id: Int) {
        counters.delByID(id)
        countersLiveData.postValue(counters)
    }

    fun updateTitle(id: Int, title: String) {
        counters[counters.getIndexByID(id)].apply {
            this.title = title
            counterLiveData.postValue(this)
        }

    }

    fun updateMaxProgress(id: Int, time: Int) {
        val counter = counters[counters.getIndexByID(id)].apply {
            maxProgress = time
            currentProgress = time
        }
        counterLiveData.postValue(counter)
    }

    fun startCounter(id: Int) {
        runService()
        val counter = counters[counters.getIndexByID(id)].apply {
            if (currentProgress == 0) currentProgress = maxProgress
            state = CounterState.RUN
            startTime = Date().time
        }
        serviceLiveData.postValue(counter)
    }

    fun pauseCounter(id: Int) {
        runService()
        val counter = counters[counters.getIndexByID(id)].apply {
            state = CounterState.PAUSED
//            startTime = 0
        }
        serviceLiveData.postValue(counter)
    }

    fun stopCounter(id: Int) {
        runService()
        val counter = counters[counters.getIndexByID(id)].apply {
            state = CounterState.FINISHED
            currentProgress = maxProgress
            //          startTime = 0
        }
        serviceLiveData.postValue(counter)
    }

    fun timerTick(id: Int, progress: Int) {
        counters[counters.getIndexByID(id)].apply {
            currentProgress = progress
            counterLiveData.postValue(this)
        }
    }

    fun timerPause(id: Int) {
        counters[counters.getIndexByID(id)].apply {
            state = CounterState.PAUSED
            startTime = 0
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
            db.updateCounter(this)
            counterLiveData.postValue(this)
        }
    }

    fun runService() {
        context?.let{
            val intent = Intent(context, CountersService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                ContextCompat.startForegroundService(context, intent)
            else
                context.startService(intent)
        }
    }

}