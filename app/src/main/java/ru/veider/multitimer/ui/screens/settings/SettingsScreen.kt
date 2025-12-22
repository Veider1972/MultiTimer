package ru.veider.multitimer.ui.screens.settings

import android.R.attr.checked
import android.R.id.message
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import org.koin.compose.koinInject
import android.provider.Settings
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import ru.veider.multitimer.R
import ru.veider.multitimer.const.ALARM_CHANNEL_ID
import ru.veider.multitimer.const.emptySound
import ru.veider.multitimer.core.utils.getAndroidMedia
import ru.veider.multitimer.domain.entity.Preferences
import ru.veider.multitimer.domain.entity.Sound
import ru.veider.multitimer.ui.assets.dialogs.NumberEditor
import ru.veider.multitimer.ui.assets.dialogs.SoundSelector
import ru.veider.multitimer.ui.theme.colorPrimary
import ru.veider.multitimer.ui.theme.textStyle_14_400
import ru.veider.multitimer.ui.theme.textStyle_14_500
import ru.veider.multitimer.ui.theme.textStyle_14_700
import ru.veider.multitimer.ui.theme.textStyle_18_400
import ru.veider.multitimer.ui.theme.textStyle_18_500
import ru.veider.multitimer.ui.theme.textStyle_18_700
import ru.veider.multitimer.utils.createAlarmNotificationChannel
import ru.veider.multitimer.utils.deleteChannel
import java.util.UUID

@Composable
fun SettingsScreen() {

    val context = LocalContext.current
    val density = LocalDensity.current
    val prefs: Preferences? = if (LocalInspectionMode.current) null else koinInject()
    val keepScreenOn = prefs?.keepScreenOn?.collectAsState()?.value ?: true
    val unlimitedNotification = prefs?.unlimitedNotification?.collectAsState()?.value ?: true
    val notificationLimits = prefs?.notificationLimits?.collectAsState()?.value ?: 20
    val sound = prefs?.sound?.collectAsState()?.value ?: emptySound()
//    val sound by rememberUpdatedState(
//        NotificationManagerCompat.from(context).let {
//            val allSounds = getAndroidMedia(context)
//            val currentUri = it.getNotificationChannel(ALARM_CHANNEL_ID)?.sound
//            allSounds.firstOrNull() { it.uri == currentUri?.path } ?: emptySound()
//        }
//    )
    var width by remember { mutableStateOf(0.dp) }

    var notificationLimitsDialogShow by remember { mutableStateOf(false) }
    if (notificationLimitsDialogShow)
        NumberEditor(
            title = stringResource(R.string.preferences_repeat_nums),
            value = notificationLimits,
            onAccept = {
                prefs?.notificationLimits?.value = it
                notificationLimitsDialogShow = false
            },
            onCancel = {
                notificationLimitsDialogShow = false
            }
        )

    var soundSelectorDialogShow by remember { mutableStateOf(false) }
    if (soundSelectorDialogShow)
        SoundSelector(
            onAccept = { sound ->
                prefs?.sound?.value = sound
                prefs?.alarmChannelId?.value?.let { oldChannel ->
                    context.deleteChannel(oldChannel)
                    val newChannelId = UUID.randomUUID().toString()
                    prefs.alarmChannelId.value = newChannelId
                    context.createAlarmNotificationChannel(
                        uri = sound.uri.toUri(),
                        channelId = newChannelId
                    )
                }
                soundSelectorDialogShow = false
            },
            onCancel = { soundSelectorDialogShow = false }
        )

    Column {
        Text(
            text = stringResource(R.string.preferences_title),
            style = textStyle_18_700,
            modifier = Modifier.padding(start = 6.dp, top = 6.dp, end = 6.dp)
        )
        HorizontalDivider(thickness = 1.dp, color = colorPrimary)
        CheckedSettings(
            message = stringResource(R.string.preferences_keep_screen_on),
            checked = keepScreenOn,
            onCheckedChange = { prefs?.keepScreenOn?.value = it },
            width = width,
            onWidthChange = { width = max(width, it) }
        )
        CheckedSettings(
            message = stringResource(R.string.preferences_repeat_counters),
            checked = unlimitedNotification,
            onCheckedChange = { prefs?.unlimitedNotification?.value = it },
            width = width,
            onWidthChange = { width = max(width, it) }

        )
        AnimatedVisibility(
            visible = !unlimitedNotification,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = LocalMinimumInteractiveComponentSize.current)
                    .padding(start = 6.dp, end = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.preferences_repeat_nums),
                    style = textStyle_14_400,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 6.dp)
                )
                var currentWidth by remember { mutableStateOf(0.dp) }
                Text(
                    text = notificationLimits.toString(),
                    textAlign = TextAlign.Center,
                    style = textStyle_18_500,
                    modifier = Modifier
                        .onSizeChanged {
                            currentWidth = density.run { it.width.toDp() }
                            width = max(width, currentWidth)
                        }
                        .then(
                            if (currentWidth > 0.dp)
                                Modifier.width(max(width, currentWidth))
                            else
                                Modifier
                        )
                        .clickable {
                            notificationLimitsDialogShow = true
                        },
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = LocalMinimumInteractiveComponentSize.current)
                .padding(start = 6.dp, end = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Звук уведомления",
                style = textStyle_14_400,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 6.dp)
            )
            Text(
                text = sound.title,
                style = textStyle_14_700,
                modifier = Modifier
                    .clickable {
                        soundSelectorDialogShow = true
//                        val intent = Intent().apply {
//                            action = Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS
//                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
//                            putExtra(Settings.EXTRA_CHANNEL_ID, ALARM_CHANNEL_ID)
//                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                        }
//
//                        try {
//                            context.startActivity(intent)
//                        } catch (e: Exception) {
//                            // Fallback на общие настройки
//                            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
//                                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
//                                }.apply {
//                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                            }
//
//                            context.startActivity(intent)
//                        }
                    }
            )
        }
    }
}

@Composable
fun CheckedSettings(
    message: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    width: Dp,
    onWidthChange: (Dp) -> Unit
) {

    val density = LocalDensity.current
    var currentWidth by remember { mutableStateOf(0.dp) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = LocalMinimumInteractiveComponentSize.current)
            .padding(start = 6.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = message,
            style = textStyle_14_400,
            modifier = Modifier
                .weight(1f)
                .padding(end = 6.dp)
        )
        Switch(
            modifier = Modifier
                .onSizeChanged {
                    currentWidth = density.run { it.width.toDp() }
                    onWidthChange(currentWidth)
                }
                .then(
                    if (currentWidth > 0.dp)
                        Modifier.width(max(width, currentWidth))
                    else
                        Modifier
                ),
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors()
        )
    }
}

@Preview(locale = "ru")
@Composable
private fun SettingsScreenPreview() {
    SettingsScreen()
}