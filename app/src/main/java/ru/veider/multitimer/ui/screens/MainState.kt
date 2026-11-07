package ru.veider.multitimer.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import androidx.navigationevent.compose.rememberNavigationEventDispatcherOwner
import com.arttttt.nav3router.Nav3Host
import com.arttttt.nav3router.rememberNav3Navigator
import com.arttttt.nav3router.rememberRouter
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import ru.veider.multitimer.navigation.Screen
import ru.veider.multitimer.ui.screens.about.AboutScreen
import ru.veider.multitimer.ui.screens.counters.TimersScreen
import ru.veider.multitimer.ui.screens.main.DrawerSheet
import ru.veider.multitimer.ui.screens.main.TopBar
import ru.veider.multitimer.ui.screens.settings.SettingsScreen
import ru.veider.multitimer.viewmodel.MainViewModel

@Composable
fun MainState() {
    val inspectionMode = LocalInspectionMode.current
    val viewModel: MainViewModel? = if (inspectionMode) null else koinInject()

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
                        viewModel?.addCounter()
                    }
                )
            }
        ) { paddingValue ->
            Nav3Host(
                backStack = backStack,
                router = router,
                navigator = navigator
            ) { backStack, onBack, router ->
                // Создаём контент NavDisplay отдельно
                val navContent: @Composable () -> Unit = {
                    NavDisplay(
                        backStack = if (inspectionMode) remember { mutableStateListOf<Any>(Screen.Counters) } else backStack,
                        modifier = Modifier.padding(paddingValue),
                        onBack = {},
                        entryProvider = entryProvider {
                            entry<Screen.Counters> { TimersScreen() }
                            entry<Screen.Settings> { SettingsScreen() }
                            entry<Screen.About> { AboutScreen() }
                        },
                    )
                }

                if (inspectionMode) {
                    // В превью предоставляем локального владельца диспетчера
                    val dispatcherOwner = rememberNavigationEventDispatcherOwner(parent = null)
                    CompositionLocalProvider(LocalNavigationEventDispatcherOwner provides dispatcherOwner) {
                        navContent()
                    }
                } else {
                    navContent()
                }
            }
        }
    }

}

@Preview(locale = "RU", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun MainStateBodyPreview() {
    MainState()
}