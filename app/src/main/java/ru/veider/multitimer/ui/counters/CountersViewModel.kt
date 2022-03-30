package ru.veider.multitimer.ui.counters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.veider.multitimer.data.Counter
import ru.veider.multitimer.data.CounterState
import ru.veider.multitimer.data.Counters

class CountersViewModel : ViewModel() {

    private val countersLiveData: MutableLiveData<Counters> =
        MutableLiveData<Counters>(Counters().apply {
            add(Counter(0, 0, 30, 0, 0, CounterState.FINISHED, "Таймер по умолчанию"))
        })

    fun getCounters() = countersLiveData

//    private val _text = MutableLiveData<String>().apply {
//        value = "This is home Fragment"
//    }
//    val text: LiveData<String> = _text
}