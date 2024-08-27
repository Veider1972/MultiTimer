package ru.veider.multitimer.ui.preferences

import android.Manifest.permission.WRITE_SETTINGS
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.veider.multitimer.R
import ru.veider.multitimer.databinding.FragmentPreferenceBinding
import ru.veider.multitimer.viewmodel.MainViewModelFactory
import ru.veider.multitimer.viewmodel.PreferenceViewModel


class PreferencesFragment : Fragment() {

    private lateinit var viewModel: PreferenceViewModel

    private lateinit var binding: FragmentPreferenceBinding


    private var activityResultLauncher: ActivityResultLauncher<Array<String>> = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.containsKey(WRITE_SETTINGS)) {
            if (result[WRITE_SETTINGS] == true) {
                viewModel.saveKeepScreenOn(true)    //                ActivityCompat.requestPermissions( requireActivity(), arrayOf(WRITE_SETTINGS), WRITE_SETTINGS_GRANT_PERMISSION)
            }
        }

        //            var allAreGranted = true
        //            for(b in result.values) {
        //                allAreGranted = allAreGranted && b
        //            }
        //
        //            if(allAreGranted) {
        //                //capturePhoto()
        //            }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPreferenceBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, MainViewModelFactory.getInstance())[PreferenceViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, resources.getStringArray(R.array.repeatsList))
        viewModel.preferencesData.observe(viewLifecycleOwner) {
            with(it) {
                binding.keepScreenOn.isChecked = keepScreenOn
                binding.unlimitedVoiceNotification.isChecked = unlimitedCounter
                binding.unlimitedVoiceNotificationCounterLayout.isVisible = !unlimitedCounter
                binding.repeatsNumber.adapter = adapter
                binding.repeatsNumber.setSelection(counterLimits - 1)
            }
        }
        with(binding) {
            keepScreenOn.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    if (Settings.System.canWrite(requireContext())) {
                        viewModel.saveKeepScreenOn(true)
                    } else {
                        viewModel.saveKeepScreenOn(false)
                        showPermissionDialog(
                            R.string.permission_write_settings_description,
                            Settings.ACTION_MANAGE_WRITE_SETTINGS
                        )
                    }
                } else {
                    viewModel.saveKeepScreenOn(false)
                }
            }
            unlimitedVoiceNotification.setOnCheckedChangeListener { _, isChecked ->
                viewModel.storeNotificationCounter(isChecked, binding.repeatsNumber.selectedItemPosition+1)
            }
            repeatsNumber.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    viewModel.storeNotificationCounter(binding.unlimitedVoiceNotification.isChecked, position+1)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }
            // repeatsNumber.setOnItemClickListener() addTextChangedListener (
            //         object : TextWatcher {
            //             override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            //
            //             override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            //
            //             override fun afterTextChanged(s: Editable?) {
            //                 val value = if (s.toString().isEmpty()) 0 else s.toString().toInt()
            //                 viewModel.storeNotificationCounter(binding.unlimitedVoiceNotification.isChecked, value)
            //             }
            //         }
            //         )
        }
    }

    private fun showPermissionDialog(
        descRes: Int,
        action: String
    ) {
        AlertDialog.Builder(requireContext()).apply {
            create()
            setMessage(descRes)
            setPositiveButton(R.string.button_text_accept) { _, _ ->
                runManageWriteSettingsActivity(action)
            }
            setNegativeButton(R.string.button_text_cancel) { _, _ ->
            }
            show()
        }
    }

    private fun runManageWriteSettingsActivity(action: String) {
        startActivity(Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP
        })
    }


}

