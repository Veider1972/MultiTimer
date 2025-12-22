package ru.veider.multitimer.data.preferences

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import ru.veider.multitimer.core.utils.stateFlow
import ru.veider.multitimer.domain.entity.Preferences
import ru.veider.multitimer.domain.entity.Sound
import java.util.UUID

class PreferencesImpl(
    context: Context
): Preferences, BasePreferences(context){

    val gson = Gson()

    override var keepScreenOn = stateFlow(
        initialValue = getBool("keepScreenOn", false),
        onValueChange = { putBool("keepScreenOn", it) }
    )
    override var unlimitedNotification = stateFlow(
        initialValue = getBool("unlimitedCounter", true),
        onValueChange = { putBool("unlimitedCounter", it)}
    )
    override var notificationLimits = stateFlow(
        initialValue = getInt("counterLimits", 20),
        onValueChange = {putInt("counterLimits", it)}
    )
    override var keptTime = stateFlow(
        initialValue = getInt("keptTime", Int.MAX_VALUE),
        onValueChange = {putInt("keptTime", it)}
    )
    override var isKept = stateFlow(
        initialValue = getBool("isKept", false),
        onValueChange = { putBool("isKept", it)}
    )

    override var sound = stateFlow(
        initialValue = getString("sound")?.let { gson.fromJson(it, Sound::class.java) } ?: Sound("Не задано", Uri.EMPTY.toString()),
        onValueChange = {
            putString("sound", gson.toJson(it))
        }
    )

    override var alarmChannelId = stateFlow(
        initialValue = getString("alarmChannelId") ?: "ALARM_CHANNEL_ID",
        onValueChange = {
            putString("alarmChannelId", value = it)
        }
    )

    override var alarmChannelNum = stateFlow(
        initialValue = getInt("alarmChannelNum", 0),
        onValueChange = {
            putInt("alarmChannelNum", value = it)
        }
    )

    override var simpleChannelId = stateFlow(
        initialValue = getString("simpleChannelId") ?: "SIMPLE_CHANNEL_ID",
        onValueChange = {
            putString("simpleChannelId", value = it)
        }
    )

    override var simpleChannelNum = stateFlow(
        initialValue = getInt("simpleChannelNum", 1),
        onValueChange = {
            putInt("simpleChannelNum", value = it)
        }
    )
}