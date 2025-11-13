package ru.veider.multitimer.ui.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.veider.multitimer.R
import ru.veider.multitimer.domain.entity.CurrentState
import ru.veider.multitimer.ui.screens.main.elements.DrawerItem
import ru.veider.multitimer.ui.theme.colorPrimary
import ru.veider.multitimer.ui.theme.colorPrimaryDark

@Composable
fun DrawerSheet(
    state: CurrentState,
    onStateChange: (CurrentState) -> Unit,
) {

    val gradient = listOf(colorPrimary, colorPrimaryDark)
    val density = LocalDensity.current
    var width by remember { mutableFloatStateOf(0f) }

    ModalDrawerSheet(
        modifier = Modifier.onGloballyPositioned {
            width = it.size.width.toFloat()
        },
        windowInsets = WindowInsets(0.dp,0.dp,0.dp,0.dp)
    ) {
        Column {
            Column(
                modifier = Modifier
                    .width(rememberUpdatedState(density.run { width.toDp() }).value)
                    .drawBehind {
                        val brush = Brush.linearGradient(
                            colors = gradient,
                            start = Offset(0f, 0f),
                            end = Offset(size.width, size.height)
                        )
                        drawRect(
                            brush = brush,
                            topLeft = Offset(0f, 0f),
                            size = size,
                        )
                    }
            ) {
                Image(
                    painter = painterResource(R.drawable.big_timer),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(
                            start = 16.dp,
                            top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                        )
                        .size(100.dp)
                )
                Text(
                    text = stringResource(R.string.app_name).uppercase(),
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 10.dp)
                )
                Text(
                    text = stringResource(R.string.my_email),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(start = 16.dp, bottom = 16.dp)
                )
            }
        }
        DrawerItem(
            icon = R.drawable.icon_timer,
            label = R.string.menu_counters,
            selected = state == CurrentState.Counters,
            onClick = { onStateChange(CurrentState.Counters) }

        )
        DrawerItem(
            icon = R.drawable.icon_settings,
            label = R.string.menu_settings,
            selected = state == CurrentState.Settings,
            onClick = { onStateChange(CurrentState.Settings) }

        )
        DrawerItem(
            icon = R.drawable.icon_about,
            label = R.string.menu_about,
            selected = state == CurrentState.About,
            onClick = { onStateChange(CurrentState.About) }

        )

    }
}

@Preview
@Composable
private fun DrawerSheetPreview() {
    DrawerSheet(
        state = CurrentState.About,
        onStateChange = {}
    )
}