package ru.veider.multitimer.data.preferences

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import ru.veider.multitimer.R
import ru.veider.multitimer.core.utils.stateFlow
import ru.veider.multitimer.domain.entity.Preferences
import ru.veider.multitimer.domain.entity.Sound

class PreferencesImpl(
    context: Context
): Preferences, BasePreferences(context){

    val gson = Gson()

    override val keepScreenOn = stateFlow(
        initialValue = getBool("keepScreenOn", false),
        onValueChange = { putBool("keepScreenOn", it) }
    )
    override val unlimitedNotification = stateFlow(
        initialValue = getBool("unlimitedCounter", true),
        onValueChange = { putBool("unlimitedCounter", it)}
    )
    override val notificationLimits = stateFlow(
        initialValue = getInt("counterLimits", 20),
        onValueChange = {putInt("counterLimits", it)}
    )
    override val keptTime = stateFlow(
        initialValue = getInt("keptTime", Int.MAX_VALUE),
        onValueChange = {putInt("keptTime", it)}
    )
    override val isKept = stateFlow(
        initialValue = getBool("isKept", false),
        onValueChange = { putBool("isKept", it)}
    )
    override val sound = stateFlow(
        initialValue = getString("sound")?.let { gson.fromJson(it, Sound::class.java) }
            ?: Sound(context.resources.getString(R.string.no_sound), Uri.EMPTY.toString()),
        onValueChange = {
            putString("sound", gson.toJson(it))
        }
    )
    override val alarmChannelId = stateFlow(
        initialValue = getString("alarmChannelId") ?: "ALARM_CHANNEL_ID",
        onValueChange = {
            putString("alarmChannelId", value = it)
        }
    )
    override val alarmChannelNum = stateFlow(
        initialValue = getInt("alarmChannelNum", 0),
        onValueChange = {
            putInt("alarmChannelNum", value = it)
        }
    )
    override val simpleChannelId = stateFlow(
        initialValue = getString("simpleChannelId") ?: "SIMPLE_CHANNEL_ID",
        onValueChange = {
            putString("simpleChannelId", value = it)
        }
    )

    override val simpleChannelNum = stateFlow(
        initialValue = getInt("simpleChannelNum", 1),
        onValueChange = {
            putInt("simpleChannelNum", value = it)
        }
    )

    override val timeEditorIsMulti = stateFlow(
        initialValue = getBool("timeEditorIsMulti", false),
        onValueChange = {
            putBool("timeEditorIsMulti", value = it)
        }
    )
}