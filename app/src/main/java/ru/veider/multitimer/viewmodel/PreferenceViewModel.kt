package ru.veider.multitimer.viewmodel

import android.content.SharedPreferences
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import ru.veider.multitimer.R
import ru.veider.multitimer.app
import ru.veider.multitimer.data.Preferences

class PreferenceViewModel : ViewModel() {

    private var preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(app.applicationContext)
    
    private val _preferencesData = MutableLiveData<Preferences>()
    val preferencesData: LiveData<Preferences> get()= _preferencesData

    var screen = KeepScreenOnData()

    data class KeepScreenOnData(
        var keepScreenOn: Boolean = false,
        var isKept: Boolean = false,
        var keptTime: Int = Int.MAX_VALUE,
    )

    init {
        with (screen){
            keepScreenOn = preferences.getBoolean(app.getString(R.string.keepScreenOn), false)
            isKept = preferences.getBoolean(app.getString(R.string.isKept), false)
            keptTime = preferences.getInt(app.getString(R.string.keptTime), Int.MAX_VALUE)
            _preferencesData.postValue(
                Preferences(
                    keepScreenOn,
                    preferences.getBoolean(app.getString(R.string.unlimitedCounter), true),
                    preferences.getInt(app.getString(R.string.counterLimits), 20)
                ))
        }
        

    }

    fun saveKeepScreenOn(value:Boolean){
        preferences.edit().putBoolean(app.getString(R.string.keepScreenOn),value).apply()
        _preferencesData.postValue(Preferences(value))
    }

    fun storeScreenSettings() {
        with(screen) {
            if (keepScreenOn && !isKept) {
                Log.d(">>>>>", "storeScreenSettings:  $screen")
                keptTime = Settings.System.getInt(app.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT)
                Settings.System.putInt(app.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, Int.MAX_VALUE)
                isKept = true
                storeKeepScreenOn()
            }
        }
    }

    fun restoreScreenSettings() {
        with(screen) {
            if (keepScreenOn && isKept) {
                Log.d(">>>>>", "restoreScreenSettings:  $screen")
                Settings.System.putInt(app.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, keptTime)
                isKept = false
                keptTime = Int.MAX_VALUE
                storeKeepScreenOn()
            }
        }
    }

    fun updateScreenSettings() {
        with(screen) {
            if (keepScreenOn && isKept) {
                Log.d(">>>>>", "updateScreenSettings: $screen")
                Settings.System.putInt(app.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, keptTime)
                storeKeepScreenOn()
            }
        }
    }
    
    fun storeKeepScreenOn(){
        preferences.edit()
            .putBoolean(app.getString(R.string.keepScreenOn),screen.keepScreenOn)
            .putBoolean(app.getString(R.string.isKept),screen.isKept)
            .putInt(app.getString(R.string.keptTime),screen.keptTime)
            .apply()
    }

    fun storeNotificationCounter(unlimited: Boolean, repeats: Int){
        if (unlimited != preferencesData.value?.unlimitedCounter || repeats != preferencesData.value?.counterLimits) {
            preferences.edit()
                .putBoolean(app.getString(R.string.unlimitedCounter), unlimited)
                .putInt(app.getString(R.string.counterLimits), repeats)
                .apply()
            _preferencesData.postValue(
                _preferencesData.value?.copy(
                    unlimitedCounter = unlimited,
                    counterLimits = repeats
                )
            )
        }
    }

    companion object {
        private var instance: PreferenceViewModel? = null
        fun getInstance() = instance?.apply {} ?: PreferenceViewModel().also { instance = it }
    }
}