package ru.veider.multitimer.ui.compose.assets.dialogs

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import kotlinx.coroutines.launch
import ru.veider.multitimer.core.utils.fadingEdge
import ru.veider.multitimer.core.utils.getTime
import ru.veider.multitimer.core.utils.hours
import ru.veider.multitimer.core.utils.minutes
import ru.veider.multitimer.core.utils.seconds
import ru.veider.multitimer.core.utils.toZeroStr
import ru.veider.multitimer.ui.compose.assets.dialogs.wrappers.TitledTwoButtonsDialogWrapper
import ru.veider.multitimer.ui.theme.colorOnSurface
import ru.veider.multitimer.ui.theme.colorPrimary
import ru.veider.multitimer.ui.theme.colorPrimaryDark
import ru.veider.multitimer.ui.theme.colorTransparent
import ru.veider.multitimer.ui.theme.textStyle_40_500
import kotlin.math.roundToInt

@SuppressLint("SimpleDateFormat")
@Composable
fun TimeEditor(
    title: String = "Настройка таймера",
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
    val textStyle = textStyle_40_500
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
                val index = hoursState.firstVisibleItemIndex + (hoursState.firstVisibleItemScrollOffset / heightPx).roundToInt()
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
                val index = minutesState.firstVisibleItemIndex + (minutesState.firstVisibleItemScrollOffset / heightPx).roundToInt()
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
                val index = secondsState.firstVisibleItemIndex + (secondsState.firstVisibleItemScrollOffset / heightPx).roundToInt()
                scope.launch {
                    currentSeconds = index
                    secondsState.animateScrollToItem(index, 0)
                }
            }
        }
    }

    TitledTwoButtonsDialogWrapper(
        title = title,
        border = BorderStroke(1.dp, colorPrimary),
        cancelButtonText = "Отменить",
        acceptButtonText = "Принять",
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
                        end = Offset(size.width,heightPx * 2 + 5),
                        strokeWidth = 5f
                    )
                    drawLine(
                        color = borderColor,
                        start = Offset(0f, heightPx * 3 + 5),
                        end = Offset(size.width,heightPx * 3 + 5),
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
//            Spacer(modifier = Modifier.weight(0.1f))
            LazyColumn(
                state = hoursState,
                reverseLayout = true,
                modifier = Modifier
                    .height(height * 5)
                    .weight(0.3f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                repeat(2) {
                    item {
                        Box(modifier = Modifier.height(height)) {}
                    }
                }
                (0..23).forEach {
                    item(key = it) {
                        Text(
                            text = it.toZeroStr(),
                            color = if (it == currentHours) colorOnSurface else colorPrimary,
                            textAlign = TextAlign.Center,
                            style = textStyle,
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned {
                                    height = density.run { it.size.height.toDp() }
                                }

                        )
                    }
                }
                repeat(2) {
                    item {
                        Box(modifier = Modifier.height(height)) {}
                    }
                }
            }
            TimeDivider(height, textStyle)
            LazyColumn(
                state = minutesState,
                reverseLayout = true,
                modifier = Modifier
                    .height(height * 5)
                    .weight(0.3f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                repeat(2) {
                    item {
                        Box(modifier = Modifier.height(height)) {}
                    }
                }
                (0..59).forEach {
                    item {
                        Text(
                            text = it.toZeroStr(),
                            textAlign = TextAlign.Center,
                            color = if (it == currentMinutes) colorOnSurface else colorPrimary,
                            style = textStyle

                        )
                    }
                }
                repeat(2) {
                    item {
                        Box(modifier = Modifier.height(height)) {}
                    }
                }
            }
            TimeDivider(height, textStyle)
            LazyColumn(
                state = secondsState,
                reverseLayout = true,
                modifier = Modifier
                    .height(height * 5)
                    .weight(0.3f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                repeat(2) {
                    item {
                        Box(modifier = Modifier.height(height)) {}
                    }
                }
                (0..59).forEach {
                    item {
                        Text(
                            text = it.toZeroStr(),
                            color = if (it == currentSeconds) colorOnSurface else colorPrimary,
                            style = textStyle,
                            textAlign = TextAlign.Center

                        )
                    }
                }
                repeat(2) {
                    item {
                        Box(modifier = Modifier.height(height)) {}
                    }
                }
            }
//            Spacer(modifier = Modifier.weight(0.1f))
        }

    }
}

@Composable
fun TimeDivider(
    height: Dp,
    textStyle: TextStyle
) {
    Box(
        modifier = Modifier.size(25.dp, 5 * height),
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
fun TimeEditorPreview() {
    TimeEditor(
        time = 60,
        onAccept = {},
        onCancel = {})
}