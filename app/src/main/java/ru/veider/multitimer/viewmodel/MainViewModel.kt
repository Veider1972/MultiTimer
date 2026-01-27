package ru.veider.multitimer.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.veider.multitimer.R
import ru.veider.multitimer.const.COUNTER
import ru.veider.multitimer.const.COUNTERS
import ru.veider.multitimer.const.COUNTERS_BUNDLE
import ru.veider.multitimer.const.CounterState
import ru.veider.multitimer.const.EVENT
import ru.veider.multitimer.const.ON_ALARM_TIMER
import ru.veider.multitimer.const.ON_PAUSE_CLICK
import ru.veider.multitimer.const.ON_RUN_CLICK
import ru.veider.multitimer.const.ON_START_SERVICE
import ru.veider.multitimer.const.ON_STOP_CLICK
import ru.veider.multitimer.const.TAG
import ru.veider.multitimer.data.Counter
import ru.veider.multitimer.data.addNew
import ru.veider.multitimer.data.deleteById
import ru.veider.multitimer.repository.CountersRepository
import ru.veider.multitimer.service.CountersService
import java.util.Date

class MainViewModel(
    val app: Application,
    private val repo: CountersRepository,
    private val gson: Gson
) : AndroidViewModel(app) {

    private val _counters: MutableStateFlow<List<Counter>> = MutableStateFlow(emptyList())
    val counters get()= _counters.asStateFlow()

    init {
        viewModelScope.launch {
            _counters.tryEmit(repo.getAll())
            Log.d(TAG, "Таймеры загружены: ${counters.value}")
            if (counters.value.isEmpty())
                _counters.value = _counters.value.addNew().also { saveCounters(it) }
            startService(_counters.value)
        }
    }

    fun saveCounters() = saveCounters(counters.value)

    fun saveCounters(counters: List<Counter>) {
        viewModelScope.launch {
            repo.deleteAll()
            Log.d(TAG, "Таймеры в репо удалены")
            repo.upsert(counters)
            Log.d(TAG, "Таймеры $counters сохранены")
        }
    }

    private fun updateCounters(counters: List<Counter>) {
        viewModelScope.launch {
            _counters.value = counters
            saveCounters(counters)
            Log.d(TAG, "Таймеры обновлены $counters")
        }
    }

    fun addCounter() {
        viewModelScope.launch {
            _counters.value = counters.value.addNew().also {
                updateCounters(it)
                Log.d(TAG, "Таймер добавлен ${it.last()}")
            }
        }
    }

    fun deleteCounter(id: Int) {
        viewModelScope.launch {
            _counters.value = counters.value.deleteById(id).also {
                repo.delete(id)
                Log.d(TAG, "Таймер удалён: ${counters.value}")
            }
        }
    }

    fun updateTitle(id: Int, title: String) {
        viewModelScope.launch {
            _counters.value = counters.value.map { counter ->
                if (counter.id == id)
                    counter.copy(title = title).also {
                        viewModelScope.launch(Dispatchers.IO) {
                            repo.upsert(it)
                            Log.d(TAG, "Таймер обновлен $it")
                        }

                    }
                else
                    counter
            }
        }
    }

    fun updateMaxProgress(id: Int, time: Int) {
        viewModelScope.launch {
            _counters.value = counters.value.map { counter ->
                if (counter.id == id)
                    counter.copy(maxProgress = time, currentProgress = time).also {
                        viewModelScope.launch(Dispatchers.IO) {
                            repo.upsert(it)
                            Log.d(TAG, "Таймер обновлен $it")
                        }

                    }
                else
                    counter
            }
        }
    }

    fun startCounter(id: Int) {
        viewModelScope.launch {
            _counters.value = counters.value.map { counter ->
                if (counter.id == id) {
                    if (counter.state == CounterState.PAUSED || counter.state == CounterState.FINISHED) {
                        if (counter.currentProgress == 0) {
                            counter.also {
                                viewModelScope.launch {
                                    Toast.makeText(this@MainViewModel.application, this@MainViewModel.application.getText(R.string.timer_need_set), Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            counter.copy(state = CounterState.RUN, startTime = Date().time).also {
                                viewModelScope.launch {
                                    repo.upsert(it)
                                    sendToService(it, ON_RUN_CLICK)
                                }
                            }
                        }
                    } else
                        counter
                } else
                    counter
            }
        }
    }

    fun pauseCounter(id: Int) {
        viewModelScope.launch {
            _counters.value = counters.value.map { counter ->
                if (counter.id == id && counter.state == CounterState.RUN)
                    counter.copy(state = CounterState.PAUSED).also {
                        viewModelScope.launch {
                            repo.upsert(it)
                            sendToService(it, ON_PAUSE_CLICK)
                        }
                    }
                else
                    counter
            }
        }
    }

    fun stopCounter(id: Int) {
        viewModelScope.launch {
            _counters.value = counters.value.map { counter ->
                if (counter.id == id && counter.state != CounterState.FINISHED)
                    counter.copy(state = CounterState.FINISHED, currentProgress = counter.maxProgress).also {
                        viewModelScope.launch {
                            repo.upsert(it)
                            sendToService(it, ON_STOP_CLICK)
                        }
                    }
                else
                    counter
            }
        }
    }

    fun timerTick(id: Int, progress: Int) {
        viewModelScope.launch {
            _counters.value = counters.value.map { counter ->
                if (counter.id == id)
                    counter.copy(currentProgress = progress)
                else
                    counter
            }
            Log.d("Counter", "viewModel viewModel=${this@MainViewModel} timerTick: ${counters.value}")
        }
    }

    fun timerFinish(id: Int) {
        viewModelScope.launch {
            _counters.value = counters.value.map { counter ->
                if (counter.id == id)
                    counter.copy(
                        state = CounterState.FINISHED,
                        currentProgress = counter.maxProgress,
                        startTime = 0,
                    )
                else
                    counter
            }
        }
    }

    fun timerAlarmed(id: Int) {
        viewModelScope.launch {
            _counters.value = counters.value.map { counter ->
                if (counter.id == id)
                    counter.copy(
                        state = CounterState.ALARMED,
                        currentProgress = 0,
                        startTime = 0,
                    ).also {
                        viewModelScope.launch {
                            repo.upsert(it)
                        }
                    }
                else
                    counter
            }
        }
    }

//    private fun deleteCounter(id: Int) {
//        viewModelScope.launch {
//            repo.delete(id)
//        }
//    }
//
//    private fun storeCounter(counter: Counter) {
//        viewModelScope.launch {
//            repo.upsert(counter)
//
//        }
//    }

    private fun startService(counters: List<Counter>) {
        val intent = Intent(this@MainViewModel.application, CountersService::class.java).apply {
            putExtra(EVENT, ON_START_SERVICE)
            putExtra(COUNTERS, Bundle().apply {
                putString(COUNTERS_BUNDLE, gson.toJson(counters))
            })
        }
        startService(intent)
    }

    private fun sendToService(counter: Counter, event: String) {
        if (event != ON_RUN_CLICK && event != ON_PAUSE_CLICK && event != ON_STOP_CLICK && event != ON_ALARM_TIMER)
            throw Exception(this@MainViewModel.application.resources?.getString(R.string.error_service_event))
        startService(Intent(this@MainViewModel.application, CountersService::class.java).apply {
            putExtra(EVENT, event)
            putExtra(COUNTER, counter)
        })
    }

    private fun startService(intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            ContextCompat.startForegroundService(this@MainViewModel.application, intent)
        else
            this.startService(intent)
    }

    fun getRunCounters(): Int =
        counters.value.count {
            it.state == CounterState.RUN
        }

    fun swapCounters(from: Int, to: Int){
        val fromCounter = counters.value.first { it.id == from }
        val toCounter = counters.value.first { it.id == to }
        _counters.value = counters.value.map{
            when (it.id){
                from -> toCounter
                to -> fromCounter
                else -> it
            }
        }
    }
}