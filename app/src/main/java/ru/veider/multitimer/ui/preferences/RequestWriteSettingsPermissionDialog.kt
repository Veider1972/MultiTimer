package ru.veider.multitimer.ui.preferences

import android.Manifest.permission
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import ru.veider.multitimer.R

/**
 * A dialog that explains the use of the location permission and requests the necessary
 * permission.
 */
class RequestWriteSettingsPermissionDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val requestCode = arguments?.getInt(ARGUMENT_PERMISSION_REQUEST_CODE) ?: 0
        return AlertDialog.Builder(activity)
            .setMessage(R.string.permission_write_settings_description)
            .setPositiveButton(R.string.button_text_accept) { _, _ -> // After click on Ok, request the permission.
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        permission.ACCESS_FINE_LOCATION,
                        permission.ACCESS_COARSE_LOCATION
                    ),
                    requestCode
                )
            }
            .setNegativeButton(R.string.button_text_cancel, null)
            .create()
    }

    companion object {
        const val ARGUMENT_PERMISSION_REQUEST_CODE = "requestCode"

        fun newInstance(
            requestCode: Int
        ): RequestWriteSettingsPermissionDialog {
            val arguments = Bundle().apply {
                putInt(ARGUMENT_PERMISSION_REQUEST_CODE, requestCode)
            }
            return RequestWriteSettingsPermissionDialog().apply {
                this.arguments = arguments
            }
        }
    }
}
