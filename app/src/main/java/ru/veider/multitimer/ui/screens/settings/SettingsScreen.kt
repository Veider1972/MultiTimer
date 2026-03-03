package ru.veider.multitimer.ui.screens.settings

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
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.veider.multitimer.R
import ru.veider.multitimer.const.doublePadding
import ru.veider.multitimer.const.emptySound
import ru.veider.multitimer.domain.entity.Preferences
import ru.veider.multitimer.ui.assets.dialogs.NumberEditor
import ru.veider.multitimer.ui.assets.dialogs.SoundSelector
import ru.veider.multitimer.ui.theme.colorPrimary
import ru.veider.multitimer.ui.theme.textStyle_14_400
import ru.veider.multitimer.ui.theme.textStyle_14_700
import ru.veider.multitimer.ui.theme.textStyle_18_500
import ru.veider.multitimer.ui.theme.textStyle_18_700
import ru.veider.multitimer.utils.createAlarmNotificationChannel
import ru.veider.multitimer.utils.deleteChannel
import java.util.UUID

@Composable
fun SettingsScreen() {

    val context = LocalContext.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val prefs: Preferences? = if (LocalInspectionMode.current) null else koinInject()
    val keepScreenOn = prefs?.keepScreenOn?.collectAsState()?.value ?: true
    val unlimitedNotification = prefs?.unlimitedNotification?.collectAsState()?.value ?: true
    val notificationLimits = prefs?.notificationLimits?.collectAsState()?.value ?: 20
    val timeEditorIsMulti = prefs?.timeEditorIsMulti?.collectAsState()?.value ?: true
    val notificationInterval = prefs?.notificationInterval?.collectAsState()?.value ?: 5
    val sound = prefs?.sound?.collectAsState()?.value ?: emptySound()
    val alternativeSoundOut = prefs?.alternativeSoundOut?.collectAsState()?.value ?: false
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

    var notificationIntervalDialogShow by remember { mutableStateOf(false) }
    if (notificationIntervalDialogShow)
        NumberEditor(
            title = stringResource(R.string.notification_interval),
            value = notificationInterval,
            minValue = 5,
            maxValue = 30,
            onAccept = {
                prefs?.notificationInterval?.value = it
                notificationIntervalDialogShow = false
            },
            onCancel = {
                notificationIntervalDialogShow = false
            }
        )

    Column {
        Text(
            text = stringResource(R.string.preferences_common_title),
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
        Text(
            text = stringResource(R.string.preferences_notification_title),
            style = textStyle_18_700,
            modifier = Modifier.padding(start = 6.dp, top = doublePadding, end = 6.dp)
        )
        HorizontalDivider(thickness = 1.dp, color = colorPrimary)
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
        StringSettings(
            title = stringResource(R.string.notification_sound),
            value = sound.title,
            width = width,
            onClick = { soundSelectorDialogShow = true }
        )
        CheckedSettings(
            message = stringResource(R.string.alternative_sound_out),
            checked = alternativeSoundOut,
            onCheckedChange = {
                prefs?.alternativeSoundOut?.value = it
                scope.launch(Dispatchers.IO){
                    prefs?.alarmChannelId?.value?.let { oldChannel ->
                        context.deleteChannel(oldChannel)
                        val newChannelId = UUID.randomUUID().toString()
                        prefs.alarmChannelId.value = newChannelId
                        context.createAlarmNotificationChannel(
                            uri = sound.uri.toUri(),
                            channelId = newChannelId
                        )
                    }
                }
                              },
            width = width,
            onWidthChange = { width = max(width, it) }

        )
        StringSettings(
            title = stringResource(R.string.notification_interval),
            value = "$notificationInterval ${stringResource(R.string.seconds)}",
            width = width,
            onClick = { notificationIntervalDialogShow = true }
        )
        Text(
            text = stringResource(R.string.preferences_timer_title),
            style = textStyle_18_700,
            modifier = Modifier.padding(start = 6.dp, top = doublePadding, end = 6.dp)
        )
        HorizontalDivider(thickness = 1.dp, color = colorPrimary)
        CheckedSettings(
            message = stringResource(R.string.multi_time_edit),
            checked = timeEditorIsMulti,
            onCheckedChange = { prefs?.timeEditorIsMulti?.value = it },
            width = width,
            onWidthChange = { width = max(width, it) }
        )
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

@Composable
private fun StringSettings(
    title: String,
    value: String,
    width: Dp,
    onClick: () -> Unit,
) {

    val density = LocalDensity.current
    val measurer = rememberTextMeasurer()
    val style = textStyle_14_700
    val textWidth = remember(value) { density.run { measurer.measure(value, style).size.width.toDp() } }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = LocalMinimumInteractiveComponentSize.current)
            .padding(start = 6.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = textStyle_14_400,
            modifier = Modifier
                .weight(1f)
                .padding(end = 6.dp)
        )
        Text(
            text = value,
            style = style,
            modifier = Modifier
                .width(max(width, textWidth))
                .clickable {
                    onClick()
                },
            textAlign = if (width >= textWidth) TextAlign.Center else TextAlign.End
        )
    }
}

@Preview(locale = "ru")
@Composable
private fun SettingsScreenPreview() {
    SettingsScreen()
}