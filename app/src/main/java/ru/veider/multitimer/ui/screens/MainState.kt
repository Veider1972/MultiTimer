package ru.veider.multitimer.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import ru.veider.multitimer.const.CounterState
import ru.veider.multitimer.data.Counter
import ru.veider.multitimer.domain.entity.CurrentState
import ru.veider.multitimer.ui.screens.main.DrawerSheet
import ru.veider.multitimer.ui.screens.main.TopBar
import ru.veider.multitimer.ui.screens.timers.MyLazyColumn
import ru.veider.multitimer.viewmodel.MainViewModel

@Composable
fun MainState() {

    val viewModel: MainViewModel = koinInject()

    val counters = viewModel.counters.collectAsState().value
    var state by remember { mutableStateOf(CurrentState.Counters) }



    LaunchedEffect(Unit) {
        while (coroutineContext.isActive) {
            delay(1000)
            Log.d("Counter", "MainState viewModel=$viewModel timerTick: $counters")
        }
    }

    MainStateBody(
        counters = counters,
        state = state,
        onStateChange = { state = it },
        onAddCounter = { viewModel.addCounter() },
        onTitleChange = { id, title -> viewModel.updateTitle(id, title) },
        onCounterChange = { id, maxProgress -> viewModel.updateMaxProgress(id, maxProgress) },
        onCounterStart = { id -> viewModel.startCounter(id) },
        onCounterPause = { id -> viewModel.pauseCounter(id) },
        onCounterStop = { id -> viewModel.stopCounter(id) },
        onMove = { from, to -> viewModel.swapCounters(from, to) }
    )
}

@Composable
private fun MainStateBody(
    counters: List<Counter>,
    state: CurrentState,
    onStateChange: (CurrentState) -> Unit,
    onAddCounter: () -> Unit,
    onTitleChange: (Int, String) -> Unit,
    onCounterChange: (Int, Int) -> Unit,
    onCounterStart: (Int) -> Unit,
    onCounterPause: (Int) -> Unit,
    onCounterStop: (Int) -> Unit,
    onMove: (Int, Int) -> Unit
) {

    val countersList by rememberUpdatedState(counters)

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    ModalNavigationDrawer(
        modifier = Modifier,
        drawerState = drawerState,
        drawerContent = {
            DrawerSheet(
                state = state,
                onStateChange = {
                    scope.launch {
                        onStateChange(it)
                        drawerState.close()
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    onOpenDrawer = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    onAddCounter = onAddCounter
                )
            }
        ) { paddingValue ->
            MyLazyColumn(
                modifier = Modifier.padding(paddingValue),
                counters = countersList,
                onTitleChange = onTitleChange,
                onCounterChange = { id, progress -> onCounterChange(id, progress) },
                onCounterStart = onCounterStart,
                onCounterPause = onCounterPause,
                onCounterStop = onCounterStop,
                onMove = onMove,
                onSwipeLeft = {},
                onSwipeRight = {}
            )
        }
    }

}

@Preview
@Composable
private fun MainStateBodyPreview() {
    MainStateBody(
        counters = listOf(
            Counter(
                id = 0, currentProgress = 10000, maxProgress = 20000, startTime = 10000, state = CounterState.RUN, title = "Приготовление супа"
            ),
            Counter(
                id = 1, currentProgress = 25000, maxProgress = 30000, startTime = 2000000, state = CounterState.PAUSED, title = "Прилёт флота"
            )
        ),
        state = CurrentState.Counters,
        onStateChange = {},
        onAddCounter = {},
        onTitleChange = { _, _ -> },
        onCounterChange = { _, _ -> },
        onCounterStart = {},
        onCounterPause = {},
        onCounterStop = {},
        onMove = {_, _ -> }
    )
}