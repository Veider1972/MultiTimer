package ru.veider.multitimer.ui.screens.main

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.veider.multitimer.R
import ru.veider.multitimer.ui.theme.colorPrimary
import ru.veider.multitimer.ui.theme.textStyle_20_700
import ru.veider.multitimer.ui.theme.titleColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    onOpenDrawer: ()->Unit,
    onAddCounter: ()->Unit
) {

    TopAppBar(
        windowInsets = TopAppBarDefaults.windowInsets,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorPrimary,
            titleContentColor = titleColor
        ),
        navigationIcon = {
            IconButton(
                onClick = onOpenDrawer,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(R.string.navigation_drawer),
                    tint = titleColor
                )
            }
        },
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = textStyle_20_700,
                color = titleColor
            )
        },
        actions = {
            IconButton(
                onClick = onAddCounter,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_timer),
                    tint = titleColor
                )
            }
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