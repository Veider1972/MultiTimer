package ru.veider.multitimer.ui.assets.dialogs

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
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
import ru.veider.multitimer.ui.assets.dialogs.wrappers.TitledTwoButtonsDialogWrapper
import ru.veider.multitimer.ui.screens.counters.assets.NumberScroller
import ru.veider.multitimer.ui.theme.colorOnSurface
import ru.veider.multitimer.ui.theme.colorPrimary
import ru.veider.multitimer.ui.theme.colorPrimaryDark
import ru.veider.multitimer.ui.theme.colorTransparent
import ru.veider.multitimer.ui.theme.textStyle_50_500
import kotlin.math.roundToInt

@Composable
fun NumberEditor(
    title: String,
    value: Int,
    maxValue: Int = 50,
    minValue: Int = 1,
    onAccept: (Int) -> Unit,
    onCancel: () -> Unit
) {

    val scope = rememberCoroutineScope()
    var currentValue by remember { mutableIntStateOf(value) }

    val density = LocalDensity.current
    val state = rememberLazyListState()
    val textStyle = textStyle_50_500
    var height by remember { mutableStateOf(0.dp) }
    val heightPx by rememberUpdatedState(newValue = density.run { height.toPx() })
    val cornerRadius = remember { density.run { 6.dp.toPx() } }
    val borderColor = colorPrimaryDark

    LaunchedEffect(Unit) {
        state.animateScrollToItem(currentValue, 0)
    }

    LaunchedEffect(state.isScrollInProgress) {
        if (!state.isScrollInProgress) {
            if (heightPx > 0) {
                val index =
                    state.firstVisibleItemIndex + (state.firstVisibleItemScrollOffset / heightPx).roundToInt()
                scope.launch {
                    currentValue = index
                    state.animateScrollToItem(index, 0)
                }
            }
        }
    }

    var valueReady by remember { mutableStateOf(false) }

    TitledTwoButtonsDialogWrapper(
        title = title,
        border = BorderStroke(1.dp, colorPrimary),
        cancelButtonText = "Отменить",
        acceptButtonText = "Принять",
        show = valueReady,
        onCancel = onCancel,
        onAccept = {
            onAccept(currentValue)
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
//            Spacer(modifier = Modifier.weight(0.1f))
            NumberScroller(
                initialNumber = currentValue,
                range = minValue..maxValue,
                onNumberChange = { currentValue = it },
                modifier = Modifier
                    .height(height * 5),
                textAlign = TextAlign.Center,
                onItemHeight = {
                    height = it
                },
                readyToShow = {valueReady = it}
            )
        }

    }
}

@Preview(device = Devices.PIXEL_5)
@Composable
private fun NumberEditorPreview() {
    NumberEditor(
        title = "Число повторов",
        value = 60,
        onAccept = {},
        onCancel = {})
}