package ru.veider.multitimer.ui.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.veider.multitimer.R
import ru.veider.multitimer.ui.theme.colorPrimary
import ru.veider.multitimer.ui.theme.titleColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    onOpenDrawer: ()->Unit,
    onAddCounter: ()->Unit
) {

    val scope = rememberCoroutineScope()

    TopAppBar(
        windowInsets = TopAppBarDefaults.windowInsets,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorPrimary,
            titleContentColor = titleColor
        ),
        navigationIcon = {
            Image(
                painter = painterResource(R.drawable.navigation),
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clickable { onOpenDrawer() },
                contentDescription = null
            )
        },
        title = {
            Text(
                text = stringResource(R.string.app_name)
            )
        },
        actions = {
            Image(
                painterResource(R.drawable.add_alarm),
                contentDescription = null,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clickable { onAddCounter() }
            )
        }
    )
}

@Preview
@Composable
private fun TopBarPreview() {
    TopBar(
        {},
        {}
    )
}