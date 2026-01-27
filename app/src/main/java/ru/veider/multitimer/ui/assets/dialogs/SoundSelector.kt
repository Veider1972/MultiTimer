package ru.veider.multitimer.ui.assets.dialogs

import android.media.RingtoneManager.getRingtone
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import ru.veider.multitimer.R
import ru.veider.multitimer.const.alphaTransition
import ru.veider.multitimer.const.singlePadding
import ru.veider.multitimer.core.utils.getAndroidMedia
import ru.veider.multitimer.domain.entity.Preferences
import ru.veider.multitimer.domain.entity.Sound
import ru.veider.multitimer.ui.assets.MeasureIntrinsics
import ru.veider.multitimer.ui.assets.buttons.DialogButton
import ru.veider.multitimer.ui.assets.dialogs.wrappers.TitledDialogWrapper
import ru.veider.multitimer.ui.theme.colorPrimary
import ru.veider.multitimer.ui.theme.textStyle_15_400
import ru.veider.multitimer.ui.theme.textStyle_15_700
import androidx.core.net.toUri

@Composable
fun SoundSelector(
    onAccept: (Sound) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current

    val prefs: Preferences = koinInject()
    val sound by prefs.sound.collectAsState()
    val ringtoneList by lazy {
        getAndroidMedia(context)
    }

    SoundSelectorBody(
        currentSound = sound,
        sounds = ringtoneList,
        onAccept = onAccept,
        onCancel = onCancel
    )
}

@Composable
private fun SoundSelectorBody(
    currentSound: Sound,
    sounds: List<Sound>,
    onAccept: (Sound) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    var dialogShow by remember { mutableStateOf(true) }

    var sound by remember { mutableStateOf(currentSound) }
    var player by remember { mutableStateOf(getRingtone(context, currentSound.uri.toUri())) }
    var availableHeight by remember { mutableStateOf(0.dp) }
    var soundHeight by remember { mutableStateOf(0.dp) }
    var buttonsHeight by remember { mutableStateOf(0.dp) }
    var width by remember { mutableStateOf(0.dp) }

    val maxHeight by rememberUpdatedState(availableHeight - soundHeight - buttonsHeight)

    LaunchedEffect(availableHeight, soundHeight, buttonsHeight) {
        Log.d("Height", "contentHeight=$availableHeight soundHeight=$soundHeight buttonsHeight=$buttonsHeight")
        Log.d("Height", "maxHeight=$maxHeight")
    }

    TitledDialogWrapper(
        title = stringResource(id = R.string.dialog_profile_set_ringtone_title),
        show = dialogShow,
    ) {
        Column(
            modifier = Modifier
                .onGloballyPositioned {
                    if (availableHeight == 0.dp)
                        availableHeight = density.run { it.size.height.toDp() }
                    if (width == 0.dp)
                        width = density.run { it.size.width.toDp() }
                }
        ) {
            Column(
                modifier = Modifier
                    .onGloballyPositioned {
                        if (soundHeight == 0.dp)
                            soundHeight = density.run { it.size.height.toDp() }
                    }
            ) {
                Row(
                    modifier = Modifier
                        .padding(singlePadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${stringResource(R.string.current)} ",
                        style = textStyle_15_400
                    )
                    Text(
                        text = sound.title,
                        style = textStyle_15_700,
                        modifier = Modifier.weight(1f),
                        overflow = TextOverflow.Ellipsis
                    )
                    Box(
                        modifier = Modifier
                            .size(LocalMinimumInteractiveComponentSize.current)
                            .background(colorPrimary.copy(alpha = 0.2f), shape = CircleShape)
                            .clip(CircleShape)
                            .clickable {
                                player.stop()
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                                    player.isLooping = false
                                player.play()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null
                        )
                    }

                }
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = colorPrimary
                )
            }

            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = singlePadding)
                    .then(
                        if (availableHeight > 0.dp && soundHeight > 0.dp && buttonsHeight > 0.dp)
                            Modifier.heightIn(max = maxHeight)
                        else
                            Modifier
                    )
            ) {
                sounds.forEach {
                    item {
                        CheckedMessage(
                            desc = it.title,
                            checked = (it == sound),
                            onClick = {
                                player.stop()
                                Log.d("SoundDebug", "Выбираю звук $it")
                                sound = it
                                Log.d("Notification", "Choosed sound: $it")
                                player = getRingtone(context, it.uri.toUri())
                                player.isLooping = false
                                player.play()
                            }
                        )
                    }
                }
            }
            MeasureIntrinsics(
                content = {
                    Row(
                        modifier = Modifier
                            .then(
                                if (width > 0.dp) Modifier.width(width) else Modifier
                            ),
                        horizontalArrangement = Arrangement.End
                    ) {
                        DialogButton(
                            text = stringResource(R.string.button_text_cancel).uppercase(),
                            onClick = {
                                scope.launch {
                                    dialogShow = false
                                    delay(alphaTransition.toLong())
                                    onCancel()
                                }
                            }
                        )
                        DialogButton(
                            text = stringResource(R.string.button_text_accept).uppercase(),
                            onClick = {
                                scope.launch {
                                    dialogShow = false
                                    delay(alphaTransition.toLong())
                                    player.stop()
                                    Log.d("SoundDebug", "Сохраняю звук $sound")
                                    onAccept(sound)
                                }
                            }
                        )
                    }
                },
                onMeasure = {
                    if (buttonsHeight == 0.dp)
                        buttonsHeight = density.run { it.height.toDp() }
                }
            )


        }

    }
}

@Composable
fun CheckedMessage(desc: String, checked: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Text(
            text = desc, style = textStyle_15_400, maxLines = 1,
            modifier = Modifier
                .weight(1f)
                .padding(end = singlePadding)
        )
        Box(
            modifier = Modifier.size(LocalMinimumInteractiveComponentSize.current),
            contentAlignment = Alignment.Center
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = { onClick() }
            )
        }
    }
}

@Preview(locale = "ru", heightDp = 400)
@Composable
private fun SoundSelectorPreview() {
    SoundSelectorBody(
        currentSound = Sound("Beeep", ""),
        sounds = listOf(
            Sound("Beeep", ""),
            Sound("Звук 1", ""),
            Sound("Beeep", ""),
            Sound("Звук 1", ""),
            Sound("Beeep", ""),
            Sound("Звук 1", ""),
            Sound("Beeep", ""),
            Sound("Звук 1", ""),
            Sound("Beeep", ""),
            Sound("Звук 1", ""),
            Sound("Beeep", ""),
            Sound("Звук 1", ""),

            ),
        onAccept = {},
        onCancel = {}
    )
}