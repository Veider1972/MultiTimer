package ru.veider.multitimer.timeselector

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.content.DialogInterface
import androidx.fragment.app.DialogFragment
import ru.veider.multitimer.R
import ru.veider.multitimer.databinding.LayoutTimeSelectBinding

class TimeSelector(private var onTimeSet: OnTimeSet, private var position: Int, progress: Int) :
    DialogFragment() {
    private var firstHours = 0
    private var secondHours = 0
    private var firstMinutes = 0
    private var secondMinutes = 0
    private var firstSeconds = 0
    private var secondSeconds = 0

    interface OnTimeSet {
        fun TimeIsSelected(position: Int, seconds: Int)
    }

    init {
        var currentProgress = progress
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
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binder = LayoutTimeSelectBinding.inflate(layoutInflater).apply {
            firstHours.value = this@TimeSelector.firstHours
            secondHours.value = this@TimeSelector.secondHours
            firstMinutes.value = this@TimeSelector.firstMinutes
            secondMinutes.value = this@TimeSelector.secondMinutes
            firstSeconds.value = this@TimeSelector.firstSeconds
            secondSeconds.value = this@TimeSelector.secondSeconds
        }
        val dialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setNegativeButton(getString(R.string.button_text_cancel)) { dialogInterface: DialogInterface?, which: Int -> }
            setPositiveButton(getString(R.string.button_text_set)) { dialogInterface: DialogInterface?, which: Int ->
                with(binder) {
                    val seconds = 3600 * (10 * firstHours.value + secondHours.value) +
                            60 * (10 * firstMinutes.value + secondMinutes.value) +
                            (10 * firstSeconds.value + secondSeconds.value)
                    onTimeSet.TimeIsSelected(position, seconds)
                }
            }
            setTitle(getString(R.string.time_set_dialog_title))
            setView(binder.root)
        }
        return dialog.show()
    }

    override fun onStart() {
        this.dialog?.setCanceledOnTouchOutside(false)
        super.onStart()
    }


}