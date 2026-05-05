package ru.veider.multitimer.data.preferences

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.veider.multitimer.R
import ru.veider.multitimer.core.utils.stateFlow
import ru.veider.multitimer.domain.entity.Preferences
import ru.veider.multitimer.domain.entity.Sound

class PreferencesImpl(
    context: Context
): Preferences, BasePreferences(context){

    val gson = Gson()

    private val scope: CoroutineScope by lazy { CoroutineScope(SupervisorJob() + Dispatchers.IO) }
    private val mutex = Mutex()
    private fun CoroutineScope.withMutex(context: () -> Unit) {
        this.launch {
            mutex.withLock {
                context()
            }
        }
    }

    override val keepScreenOn = stateFlow(
        initialValue = getBool("keepScreenOn", false),
        onValueChange = {
            scope.withMutex {
                putBool("keepScreenOn", it)
            }
        }
    )
    override val unlimitedNotification = stateFlow(
        initialValue = getBool("unlimitedCounter", true),
        onValueChange = {
            scope.withMutex {
                putBool("unlimitedCounter", it)
            }
        }
    )
    override val notificationLimits = stateFlow(
        initialValue = getInt("counterLimits", 20),
        onValueChange = {
            scope.withMutex {
                putInt("counterLimits", it)
            }
        }
    )
    override val keptTime = stateFlow(
        initialValue = getInt("keptTime", Int.MAX_VALUE),
        onValueChange = {
            scope.withMutex {
                putInt("keptTime", it)
            }
        }
    )
    override val isKept = stateFlow(
        initialValue = getBool("isKept", false),
        onValueChange = {
            scope.withMutex {
                putBool("isKept", it)
            }
        }
    )
    override val sound = stateFlow(
        initialValue = getString("sound")?.let { gson.fromJson(it, Sound::class.java) }
            ?: Sound(context.resources.getString(R.string.no_sound), Uri.EMPTY.toString()),
        onValueChange = {
            scope.withMutex {
                putString("sound", gson.toJson(it))
            }
        }
    )
    override val alternativeSoundOut = stateFlow(
        initialValue = getBool("alternativeSoundOut", false),
        onValueChange = {
            scope.withMutex {
                putBool("alternativeSoundOut", it)
            }
        }
    )
    override val notificationInterval = stateFlow(
        initialValue = getInt("interval", 5),
        onValueChange = {
            scope.withMutex {
                putInt("interval", value = it)
            }
        }
    )
    override val alarmChannelId = stateFlow(
        initialValue = getString("alarmChannelId") ?: "ALARM_CHANNEL_ID",
        onValueChange = {
            scope.withMutex {
                putString("alarmChannelId", value = it)
            }
        }
    )
    override val alarmChannelNum = stateFlow(
        initialValue = getInt("alarmChannelNum", 0),
        onValueChange = {
            scope.withMutex {
                putInt("alarmChannelNum", value = it)
            }
        }
    )
    override val simpleChannelId = stateFlow(
        initialValue = getString("simpleChannelId") ?: "SIMPLE_CHANNEL_ID",
        onValueChange = {
            scope.withMutex {
                putString("simpleChannelId", value = it)
            }
        }
    )

    override val simpleChannelNum = stateFlow(
        initialValue = getInt("simpleChannelNum", 1),
        onValueChange = {
            scope.withMutex {
                putInt("simpleChannelNum", value = it)
            }
        }
    )

    override val timeEditorIsMulti = stateFlow(
        initialValue = getBool("timeEditorIsMulti", false),
        onValueChange = {
            scope.withMutex {
                putBool("timeEditorIsMulti", value = it)
            }
        }
    )

    override val runCounter = stateFlow(
        initialValue = getLong("bootUpCounter", 0),
        onValueChange = {
            scope.withMutex {
                putLong("bootUpCounter", value = it)
            }
        }
    )

    override val hasFeedback = stateFlow(
        initialValue = getBool("reviewCounter", false),
        onValueChange = {
            scope.withMutex {
                putBool("reviewCounter", value = it)
            }
        }
    )
}