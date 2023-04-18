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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
		viewModel.preferencesData.observe(viewLifecycleOwner) {
			with(it) {
				binding.keepScreenOn.isChecked = this.keepScreenOn
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
							Settings.ACTION_MANAGE_WRITE_SETTINGS)
					}
				} else {
					viewModel.saveKeepScreenOn(false)
				}
			}
		}
	}
	
	private fun showPermissionDialog(
		descRes: Int,
		action:String
	){
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
	
	private fun runManageWriteSettingsActivity(action:String) {
		startActivity(Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
			flags = Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP
		})
	}
	
	
}

