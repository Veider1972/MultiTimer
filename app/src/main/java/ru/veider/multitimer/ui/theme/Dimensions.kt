package ru.veider.multitimer.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val bigPadding = 24.dp
val mediumPadding = 16.dp
val smallPadding = 8.dp

val paddingsHalf = 3.dp
val paddingsSingle = 6.dp
val paddingsDouble = 12.dp

val smallBorderCorner = 6.dp

val bigElevation = 12.dp
val mediumElevation = 8.dp
val smallElevation = 6.dp

/** Corner radii shared by every surface so cards, dialogs and buttons stay consistent. */
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

val cardCornerRadius = 20.dp
val dialogCornerRadius = 28.dp
val buttonCornerRadius = 6.dp
val chipCornerRadius = 10.dp
val listItemCornerRadius = 16.dp
