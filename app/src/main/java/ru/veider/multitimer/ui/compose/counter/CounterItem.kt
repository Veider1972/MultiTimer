package ru.veider.multitimer.ui.compose.counter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import ru.veider.multitimer.const.CounterState
import ru.veider.multitimer.data.Counter
import ru.veider.multitimer.ui.compose.assets.dialogs.TimeEditor
import ru.veider.multitimer.ui.compose.assets.dialogs.TitleEditor
import ru.veider.multitimer.ui.theme.colorOnSurface
import ru.veider.multitimer.ui.theme.colorPrimary
import ru.veider.multitimer.ui.theme.colorSurface
import ru.veider.multitimer.ui.theme.colorTimerSimple
import ru.veider.multitimer.ui.theme.textStyle_13_400
import ru.veider.multitimer.ui.theme.textStyle_18_700
import ru.veider.multitimer.viewmodel.MainViewModel

@Composable
fun CounterItem(
    modifier: Modifier = Modifier,
    counter: Counter
) {

    val viewModel: MainViewModel = koinInject()
    val scope = rememberCoroutineScope()

    val currentCounter by rememberUpdatedState(counter)

    var titleEditorView by remember { mutableStateOf(false) }
    if (titleEditorView)
        TitleEditor(
            title = counter.title,
            onTitleChange = {
                scope.launch {
                    viewModel.updateTitle(counter.id, it)
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
            onAccept = {
                scope.launch {
                    viewModel.updateMaxProgress(counter.id, it)
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
        onCounterClick = { timerEditorView = true },
        onConterStart = { viewModel.startCounter(counter.id) },
        onCounterPause = { viewModel.pauseCounter(counter.id) },
        onCounterStop = { viewModel.stopCounter(counter.id) }
    )
}

@Composable
private fun CounterItemBody(
    modifier: Modifier = Modifier,
    counter: Counter,
    onTitleClick: () -> Unit,
    onCounterClick: () -> Unit,
    onConterStart: () -> Unit,
    onCounterPause: () -> Unit,
    onCounterStop: () -> Unit,
) {
    Row(
        modifier = modifier
            .background(color = colorSurface)
            .padding(vertical = 6.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CounterWidget(
            counter = rememberUpdatedState(counter).value,
            modifier = Modifier
                .padding(start = 6.dp)
                .size(80.dp),
            onClick = onCounterClick
        )
        Column(
            modifier = Modifier.padding(horizontal = 6.dp)
        ) {
            Text(
                text = if (counter.title.isNotEmpty()) counter.title else "Без названия",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onTitleClick()
                    },
                style = textStyle_18_700,
                color = if (counter.title.isNotEmpty()) colorOnSurface else colorTimerSimple,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    text = "СТАРТ",
                    enabled = counter.state == CounterState.FINISHED || counter.state == CounterState.PAUSED,
                    modifier = Modifier.weight(1f),
                    onClick = onConterStart
                )
                Button(
                    text = "ПАУЗА",
                    enabled = counter.state == CounterState.RUN,
                    modifier = Modifier.weight(1f),
                    onClick = onCounterPause
                )
                Button(
                    text = if (counter.state == CounterState.RUN || counter.state == CounterState.FINISHED || counter.state == CounterState.ALARMED) "СТОП" else "СБРОС",
                    enabled = counter.state == CounterState.RUN || counter.state == CounterState.PAUSED || counter.state == CounterState.ALARMED,
                    modifier = Modifier.weight(1f),
                    onClick = onCounterStop
                )
            }

        }
    }
}

@Composable
fun Button(text: String, enabled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = colorPrimary,

            ),
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Text(
            text = text,
            style = textStyle_13_400,
            color = colorOnSurface
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
                state = CounterState.PAUSED,
                title = "Прилёт флота"
            ),
            {}, {}, {}, {}, {}
        )
    }

}

