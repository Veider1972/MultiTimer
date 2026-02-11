package ru.veider.multitimer.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.arttttt.nav3router.Nav3Host
import com.arttttt.nav3router.rememberNav3Navigator
import com.arttttt.nav3router.rememberRouter
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import ru.veider.multitimer.domain.entity.CurrentState
import ru.veider.multitimer.navigation.Screen
import ru.veider.multitimer.ui.screens.about.AboutScreen
import ru.veider.multitimer.ui.screens.main.DrawerSheet
import ru.veider.multitimer.ui.screens.main.TopBar
import ru.veider.multitimer.ui.screens.settings.SettingsScreen
import ru.veider.multitimer.ui.screens.counters.TimersScreen
import ru.veider.multitimer.viewmodel.MainViewModel

@Composable
fun MainState() {

    val viewModel: MainViewModel = koinInject()

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val backStack = rememberNavBackStack(Screen.Counters)
    val router = rememberRouter<Screen>()
    val navigator = rememberNav3Navigator(backStack){}

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerSheet(
                state = backStack.last() as Screen,
                onStateChange = {
                    scope.launch {
                        backStack.add(it)
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
                    onAddCounter = {
                        viewModel.addCounter()
                    }
                )
            }
        ) { paddingValue ->
            Nav3Host(
                backStack = backStack,
                router = router,
                navigator = navigator
            ) { backStack, onBack, router ->
                NavDisplay(
                    backStack = backStack,
                    modifier = Modifier.padding(paddingValue),
                    onBack = {},
                    entryProvider = entryProvider {
                        entry<Screen.Counters> {
                            TimersScreen()
                        }
                        entry<Screen.Settings> {
                            SettingsScreen()
                        }
                        entry<Screen.About> {
                            AboutScreen()
                        }
                    },
                )
            }
        }
    }

}
//
//@Preview
//@Composable
//private fun MainStateBodyPreview() {
//    MainStateBody(
//        counters = listOf(
//            Counter(
//                id = 0, currentProgress = 10000, maxProgress = 20000, startTime = 10000, state = CounterState.RUN, title = "Приготовление супа"
//            ),
//            Counter(
//                id = 1, currentProgress = 25000, maxProgress = 30000, startTime = 2000000, state = CounterState.PAUSED, title = "Прилёт флота"
//            )
//        ),
//        state = CurrentState.Counters,
//        onStateChange = {},
//        onAddCounter = {},
//        onTitleChange = { _, _ -> },
//        onCounterChange = { _, _ -> },
//        onCounterStart = {},
//        onCounterPause = {},
//        onCounterStop = {},
//        onMove = {_, _ -> }
//    )
//}