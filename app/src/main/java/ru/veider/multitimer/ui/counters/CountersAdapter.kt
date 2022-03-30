package ru.veider.multitimer.ui.counters

import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.veider.multitimer.R
import ru.veider.multitimer.data.*
import ru.veider.multitimer.databinding.ItemCounterBinding
import java.util.*


class CountersAdapter(private val fragment: CountersFragment, private val viewModel: CountersViewModel) :
    ArrayAdapter<Counter>(
        fragment.requireContext(), R.layout.item_counter, viewModel.getCounters().value as Counters
    ) {

//    private val timerAdapterEvents: TimerAdapterEvents = fragment
    private var counters = viewModel.getCounters().value as Counters


    private lateinit var holder: CounterHolder

    init {
        val observer = Observer<Counters> { counters -> isCountersChanged(counters) }
        viewModel.getCounters().observe(fragment.viewLifecycleOwner, observer)
    }

    private fun isCountersChanged(counters: Counters) {
        this.counters = counters
        notifyDataSetChanged()
    }

//    interface TimerAdapterEvents {
//        fun onTimerStart(id: Int)
//        fun onTimerPause(id: Int)
//        fun onTimerStop(id: Int)
//        //fun OnTimerSetValue(id: Int, seconds: Int)
//    }


    inner class CounterHolder {
        lateinit var progressIndicator: CircularProgressIndicator
        lateinit var buttonStart: MaterialButton
        lateinit var buttonPause: MaterialButton
        lateinit var buttonStop: MaterialButton
        lateinit var titleTextView: TextView
        lateinit var counter: Counter

    }

    override fun getView(position: Int, counterView: View?, parent: ViewGroup): View {
        var rowView = counterView
        if (rowView == null) {
            val layoutInflater = fragment.layoutInflater
            val binder = ItemCounterBinding.inflate(layoutInflater,parent,false)
            rowView = binder.root
            holder = CounterHolder().apply {
                counter = counters[position]
                progressIndicator = binder.progressIndicator
                titleTextView = binder.title
                buttonStart = binder.buttonStart
                buttonPause = binder.buttonPause
                buttonStop = binder.buttonStop
            }
            rowView.setTag(holder)
        } else {
            holder = rowView.tag as CounterHolder
        }
        holder.progressIndicator.apply {
            setProgressTextAdapter { currentProgress: Double ->
                var progress = currentProgress
                val hours = (progress / 3600).toInt()
                progress %= 3600.0
                val minutes = (progress / 60).toInt()
                val seconds = (progress % 60).toInt()
                val sb = StringBuilder().apply {
                    if (hours < 10) {
                        append(0)
                    }
                    append(hours).append(":")
                    if (minutes < 10) {
                        append(0)
                    }
                    append(minutes).append(":")
                    if (seconds < 10) {
                        append(0)
                    }
                    append(seconds)
                }
                sb.toString()
            }
//            setOnClickListener {
//                TimeSelector(
//                    this@CountersAdapter,
//                    position,
//                    counters[position].currentProgress
//                ).showNow(
//                    (rowView.context as AppCompatActivity).supportFragmentManager,
//                    "TAG"
//                )
//            }
            maxProgress = counters[position].maxProgress.toDouble()
            setCurrentProgress(counters[position].currentProgress.toDouble())
        }
//        holder.titleTextView.apply {
//            setOnClickListener {
//                val inflater = LayoutInflater.from(holder.titleTextView.context)
//                val binder = QueryLayoutBinding.inflate(inflater)
//                MaterialAlertDialogBuilder(holder.titleTextView.context).apply {
//                    setView(binder.root)
//                    if (holder.titleTextView.text.isNotEmpty())
//                        binder.inputText.setText(counters[position].title)
//                    setPositiveButton(
//                        R.string.button_text_accept.toRString(),
//                        DialogInterface.OnClickListener { dialogInterface, i ->
//                            if (holder.titleTextView.text.isNotEmpty()) {
//                                counters[position].title = binder.inputText.text.toString()
//                                viewModel.updateCounter(counters[position])
//                            }
//                        })
//                    setNegativeButton(
//                        R.string.button_text_cancel.toRString(),
//                        DialogInterface.OnClickListener { dialogInterface, i ->
//
//                        })
//                }.show()
//            }
//            text = counters[position].title
//        }
        holder.buttonStart.setOnClickListener {
            val counter = counters[position].apply {
                timeOfStart = Date().time
                pausedAt = timeOfStart
                state = CounterState.RUNNED
            }
//            viewModel.updateCounter(counter)
            //timerAdapterEvents.onTimerStart(counters[position].id)
        }
//        holder.buttonPause.setOnClickListener {
//            val counter = counters[position].apply {
//                pausedAt = Date().time
//                state = CounterState.PAUSED
//            }
//            viewModel.updateCounter(counter)
//            //timerAdapterEvents.onTimerPause(counters[position].id)
//        }
//        holder.buttonStop.setOnClickListener {
//            val counter = counters[position].apply {
//                state = CounterState.FINISHED
//                timeOfStart = 0
//                currentProgress = maxProgress
//                pausedAt = 0
//            }
//            viewModel.updateCounter(counter)
//            //timerAdapterEvents.onTimerStop(counters[position].id)
//        }
        return rowView
    }


    fun updateCounter() {
        this.notifyDataSetChanged()
    }

//    override fun TimeIsSelected(position: Int, seconds: Int) {
//        with(counters[position]) {
//            maxProgress = seconds
//            currentProgress = seconds
//        }
//
//        viewModel.updateCounters(counters)
//        this.notifyDataSetChanged()
//    }

    private fun Int.toRString(): String {
        return fragment.getString(this)
    }


}