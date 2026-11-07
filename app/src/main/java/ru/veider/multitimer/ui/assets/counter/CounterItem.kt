package ru.veider.multitimer.ui.assets.counter

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import ru.veider.multitimer.MainActivity
import ru.veider.multitimer.R
import ru.veider.multitimer.const.CounterState
import ru.veider.multitimer.data.Counter
import ru.veider.multitimer.domain.entity.Preferences
import ru.veider.multitimer.ui.assets.dialogs.TimeEditor
import ru.veider.multitimer.ui.assets.dialogs.TitleEditor
import ru.veider.multitimer.ui.theme.buttonBackgroundActive
import ru.veider.multitimer.ui.theme.buttonBackgroundInactive
import ru.veider.multitimer.ui.theme.buttonCornerRadius
import ru.veider.multitimer.ui.theme.buttonTextActive
import ru.veider.multitimer.ui.theme.buttonTextInactive
import ru.veider.multitimer.ui.theme.colorOnSurface
import ru.veider.multitimer.ui.theme.colorOnSurfaceVariant
import ru.veider.multitimer.ui.theme.colorSurfaceContainer
import ru.veider.multitimer.ui.theme.colorTimerSimple
import ru.veider.multitimer.ui.theme.textStyle_13_400
import ru.veider.multitimer.ui.theme.textStyle_18_700
import ru.veider.multitimer.ui.theme.textStyle_13_500
import ru.veider.multitimer.viewmodel.MainViewModel

@Composable
fun CounterItem(
    modifier: Modifier = Modifier,
    counter: Counter
) {

    val inspectionMode = LocalInspectionMode.current
    val viewModel: MainViewModel? = if (inspectionMode) null else koinInject()
    val prefs: Preferences? = if (inspectionMode) null else koinInject()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val currentCounter by rememberUpdatedState(counter)

    val timeEditorIsMulti = prefs?.timeEditorIsMulti?.collectAsState()?.value ?: false

    var titleEditorView by remember { mutableStateOf(false) }
    if (titleEditorView)
        TitleEditor(
            title = counter.title,
            onTitleChange = {
                scope.launch {
                    viewModel?.updateTitle(counter.id, it)
                    titleEditorView = false
                }
            },
            onDismiss = {
                scope.launch {
                    titleEditorView = false
                }
            }
        )

    var timerEditorView by remember { mutableStateOf(false) }
    if (timerEditorView)
        TimeEditor(
            time = counter.maxProgress,
            isMulti = timeEditorIsMulti,
            onAccept = {
                scope.launch {
                    viewModel?.updateMaxProgress(counter.id, it)
                    timerEditorView = false
                }

            },
            onCancel = {
                scope.launch {
                    timerEditorView = false
                }
            }
        )

    CounterItemBody(
        modifier = modifier,
        counter = currentCounter,
        onTitleClick = { titleEditorView = true },
        onCounterClick = {
            if (currentCounter.state == CounterState.FINISHED)
                timerEditorView = true
            else
                scope.launch {
                    Toast.makeText(context, (context as MainActivity).resources.getString(R.string.timer_need_stop_first), Toast.LENGTH_LONG).show()
                }
        },
        onCounterStart = {
            prefs?.runCounter?.value++
            viewModel?.startCounter(counter.id) },
        onCounterPause = { viewModel?.pauseCounter(counter.id) },
        onCounterStop = { viewModel?.stopCounter(counter.id) }
    )
}

@Composable
private fun CounterItemBody(
    modifier: Modifier = Modifier,
    counter: Counter,
    onTitleClick: () -> Unit,
    onCounterClick: () -> Unit,
    onCounterStart: () -> Unit,
    onCounterPause: () -> Unit,
    onCounterStop: () -> Unit,
) {
    Row(
        modifier = modifier
            .background(color = colorSurfaceContainer, shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 6.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CounterWidget(
            counter = rememberUpdatedState(counter).value,
            alarmed = counter.state == CounterState.ALARMED,
            modifier = Modifier
                .padding(start = 2.dp)
                .size(78.dp),
            onClick = onCounterClick
        )
        Column(
            modifier = Modifier
                .padding(start = 14.dp)
                .weight(1f)
        ) {
            Text(
                text = counter.title.ifEmpty { stringResource(R.string.no_name) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onTitleClick()
                    },
                style = textStyle_18_700,
                color = if (counter.title.isNotEmpty()) colorOnSurface else colorTimerSimple,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
            Row(
                modifier = Modifier.padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val runButtonEnabled by rememberUpdatedState(counter.state == CounterState.FINISHED || counter.state == CounterState.PAUSED)
                CounterActionButton(
                    text = stringResource(R.string.button_text_start),
                    enabled = runButtonEnabled,
                    textColor = if (runButtonEnabled) buttonTextActive else buttonTextInactive,
                    modifier = Modifier.weight(1f).height(36.dp),
                    onClick = onCounterStart
                )
                val pauseButtonEnabled by rememberUpdatedState(counter.state == CounterState.RUN)
                CounterActionButton(
                    text = stringResource(R.string.button_text_pause),
                    enabled = pauseButtonEnabled,
                    textColor = if (pauseButtonEnabled) buttonTextActive else buttonTextInactive,
                    modifier = Modifier.weight(1f).height(36.dp),
                    onClick = onCounterPause
                )
                val stopButtonEnabled by rememberUpdatedState(counter.state == CounterState.RUN || counter.state == CounterState.PAUSED || counter.state == CounterState.ALARMED)
                CounterActionButton(
                    text = if (stopButtonEnabled) stringResource(R.string.button_text_stop) else stringResource(R.string.button_text_reset),
                    enabled = stopButtonEnabled,
                    textColor = if (stopButtonEnabled)  buttonTextActive else buttonTextInactive,
                    modifier = Modifier.weight(1f).height(36.dp),
                    onClick = onCounterStop
                )
            }

        }
    }
}

@Composable
private fun Counter.statusLabel(): String = when (state) {
    CounterState.RUN -> stringResource(R.string.counter_status_running)
    CounterState.PAUSED -> stringResource(R.string.counter_status_paused)
    CounterState.ALARMED -> stringResource(R.string.counter_status_alarmed)
    CounterState.FINISHED -> stringResource(R.string.counter_status_idle)
}

@Composable
fun CounterActionButton(
    text: String,
    enabled: Boolean,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        enabled = enabled,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonBackgroundActive,
            disabledContainerColor = buttonBackgroundInactive
            ),
        shape = RoundedCornerShape(buttonCornerRadius),
        modifier = modifier
    ) {
        Text(
            text = text,
            style = textStyle_13_400,
            color = textColor
        )
    }
}

@Preview
@Composable
private fun CounterItemPreview() {
    Column {
        CounterItemBody(
            modifier = Modifier,
            Counter(
                id = 0,
                currentProgress = 10000,
                maxProgress = 20000,
                startTime = 0,
                state = CounterState.PAUSED,
                title = "Прилёт флота"
            ),
            {}, {}, {}, {}, {}
        )
        CounterItemBody(
            modifier = Modifier,
            Counter(
                id = 0,
                currentProgress = 0,
                maxProgress = 0,
                startTime = 0,
                state = CounterState.FINISHED,
                title = ""
            ),
            {}, {}, {}, {}, {}
        )
        CounterItemBody(
            modifier = Modifier,
            Counter(
                id = 0,
                currentProgress = 10000,
                maxProgress = 20000,
                startTime = 0,
                state = CounterState.RUN,
                title = "Приготовление супа"
            ),
            {}, {}, {}, {}, {}
        )
    }
}
