package ru.veider.multitimer.viewmodel

import android.provider.Settings
import androidx.lifecycle.ViewModel
import ru.veider.multitimer.app
import ru.veider.multitimer.domain.entity.Preferences

class PreferenceViewModel(
    private val preferences: Preferences
) : ViewModel() {

    fun saveKeepScreenOn(value: Boolean) {
        preferences.keepScreenOn.value = value
    }

    fun storeScreenSettings() {
        with(preferences){
            if (keepScreenOn.value && !isKept.value) {
                keptTime.value = Settings.System.getInt(app.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT)
                Settings.System.putInt( app.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, Int.MAX_VALUE )
                isKept.value = true
            }
        }

    }

    fun restoreScreenSettings() {
        with(preferences) {
            if (keepScreenOn.value && isKept.value) {
                Settings.System.putInt(app.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, keptTime.value)
                isKept.value = false
                keptTime.value = Int.MAX_VALUE
            }
        }
    }

    fun updateScreenSettings() {
        with(preferences) {
            if (keepScreenOn.value && isKept.value) {
                Settings.System.putInt(app.contentResolver,Settings.System.SCREEN_OFF_TIMEOUT,keptTime.value)
            }
        }
    }
}