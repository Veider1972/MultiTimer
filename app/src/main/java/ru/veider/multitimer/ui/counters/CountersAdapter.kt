package ru.veider.multitimer.ui.counters

import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.veider.multitimer.R
import ru.veider.multitimer.data.Counter
import ru.veider.multitimer.data.CounterState
import ru.veider.multitimer.data.Counters
import ru.veider.multitimer.databinding.ItemCounterBinding
import ru.veider.multitimer.databinding.LayoutQueryBinding
import ru.veider.multitimer.timeselector.TimeSelector


class CountersAdapter(
    val fragment: CountersFragment,
    private var counters: Counters
) : RecyclerView.Adapter<CountersAdapter.CounterHolder>() {

    private val events: CountersAdapterEvents = fragment

    interface CountersAdapterEvents {
        fun onTimerStart(id: Int)
        fun onTimerPause(id: Int)
        fun onTimerStop(id: Int)
        fun onTimerTitleChange(id: Int, title: String)
        fun onTimerSetValue(id: Int, seconds: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CounterHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_counter, parent, false)
        return CounterHolder(view)
    }

    override fun onBindViewHolder(holder: CounterHolder, position: Int) {
        holder.onBind(counters[position])
    }

    override fun onBindViewHolder(holder: CounterHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            counters[position].apply {
                if (!holder.getTitle.text.equals(this.title))
                    holder.getTitle.text = this.title
                holder.getProgressIndicator.also {
                    if (it.progress.toInt() != this.currentProgress)
                        it.setProgress(this.currentProgress.toDouble(), this.maxProgress.toDouble())
                    setProgressIndicatorBackgroundColor(it, this)
                }
            }

        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    private fun setProgressIndicatorBackgroundColor(progressIndicator: CircularProgressIndicator, counter: Counter) =
            if (counter.state == CounterState.ALARMED)
                progressIndicator.progressBackgroundColor = ContextCompat.getColor(fragment.requireContext(), R.color.timer_alarm_color)
            else
                progressIndicator.progressBackgroundColor = ContextCompat.getColor(fragment.requireContext(), R.color.timer_simple_color)


    override fun getItemCount() = counters.size

    fun swapItems(fromPosition: Int, toPosition: Int) {
        counters.swap(fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    //========== CounterHolder====================================================
    inner class CounterHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        TimeSelector.TimeSelectorEvent {

        private var title: TextView
        val getTitle get() = title
        private var progressIndicator: CircularProgressIndicator
        val getProgressIndicator get() = progressIndicator
        private var buttonStart: MaterialButton
        private var buttonPause: MaterialButton
        private var buttonStop: MaterialButton
        lateinit var counter: Counter
        private var binder = ItemCounterBinding.bind(itemView)

        init {
            title = binder.title.apply {
                setOnClickListener {
                    val inflater = LayoutInflater.from(this.context)
                    val binder = LayoutQueryBinding.inflate(inflater)
                    MaterialAlertDialogBuilder(this.context).apply {
                        setView(binder.root)
                        if (text.isNotEmpty())
                            binder.inputText.setText(counter.title)
                        setPositiveButton(
                            resources.getString(R.string.button_text_accept),
                            DialogInterface.OnClickListener { _, _ ->
                                if (binder.inputText.text.toString().isNotEmpty()) {
                                    counter.apply {
                                        title = binder.inputText.text.toString()
                                        events.onTimerTitleChange(counter.id, title)
                                    }
                                }
                            })
                        setNegativeButton(
                            resources.getString(R.string.button_text_cancel),
                            DialogInterface.OnClickListener { _, _ ->
                            })
                    }.show()
                }
            }
            progressIndicator = binder.progressIndicator.apply {
                setProgressTextAdapter { currentProgress: Double ->
                    var progress = currentProgress
                    val hours = (progress / 3600).toInt()
                    progress %= 3600.0
                    val minutes = (progress / 60).toInt()
                    val seconds = (progress % 60).toInt()
                    val sb = StringBuilder()
                    if (hours < 10) {
                        sb.append(0)
                    }
                    sb.append(hours).append(":")
                    if (minutes < 10) {
                        sb.append(0)
                    }
                    sb.append(minutes).append(":")
                    if (seconds < 10) {
                        sb.append(0)
                    }
                    sb.append(seconds)
                    sb.toString()
                }
                setOnClickListener {
                    TimeSelector(
                        this@CounterHolder,
                        counter
                    ).showNow(
                        (itemView.context as AppCompatActivity).supportFragmentManager,
                        "TAG"
                    )
                }
            }

            buttonStart = binder.buttonStart.apply {
                setOnClickListener {
                    if (counter.state != CounterState.RUN) {
                        events.onTimerStart(counter.id)
                    }
                }
            }
            buttonPause = binder.buttonPause.apply {
                setOnClickListener {
                    if (counter.state == CounterState.RUN)
                        events.onTimerPause(counter.id)
                }
            }
            buttonStop = binder.buttonStop.apply {
                setOnClickListener {
                    if (counter.state == CounterState.RUN || counter.state == CounterState.ALARMED)
                        events.onTimerStop(counter.id)
                }
            }
        }

        fun onBind(counter: Counter) {
            this.counter = counter
            binder.apply {
                title.text = counter.title
                progressIndicator.apply {
                    setProgress(
                        counter.currentProgress.toDouble(),
                        counter.maxProgress.toDouble()
                    )
                    setProgressIndicatorBackgroundColor(this, counter)
                }
            }
        }

        override fun onTimeSelected(id: Int, maxProgress: Int) {
            events.onTimerSetValue(id, maxProgress)
        }
    }
}