package ru.veider.multitimer.ui.screens.counters

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.koin.compose.koinInject
import ru.veider.multitimer.MainActivity
import ru.veider.multitimer.R
import ru.veider.multitimer.const.CounterState
import ru.veider.multitimer.data.Counter
import ru.veider.multitimer.ui.assets.counter.ActionCounterItem
import ru.veider.multitimer.ui.assets.counter.CounterItem
import ru.veider.multitimer.ui.assets.dialogs.wrappers.TwoButtonDialog
import ru.veider.multitimer.ui.theme.colorSurface
import ru.veider.multitimer.viewmodel.MainViewModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun TimersScreen() {

    val context = LocalContext.current
    val viewModel: MainViewModel = koinInject()

    val counters = viewModel.counters.collectAsState().value



    LaunchedEffect(Unit) {
        while (coroutineContext.isActive) {
            delay(1000)
            Log.d("Counter", "MainState viewModel=$viewModel timerTick: $counters")
        }
    }

    var counterIdToDeleting: Int? by remember { mutableStateOf(null) }
    if (counterIdToDeleting != null) {
        val counter =
            remember(counterIdToDeleting) { counters.find { it.id == counterIdToDeleting } }
        val title = remember(counter) {
            counter?.title?.ifEmpty { (context as MainActivity).getString(R.string.no_name) }
                ?: (context as MainActivity).getString(R.string.no_name)
        }
        TwoButtonDialog(
            title = stringResource(R.string.delete_time_ask),
            message = title,
            acceptButtonText = stringResource(R.string.button_text_yes),
            cancelButtonText = stringResource(R.string.button_text_no),
            onAccept = {
                counterIdToDeleting?.let {
                    viewModel.deleteCounter(it)
                }
                counterIdToDeleting = null
            },
            onCancel = { counterIdToDeleting = null }
        )
    }

    TimersScreenBody(
        counters = counters,
        onMove = { from, to -> viewModel.swapCounters(from, to) },
        onDelete = { counterIdToDeleting = it }
    )
}

@Composable
fun TimersScreenBody(
    modifier: Modifier = Modifier,
    counters: List<Counter>,
    onMove: (Int, Int) -> Unit,
    onDelete: (Int) -> Unit
) {
    val inspectionMode = LocalInspectionMode.current
    val hapticFeedback = LocalHapticFeedback.current
    val listState = rememberLazyListState()
    // Состояние для каждого элемента
    val swipeStates = remember { mutableStateMapOf<Int, SwipeState>() }
    var draggedItem by remember { mutableStateOf<Counter?>(null) }
    val reordarableState = rememberReorderableLazyListState(listState) { from, to ->
        onMove(counters[from.index].id, counters[to.index].id)
        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
    }
    var horizontalSwipeEnable by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(color = colorSurface),
        state = listState
    ) {

        if (inspectionMode) {
            items(items = counters) {
                CounterItem(
                    counter = it
                )
            }
        }
        items(counters.size, key = { counters[it].id }) { index ->
            val counter = counters[index]
            ReorderableItem(
                state = reordarableState,
                key = counter.id,
            ) {
                val swipeState = swipeStates[counter.id] ?: SwipeState()
                val offsetY = remember { Animatable(swipeState.offsetY) }

                ActionCounterItem(
                    counter = counter,
                    horizontalSwipeEnable = horizontalSwipeEnable,
                    modifier = Modifier
                        .zIndex(if (draggedItem?.id == counter.id) 1f else 0f)
                        .longPressDraggableHandle(
                            onDragStarted = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                            },
                            onDragStopped = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                            }
                        )
                        .offset { IntOffset(0, offsetY.value.toInt()) },
                    onDelete = { onDelete(counter.id) }
                )
            }
        }
    }
}

data class SwipeState(
    val offsetX: Float = 0f, // Смещение по горизонтали (для сдвига влево/вправо)
    val offsetY: Float = 0f, // Смещение по вертикали (для перетаскивания вверх/вниз)
    val isSwiped: Boolean = false // Флаг, указывающий, был ли элемент сдвинут
)

@Preview()
@Composable
private fun MyLazyColumnPreview() {
    val context = LocalContext.current
    TimersScreenBody(
        modifier = Modifier,
        counters = listOf(
            Counter(
                id = 0,
                currentProgress = 10000,
                maxProgress = 20000,
                startTime = 10000,
                state = CounterState.RUN,
                title = "Приготовление супа"
            ),
            Counter(
                id = 1,
                currentProgress = 25000,
                maxProgress = 30000,
                startTime = 2000000,
                state = CounterState.PAUSED,
                title = "Прилёт флота"
            )
        ),
        onMove = { _, _ -> },
        onDelete = {}
    )
}