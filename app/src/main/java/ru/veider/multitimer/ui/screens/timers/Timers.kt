package ru.veider.multitimer.ui.screens.timers

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.rememberReorderableLazyGridState
import org.burnoutcrew.reorderable.reorderable
import ru.veider.multitimer.const.CounterState
import ru.veider.multitimer.data.Counter
import ru.veider.multitimer.ui.assets.counter.ActionCounterItem
import ru.veider.multitimer.ui.theme.colorSurface

@Composable
fun MyLazyColumn(
    modifier: Modifier = Modifier,
    counters: List<Counter>,
    onTitleChange: (Int, String) -> Unit,
    onCounterChange: (Int, Int) -> Unit,
    onCounterStart: (Int) -> Unit,
    onCounterPause: (Int) -> Unit,
    onCounterStop: (Int) -> Unit,
    onSwipeLeft: (Int) -> Unit,
    onSwipeRight: (Int) -> Unit,
    onMove: (Int, Int) -> Unit // Функция для изменения порядка элементов
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    // Состояние для каждого элемента
    val swipeStates = remember { mutableStateMapOf<Int, SwipeState>() }
    var draggedItem by remember { mutableStateOf<Counter?>(null) }
    val reordarableState = rememberReorderableLazyGridState(
        onMove = { from, to -> onMove(counters[from.index].id, counters[to.index].id) }
    )
    var horizontalSwipeEnable by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(color = colorSurface)
            .reorderable(reordarableState),
        state = listState
    ) {

        items(counters.size, key = { counters[it].id }) { index ->
            val counter = counters[index]
            ReorderableItem(
                reorderableState = reordarableState,
                key = counter.id,
                index = index + 1
            ) {

                val swipeState = swipeStates[counter.id] ?: SwipeState()
                val offsetY = remember { Animatable(swipeState.offsetY) }

                ActionCounterItem(
                    counter = counter,
                    horisontalSwipeEnable = horizontalSwipeEnable,
                    modifier = Modifier
                        .zIndex(if (draggedItem?.id == counter.id) 1f else 0f)
                        .pointerInput(Unit) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = {
                                    scope.launch {
                                        horizontalSwipeEnable = false
                                        draggedItem = counter
                                    }
                                },
                                onDragEnd = {
                                    scope.launch {
                                        horizontalSwipeEnable = true
                                        draggedItem = null
                                        offsetY.snapTo(0f) // Сброс смещения по вертикали
                                    }

                                },
                                onDrag = { change, dragAmount ->
                                    scope.launch {
                                        change.consume()
                                        // Обработка вертикального перетаскивания
                                        offsetY.snapTo(offsetY.value + dragAmount.y)
                                        // Определяем новый индекс элемента
                                        val newIndex = (index + (dragAmount.y / 50).toInt())
                                            .coerceIn(0, counters.size - 1)
                                        if (newIndex != index) {
                                            onMove(index, newIndex)
                                        }
                                    }

                                }
                            )
                        }
                        .offset { IntOffset(0, offsetY.value.toInt()) },
                    onDelete = {}
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

@Preview(apiLevel = 34)
@Composable
private fun MyLazyColumnPreview() {
    MyLazyColumn(
        modifier = Modifier,
        counters = listOf(
            Counter(
                id = 0, currentProgress = 10000, maxProgress = 20000, startTime = 10000, state = CounterState.RUN, title = "Приготовление супа"
            ),
            Counter(
                id = 1, currentProgress = 25000, maxProgress = 30000, startTime = 2000000, state = CounterState.PAUSED, title = "Прилёт флота"
            )
        ),
        onTitleChange = { _, _ -> },
        onCounterChange = { _, _ -> },
        onCounterStart = {},
        onCounterPause = {},
        onCounterStop = {},
        onSwipeLeft = {},
        onSwipeRight = {},
        onMove = { _, _ -> }
    )
}