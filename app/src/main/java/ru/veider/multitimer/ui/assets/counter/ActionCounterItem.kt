package ru.veider.multitimer.ui.assets.counter

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.veider.multitimer.const.CounterState
import ru.veider.multitimer.data.Counter
import ru.veider.multitimer.ui.theme.colorPrimary

@Composable
fun ActionCounterItem(
    counter: Counter,
    horizontalSwipeEnable: Boolean,
    modifier: Modifier = Modifier,
    onDelete: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }

    // Обновление состояния после завершения жеста
    LaunchedEffect(offsetX.value) {
        if (offsetX.value <= -100f) {
            onDelete(counter.id)
            offsetX.animateTo(0f)
        } else if (offsetX.value >= 100f) {
//            onSwipeRight(counter.id)
            offsetX.animateTo(0f)
        } else {
            offsetX.animateTo(0f)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (horizontalSwipeEnable)
                    Modifier.pointerInput(Unit) {
                        detectHorizontalDragGestures { _, dragAmount ->
                            scope.launch {
                                val originalX = offsetX.value
                                var newValue = originalX + dragAmount
                                if (newValue>0)
                                    newValue = 0f
                                offsetX.snapTo(newValue)
                            }
                        }
                    }
                else
                    Modifier
            )

    ) {
        // Фон для действий при сдвиге

        IconButton(
            onClick = { /*onSwipeLeft(counter.id)*/ },
            modifier = Modifier
                .background(Color.Red)
                .align(Alignment.CenterEnd)
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Delete")
        }

        // Основной контент элемента
        CounterItem(
            modifier = Modifier.offset { IntOffset(offsetX.value.toInt(), 0) },
            counter = counter
        )

        HorizontalDivider(
            modifier = Modifier.align(Alignment.BottomCenter),
            thickness = 1.dp,
            color = colorPrimary
        )
    }
}

data class HorizontalSwipeState(
    val offsetX: Float = 0f, // Смещение по горизонтали (для сдвига влево/вправо)
    val isSwiped: Boolean = false // Флаг, указывающий, был ли элемент сдвинут
)

@Preview(widthDp = 300, heightDp = 50, apiLevel = 35)
@Composable
private fun ActionCounterItemPreview() {
    ActionCounterItem(
        counter = Counter(id = 0, currentProgress = 10000, maxProgress = 20000, startTime = 10000, state = CounterState.RUN, title = "Приготовление супа"),
        horizontalSwipeEnable = true,
        onDelete = {}
    )
}