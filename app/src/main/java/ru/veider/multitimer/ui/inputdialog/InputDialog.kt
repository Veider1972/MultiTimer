package ru.veider.multitimer.ui.inputdialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.veider.multitimer.R
import ru.veider.multitimer.data.Counter
import ru.veider.multitimer.databinding.LayoutQueryBinding
import ru.veider.multitimer.viewmodel.CountersViewModel
import ru.veider.multitimer.viewmodel.CountersViewModelFactory

class InputDialog : DialogFragment() {

    private lateinit var viewModel: CountersViewModel
    private var _binder: LayoutQueryBinding? = null
    private val binder get() = _binder!!

    companion object {
        private var instance: InputDialog? = null
        private lateinit var title: String
        private var counterId: Int = -1
        fun getInstance(counter: Counter): InputDialog {
            title = counter.title
            counterId = counter.id
            return instance?.apply {} ?: InputDialog().also { instance = it }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        viewModel = ViewModelProvider(this, CountersViewModelFactory.getInstance())[CountersViewModel::class.java]
        _binder = LayoutQueryBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setView(binder.root)
            if (title.isNotEmpty())
                binder.inputText.setText(title)
            setPositiveButton(
                resources.getString(R.string.button_text_accept)
            ) { _, _ ->
                if (binder.inputText.text.toString().isNotEmpty()) {
                    title = binder.inputText.text.toString()
                    viewModel.updateTitle(counterId, title)
                }
            }
            setNegativeButton(
                resources.getString(R.string.button_text_cancel), null
            )
        }
        return dialog.show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        title = binder.inputText.text.toString()
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        _binder = null
        super.onDestroy()
    }
}