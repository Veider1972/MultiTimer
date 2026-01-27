package ru.veider.multitimer.ui.assets.dialogs

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import kotlinx.coroutines.launch
import ru.rustore.sdk.review.t
import ru.veider.multitimer.R
import ru.veider.multitimer.const.doublePadding
import ru.veider.multitimer.core.utils.fadingEdge
import ru.veider.multitimer.core.utils.getTime
import ru.veider.multitimer.core.utils.hours
import ru.veider.multitimer.core.utils.minutes
import ru.veider.multitimer.core.utils.seconds
import ru.veider.multitimer.ui.assets.dialogs.wrappers.TitledTwoButtonsDialogWrapper
import ru.veider.multitimer.ui.screens.counters.assets.NumberScroller
import ru.veider.multitimer.ui.theme.colorOnSurface
import ru.veider.multitimer.ui.theme.colorPrimary
import ru.veider.multitimer.ui.theme.colorPrimaryDark
import ru.veider.multitimer.ui.theme.colorTransparent
import ru.veider.multitimer.ui.theme.textStyle_50_500
import kotlin.math.roundToInt

@SuppressLint("SimpleDateFormat")
@Composable
fun TimeEditor(
    title: String = stringResource(R.string.timer_settings),
    time: Int,
    isMulti: Boolean,
    onAccept: (Int) -> Unit,
    onCancel: () -> Unit
) {
    if (isMulti)
        TimeEditorMulti(
            title = title,
            time = time,
            onAccept = onAccept,
            onCancel = onCancel
        )
    else
        TimeEditorMono(
        title = title,
        time = time,
        onAccept = onAccept,
        onCancel = onCancel
    )

}

