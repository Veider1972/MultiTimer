package ru.veider.multitimer.ui.assets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize

@Composable
fun MeasureIntrinsics(
    content: @Composable () -> Unit,
    onMeasure: (IntSize) -> Unit
) {
    var height by remember { mutableIntStateOf(0) }
    var width by remember { mutableIntStateOf(0) }
    content()
    Layout(
        content = content,
        measurePolicy = { measurables, constraints ->
            if (height == 0) {
                val placeable = measurables.first().measure(
                    constraints.copy(
                        minWidth = 0,
                        maxWidth = constraints.maxWidth,
                        minHeight = 0,
                        maxHeight = Constraints.Infinity
                    )
                )
                height = placeable.height
                width = placeable.width
                onMeasure(IntSize(width,height))
            }
            layout(0, 0) {}
        }
    )
}