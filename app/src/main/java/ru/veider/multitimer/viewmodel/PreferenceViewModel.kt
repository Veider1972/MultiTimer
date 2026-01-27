package ru.veider.multitimer.viewmodel

import android.app.Application
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.application
import ru.veider.multitimer.domain.entity.Preferences

class PreferenceViewModel(
    app: Application,
    private val preferences: Preferences
) : AndroidViewModel(app) {

    fun saveKeepScreenOn(value: Boolean) {
        preferences.keepScreenOn.value = value
    }

    fun storeScreenSettings() {
        with(preferences){
            if (keepScreenOn.value && !isKept.value) {
                keptTime.value = Settings.System.getInt(this@PreferenceViewModel.application.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT)
                Settings.System.putInt( this@PreferenceViewModel.application.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, Int.MAX_VALUE )
                isKept.value = true
            }
        }

    }

    fun restoreScreenSettings() {
        with(preferences) {
            if (keepScreenOn.value && isKept.value) {
                Settings.System.putInt(this@PreferenceViewModel.application.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, keptTime.value)
                isKept.value = false
                keptTime.value = Int.MAX_VALUE
            }
        }
    }

    fun updateScreenSettings() {
        with(preferences) {
            if (keepScreenOn.value && isKept.value) {
                Settings.System.putInt(this@PreferenceViewModel.application.contentResolver,Settings.System.SCREEN_OFF_TIMEOUT,keptTime.value)
            }
        }
    }
}