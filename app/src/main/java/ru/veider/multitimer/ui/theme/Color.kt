package ru.veider.multitimer.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import ru.veider.multitimer.R

val colorPrimary @Composable get()= colorResource(id = R.color.color_primary)
val colorPrimaryDark  @Composable get()= colorResource(id = R.color.color_primary_dark)
val colorSurface @Composable get()= colorResource(id = R.color.color_surface)
val colorOnSurface @Composable get()= colorResource(id = R.color.color_on_surface)
val colorOnPrimary @Composable get()= colorResource(id = R.color.color_on_primary)
val colorPrimaryContainer @Composable get()= colorResource(id = R.color.color_primary_container)
val colorOnPrimaryContainer @Composable get()= colorResource(id = R.color.color_on_primary_container)

val colorSecondary @Composable get()= colorResource(id = R.color.color_secondary)
val colorOnSecondary @Composable get()= colorResource(id = R.color.color_on_secondary)
val colorOnSecondaryContainer @Composable get()= colorResource(id = R.color.color_on_secondary_container)

val colorTertiary @Composable get()= colorResource(id = R.color.color_tertiary)
val colorOnTertiary @Composable get()= colorResource(id = R.color.color_on_tertiary)
val colorTertiaryContainer @Composable get()= colorResource(id = R.color.color_tertiary_container)
val colorOnTertiaryContainer @Composable get()= colorResource(id = R.color.color_on_tertiary_container)

val colorTimerSimple @Composable get()= colorResource(id = R.color.timer_simple_color)
val colorTimerAlarm @Composable get()= colorResource(id = R.color.timer_alarm_color)

val colorTransparent @Composable get()= colorResource(id = R.color.transparent)

val colorDivider @Composable get()= colorResource(id = R.color.color_primary)