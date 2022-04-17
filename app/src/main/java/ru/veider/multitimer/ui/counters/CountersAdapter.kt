package ru.veider.multitimer.ui.counters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator
import com.google.android.material.button.MaterialButton
import ru.veider.multitimer.R
import ru.veider.multitimer.data.Counter
import ru.veider.multitimer.const.CounterState
import ru.veider.multitimer.data.Counters
import ru.veider.multitimer.databinding.ItemCounterBinding
import ru.veider.multitimer.ui.timeselector.TimeSelector
import ru.veider.multitimer.ui.inputdialog.InputDialog


class CountersAdapter(
    private val fragment: CountersFragment,
    private var counters: Counters
) : RecyclerView.Adapter<CountersAdapter.CounterHolder>() {

    private val events: CountersAdapterEvents = fragment

    interface CountersAdapterEvents {
        fun onClickStartButton(id: Int)
        fun onClickPauseButton(id: Int)
        fun onClickStopButton(id: Int)
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
    inner class CounterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

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
                setOnClickListener { InputDialog.getInstance(counter).show((itemView.context as AppCompatActivity).supportFragmentManager, "TAG") }
            }
            progressIndicator = binder.progressIndicator.apply {
                setProgressTextAdapter { currentProgress: Double ->
                    var progress = currentProgress
                    val hours = (progress / 3600).toInt()
                    progress %= 3600.0
                    val minutes = (progress / 60).toInt()
                    val seconds = (progress % 60).toInt()
                    val sb = StringBuilder().apply {
                        append("${firstZero(hours)}:${firstZero(minutes)}:${firstZero(seconds)}")
                    }
                    sb.toString()
                }
                setOnClickListener {
                    TimeSelector.getInstance(counter).show(
                        (itemView.context as AppCompatActivity).supportFragmentManager,
                        "TAG"
                    )
                }
            }

            buttonStart = binder.buttonStart.apply {
                setOnClickListener {
                    events.onClickStartButton(counter.id)
                }
            }
            buttonPause = binder.buttonPause.apply {
                setOnClickListener {
                    events.onClickPauseButton(counter.id)
                }
            }
            buttonStop = binder.buttonStop.apply {
                setOnClickListener {
                    events.onClickStopButton(counter.id)
                }
            }
        }

        private fun firstZero(n: Int) = if (n in 0..9) "0$n" else "$n"

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
    }
}