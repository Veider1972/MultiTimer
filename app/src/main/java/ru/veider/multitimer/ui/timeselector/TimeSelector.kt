package ru.veider.multitimer.ui.timeselector

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import ru.veider.multitimer.R
import ru.veider.multitimer.data.Counter
import ru.veider.multitimer.databinding.LayoutTimeSelectBinding
import ru.veider.multitimer.viewmodel.CountersViewModel
import ru.veider.multitimer.viewmodel.CountersViewModelFactory

class TimeSelector :
    DialogFragment() {
    private var firstHours = 0
    private var secondHours = 0
    private var firstMinutes = 0
    private var secondMinutes = 0
    private var firstSeconds = 0
    private var secondSeconds = 0
    private lateinit var viewModel: CountersViewModel
    private var _binder: LayoutTimeSelectBinding? = null
    private val binder get() = _binder!!

    companion object {
        private var instance: TimeSelector? = null
        private lateinit var counter: Counter
        fun getInstance(counter: Counter): TimeSelector {
            this.counter = counter
            return instance?.apply {} ?: TimeSelector().also { instance = it }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        viewModel = ViewModelProvider(this, CountersViewModelFactory.getInstance())[CountersViewModel::class.java]
        var currentProgress = counter.maxProgress
        val hours = (currentProgress / 3600)
        currentProgress %= 3600
        val minutes = (currentProgress / 60)
        val seconds = (currentProgress % 60)
        firstHours = (hours / 10)
        secondHours = hours - firstHours * 10
        firstMinutes = (minutes / 10)
        secondMinutes = minutes - firstMinutes * 10
        firstSeconds = (seconds / 10)
        secondSeconds = seconds - firstSeconds * 10
        _binder = LayoutTimeSelectBinding.inflate(layoutInflater).apply {
            firstHours.value = this@TimeSelector.firstHours
            secondHours.value = this@TimeSelector.secondHours
            firstMinutes.value = this@TimeSelector.firstMinutes
            secondMinutes.value = this@TimeSelector.secondMinutes
            firstSeconds.value = this@TimeSelector.firstSeconds
            secondSeconds.value = this@TimeSelector.secondSeconds
        }
        val dialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setNegativeButton(getString(R.string.button_text_cancel), null)
            setPositiveButton(getString(R.string.button_text_set)) { _, _ ->
                viewModel.updateMaxProgress(counter.id, getSeconds())
            }
            setTitle(getString(R.string.time_set_dialog_title))
            setView(binder.root)
        }
        return dialog.show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        counter.maxProgress = getSeconds()
        super.onSaveInstanceState(outState)
    }

    override fun onStart() {
        this.dialog?.setCanceledOnTouchOutside(false)
        super.onStart()
    }

    override fun onDestroy() {
        _binder = null
        super.onDestroy()
    }

    private fun getSeconds(): Int {
        return 3600 * (10 * binder.firstHours.value + binder.secondHours.value) +
                60 * (10 * binder.firstMinutes.value + binder.secondMinutes.value) +
                (10 * binder.firstSeconds.value + binder.secondSeconds.value)
    }
}