@SuppressLint("SimpleDateFormat")
@Composable
fun TimeEditorMulti(
    title: String = stringResource(R.string.timer_settings),
    time: Int,
    onAccept: (Int) -> Unit,
    onCancel: () -> Unit
) {

    val scope = rememberCoroutineScope()
    var currentHoursTens by remember { mutableIntStateOf(time.hours / 10) }
    var currentHours by remember { mutableIntStateOf(time.hours % 10) }
    var currentMinutesTens by remember { mutableIntStateOf(time.minutes / 10) }
    var currentMinutes by remember { mutableIntStateOf(time.minutes % 10) }
    var currentSecondsTens by remember { mutableIntStateOf(time.seconds / 10) }
    var currentSeconds by remember { mutableIntStateOf(time.seconds % 10) }

    val density = LocalDensity.current
    val hoursTensState = rememberLazyListState()
    val hoursState = rememberLazyListState()
    val minutesTensState = rememberLazyListState()
    val minutesState = rememberLazyListState()
    val secondsTensState = rememberLazyListState()
    val secondsState = rememberLazyListState()
    val textStyle = textStyle_50_500
    var height by remember { mutableStateOf(0.dp) }
    val heightPx by rememberUpdatedState(newValue = density.run { height.toPx() })
    val cornerRadius = remember { density.run { 6.dp.toPx() } }
    val borderColor = colorPrimaryDark

    LaunchedEffect(Unit) {
        hoursTensState.animateScrollToItem(currentHoursTens, 0)
    }
    LaunchedEffect(Unit) {
        hoursState.animateScrollToItem(currentHours, 0)
    }
    LaunchedEffect(Unit) {
        minutesTensState.animateScrollToItem(currentMinutesTens, 0)
    }
    LaunchedEffect(Unit) {
        minutesState.animateScrollToItem(currentMinutes, 0)
    }
    LaunchedEffect(Unit) {
        secondsTensState.animateScrollToItem(currentSecondsTens, 0)
    }
    LaunchedEffect(Unit) {
        secondsState.animateScrollToItem(currentSecondsTens, 0)
    }

    LaunchedEffect(hoursState.isScrollInProgress) {
        if (!hoursState.isScrollInProgress) {
            if (heightPx > 0) {
                val index =
                    hoursState.firstVisibleItemIndex + (hoursState.firstVisibleItemScrollOffset / heightPx).roundToInt()
                scope.launch {
                    currentHours = index
                    hoursState.animateScrollToItem(index, 0)
                }
            }
        }
    }
    LaunchedEffect(minutesState.isScrollInProgress) {
        if (!minutesState.isScrollInProgress) {
            if (heightPx > 0) {
                val index =
                    minutesState.firstVisibleItemIndex + (minutesState.firstVisibleItemScrollOffset / heightPx).roundToInt()
                scope.launch {
                    currentMinutes = index
                    minutesState.animateScrollToItem(index, 0)
                }
            }
        }
    }
    LaunchedEffect(secondsState.isScrollInProgress) {
        if (!secondsState.isScrollInProgress) {
            if (heightPx > 0) {
                val index =
                    secondsState.firstVisibleItemIndex + (secondsState.firstVisibleItemScrollOffset / heightPx).roundToInt()
                scope.launch {
                    currentSeconds = index
                    secondsState.animateScrollToItem(index, 0)
                }
            }
        }
    }

    var hoursTensReady by remember { mutableStateOf(false) }
    var hoursReady by remember { mutableStateOf(false) }
    var minutesTensReady by remember { mutableStateOf(false) }
    var minutesReady by remember { mutableStateOf(false) }
    var secondsTensReady by remember { mutableStateOf(false) }
    var secondsReady by remember { mutableStateOf(false) }
    val readyToShow by rememberUpdatedState(
        if (LocalInspectionMode.current)
            true
        else
            hoursTensReady && hoursReady
                    && minutesTensReady && minutesReady
                    && secondsTensReady && secondsReady)

    TitledTwoButtonsDialogWrapper(
        title = title,
        border = BorderStroke(1.dp, colorPrimary),
        cancelButtonText = stringResource(R.string.button_text_cancel),
        acceptButtonText = stringResource(R.string.button_text_accept),
        show = readyToShow,
        onCancel = onCancel,
        onAccept = {
            onAccept(getTime(10 * currentHoursTens + currentHours, 10 * currentMinutesTens + currentMinutes, 10 * currentSecondsTens + currentSeconds))
        }) {
        Row(
            modifier = Modifier
                .drawWithContent {
                    drawContent()
                    drawLine(
                        color = borderColor,
                        start = Offset(0f, heightPx * 2 + 5),
                        end = Offset(size.width, heightPx * 2 + 5),
                        strokeWidth = 5f
                    )
                    drawLine(
                        color = borderColor,
                        start = Offset(0f, heightPx * 3 + 5),
                        end = Offset(size.width, heightPx * 3 + 5),
                        strokeWidth = 5f
                    )
                }
                .fadingEdge(
                    Brush.verticalGradient(
                        0f to colorTransparent,
                        0.25f to colorOnSurface.copy(alpha = 0.2f),
                        0.4f to colorOnSurface,
                        0.6f to colorOnSurface,
                        0.75f to colorOnSurface.copy(alpha = 0.2f),
                        1f to colorTransparent,
                    )
                )
        ) {
            Row(modifier = Modifier.padding(horizontal = doublePadding)) {
                NumberScroller(
                    initialNumber = currentHoursTens,
                    range = 0..9,
                    onNumberChange = { currentHoursTens = it },
                    modifier = Modifier
                        .height(height * 5),
                    textAlign = TextAlign.Center,
                    firstZero = false,
                    onItemHeight = {
                        height = it
                    },
                    readyToShow = { hoursTensReady = it }
                )
                NumberScroller(
                    initialNumber = currentHours,
                    range = 0..9,
                    onNumberChange = { currentHours = it },
                    modifier = Modifier
                        .height(height * 5),
                    textAlign = TextAlign.Center,
                    firstZero = false,
                    onItemHeight = {
                        height = it
                    },
                    readyToShow = { hoursReady = it }
                )
                TimeDivider(height, textStyle)
                NumberScroller(
                    initialNumber = currentMinutesTens,
                    range = 0..5,
                    onNumberChange = { currentMinutesTens = it },
                    modifier = Modifier
                        .height(height * 5),
                    textAlign = TextAlign.Center,
                    firstZero = false,
                    onItemHeight = {},
                    readyToShow = { minutesTensReady = it }
                )
                NumberScroller(
                    initialNumber = currentMinutes,
                    range = 0..9,
                    onNumberChange = { currentMinutes = it },
                    modifier = Modifier
                        .height(height * 5),
                    textAlign = TextAlign.Center,
                    firstZero = false,
                    onItemHeight = {},
                    readyToShow = { minutesReady = it }
                )
                TimeDivider(height, textStyle)
                NumberScroller(
                    initialNumber = currentSecondsTens,
                    range = 0..5,
                    onNumberChange = { currentSecondsTens = it },
                    modifier = Modifier
                        .height(height * 5),
                    textAlign = TextAlign.Center,
                    firstZero = false,
                    onItemHeight = {},
                    readyToShow = { secondsTensReady = it }
                )
                NumberScroller(
                    initialNumber = currentSeconds,
                    range = 0..9,
                    onNumberChange = { currentSeconds = it },
                    modifier = Modifier
                        .height(height * 5),
                    textAlign = TextAlign.Center,
                    firstZero = false,
                    onItemHeight = {},
                    readyToShow = { secondsReady = it }
                )
            }
        }

    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun TimeEditorMono(
    title: String = stringResource(R.string.timer_settings),
    time: Int,
    onAccept: (Int) -> Unit,
    onCancel: () -> Unit
) {

    val scope = rememberCoroutineScope()
    var currentHours by remember { mutableIntStateOf(time.hours) }
    var currentMinutes by remember { mutableIntStateOf(time.minutes) }
    var currentSeconds by remember { mutableIntStateOf(time.seconds) }

    val density = LocalDensity.current
    val hoursState = rememberLazyListState()
    val minutesState = rememberLazyListState()
    val secondsState = rememberLazyListState()
    val textStyle = textStyle_50_500
    var height by remember { mutableStateOf(0.dp) }
    val heightPx by rememberUpdatedState(newValue = density.run { height.toPx() })
    val cornerRadius = remember { density.run { 6.dp.toPx() } }
    val borderColor = colorPrimaryDark

    LaunchedEffect(Unit) {
        hoursState.animateScrollToItem(time.hours, 0)
    }
    LaunchedEffect(Unit) {
        minutesState.animateScrollToItem(time.minutes, 0)
    }
    LaunchedEffect(Unit) {
        secondsState.animateScrollToItem(time.seconds, 0)
    }

    LaunchedEffect(hoursState.isScrollInProgress) {
        if (!hoursState.isScrollInProgress) {
            if (heightPx > 0) {
                val index =
                    hoursState.firstVisibleItemIndex + (hoursState.firstVisibleItemScrollOffset / heightPx).roundToInt()
                scope.launch {
                    currentHours = index
                    hoursState.animateScrollToItem(index, 0)
                }
            }
        }
    }
    LaunchedEffect(minutesState.isScrollInProgress) {
        if (!minutesState.isScrollInProgress) {
            if (heightPx > 0) {
                val index =
                    minutesState.firstVisibleItemIndex + (minutesState.firstVisibleItemScrollOffset / heightPx).roundToInt()
                scope.launch {
                    currentMinutes = index
                    minutesState.animateScrollToItem(index, 0)
                }
            }
        }
    }
    LaunchedEffect(secondsState.isScrollInProgress) {
        if (!secondsState.isScrollInProgress) {
            if (heightPx > 0) {
                val index =
                    secondsState.firstVisibleItemIndex + (secondsState.firstVisibleItemScrollOffset / heightPx).roundToInt()
                scope.launch {
                    currentSeconds = index
                    secondsState.animateScrollToItem(index, 0)
                }
            }
        }
    }

    var hoursReady by remember { mutableStateOf(false) }
    var minutesReady by remember { mutableStateOf(false) }
    var secondsReady by remember { mutableStateOf(false) }
    val readyToShow by rememberUpdatedState(if (LocalInspectionMode.current) true else hoursReady && minutesReady && secondsReady)

    TitledTwoButtonsDialogWrapper(
        title = title,
        border = BorderStroke(1.dp, colorPrimary),
        cancelButtonText = stringResource(R.string.button_text_cancel),
        acceptButtonText = stringResource(R.string.button_text_accept),
        show = readyToShow,
        onCancel = onCancel,
        onAccept = {
            onAccept(getTime(currentHours, currentMinutes, currentSeconds))
        }) {
        Row(
            modifier = Modifier
                .drawWithContent {
                    drawContent()
                    drawLine(
                        color = borderColor,
                        start = Offset(0f, heightPx * 2 + 5),
                        end = Offset(size.width, heightPx * 2 + 5),
                        strokeWidth = 5f
                    )
                    drawLine(
                        color = borderColor,
                        start = Offset(0f, heightPx * 3 + 5),
                        end = Offset(size.width, heightPx * 3 + 5),
                        strokeWidth = 5f
                    )
                }
                .fadingEdge(
                    Brush.verticalGradient(
                        0f to colorTransparent,
                        0.25f to colorOnSurface.copy(alpha = 0.2f),
                        0.4f to colorOnSurface,
                        0.6f to colorOnSurface,
                        0.75f to colorOnSurface.copy(alpha = 0.2f),
                        1f to colorTransparent,
                    )
                )
        ) {
            Row(modifier = Modifier.padding(horizontal = doublePadding)) {
                NumberScroller(
                    initialNumber = currentHours,
                    range = 0..24,
                    onNumberChange = { currentHours = it },
                    modifier = Modifier
                        .height(height * 5),
                    textAlign = TextAlign.Center,
                    onItemHeight = {
                        height = it
                    },
                    readyToShow = { hoursReady = it }
                )
                TimeDivider(height, textStyle)
                NumberScroller(
                    initialNumber = currentMinutes,
                    range = 0..59,
                    onNumberChange = { currentMinutes = it },
                    modifier = Modifier
                        .height(height * 5),
                    textAlign = TextAlign.Center,
                    onItemHeight = {},
                    readyToShow = { minutesReady = it }
                )
                TimeDivider(height, textStyle)
                NumberScroller(
                    initialNumber = currentSeconds,
                    range = 0..59,
                    onNumberChange = { currentSeconds = it },
                    modifier = Modifier
                        .height(height * 5),
                    textAlign = TextAlign.Center,
                    onItemHeight = {},
                    readyToShow = { secondsReady = it }
                )
            }
        }

    }
}

@Composable
fun TimeDivider(
    height: Dp,
    textStyle: TextStyle
) {
    Box(
        modifier = Modifier.height(5 * height),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = ":",
            color = colorPrimaryDark,
            style = textStyle

        )
    }
}

@Preview
@Composable
private fun Preview() {
    TimeEditor(
        time = 1234567,
        isMulti = false,
        onAccept = {},
        onCancel = {})
}