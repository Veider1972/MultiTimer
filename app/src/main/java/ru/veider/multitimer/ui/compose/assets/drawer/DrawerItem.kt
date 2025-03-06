package ru.veider.multitimer.ui.compose.assets.drawer

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.veider.multitimer.R

@Composable
fun DrawerItem(
    @DrawableRes icon: Int,
    @StringRes label: Int,
    selected: Boolean,
    onClick: ()->Unit
) {
    NavigationDrawerItem(
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp).width(200.dp),
        icon = {
            Icon(
                painter = painterResource(icon),
                contentDescription = null
            )
        },
        label = {
            Text(
                text = stringResource(label),
                fontSize = 16.sp
            )
        },
        onClick = onClick,
        selected = selected
    )
}