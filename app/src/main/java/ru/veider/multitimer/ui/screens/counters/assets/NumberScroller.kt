package ru.veider.multitimer.ui.screens.counters.assets

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.veider.multitimer.core.utils.toZeroStr
import ru.veider.multitimer.ui.theme.colorOnPrimaryContainer
import ru.veider.multitimer.ui.theme.colorPrimary
import ru.veider.multitimer.ui.theme.textStyle_50_500
import kotlin.math.roundToInt

@SuppressLint("UnrememberedMutableState")
@Composable
fun NumberScroller(
    initialNumber: Int = 0,
    range: IntRange = 0..24,
    onNumberChange: (Int) -> Unit,
    modifier: Modifier = Modifier
        .height(60.dp * 5)
        .fillMaxWidth(),
    textAlign: TextAlign,
    firstZero: Boolean = true,
    onItemHeight: (Dp) -> Unit,
    readyToShow: (Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val numbers = remember { mutableStateListOf<Int>() }
    val min by rememberUpdatedState( range.first)
    val max by rememberUpdatedState( range.last)
    val listState = rememberLazyListState()
    var currentNumber by remember(initialNumber) { mutableIntStateOf(initialNumber) }
    var itemHeight by remember {
        mutableStateOf(density.run {
            textMeasurer.measure(
                "0",
                style = textStyle_50_500
            ).size.height.toDp().also {
                onItemHeight(it)
            }
        })
    }
    var positionReady by remember { mutableStateOf(false) }

    LaunchedEffect(itemHeight, positionReady) {
        if (itemHeight > 0.dp && positionReady)
            readyToShow(true)
    }

    LaunchedEffect(currentNumber) {
        onNumberChange(currentNumber)
    }

    // Инициализация
    LaunchedEffect(Unit) {
        // Создаем большой циклический буфер
        val bufferSize = 100 * (max - min + 1)
        for (i in -bufferSize / 2 until bufferSize / 2) {
            var num = initialNumber + i
            while (num < min) num += (max - min + 1)
            while (num > max) num -= (max - min + 1)
            numbers.add(num)
        }

        // Находим индекс начального числа (должен быть в центре буфера)
        val centerIndex = bufferSize / 2
        listState.scrollToItem(centerIndex - 2) // Чтобы число было в центре видимой области
        positionReady = true
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress && (itemHeight > 0.dp)) {
            val index =
                listState.firstVisibleItemIndex + (listState.firstVisibleItemScrollOffset / density.run { itemHeight.toPx() }).roundToInt()
            scope.launch {
                currentNumber = numbers[index + 2]
                listState.animateScrollToItem(index, 0)
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier,
        reverseLayout = true
    ) {
        itemsIndexed(numbers) { index, number ->
            val isCenter by remember {
                derivedStateOf {
                    val visibleItems = listState.layoutInfo.visibleItemsInfo
                    visibleItems.size >= 5 && visibleItems[2].index == index
                }
            }

            Text(
                text = if (firstZero) number.toZeroStr() else number.toString(),
                color = if (number == currentNumber) colorOnPrimaryContainer else colorPrimary,
                textAlign = textAlign,
                style = textStyle_50_500,
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        itemHeight = density.run { it.size.height.toDp() }
                    }

            )
        }
    }
}

@Preview
@Composable
fun GuaranteedInitialPositionListPreview() {
    Row {
        NumberScroller(
            initialNumber = 3,
            range = 2..10,
            onNumberChange = {},
            textAlign = TextAlign.Center,
            firstZero = false,
            onItemHeight = {},
            readyToShow = {}
        )
    }

}