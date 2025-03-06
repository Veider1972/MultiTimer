package ru.veider.multitimer.ui.compose.counter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.veider.multitimer.R
import ru.veider.multitimer.const.CounterState
import ru.veider.multitimer.core.utils.HhMmSs
import ru.veider.multitimer.data.Counter
import ru.veider.multitimer.ui.theme.colorPrimary
import ru.veider.multitimer.ui.theme.colorPrimaryDark
import ru.veider.multitimer.ui.theme.colorTimerAlarm
import ru.veider.multitimer.ui.theme.colorTimerSimple
import ru.veider.multitimer.ui.theme.fontFamily400
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CounterWidget(
    counter: Counter,
    dotColor: Color = colorPrimaryDark,
    startColor: Color = colorPrimary,
    endColor: Color = colorTimerAlarm,
    backgroundColor: Color = colorTimerSimple,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val sample by rememberUpdatedState(if (counter.maxProgress == 0) stringResource(R.string.setTime) else counter.currentProgress.HhMmSs)
    val density = LocalDensity.current
    val dotSize = remember { density.run { 7.dp.toPx() } }
    val lineWidth = remember { density.run { 3.dp.toPx() } }
    val textMeasurer = rememberTextMeasurer()
    var textSize by remember { mutableStateOf(0.1.sp) }
    val ratio by rememberUpdatedState(if (counter.maxProgress > 0) counter.currentProgress.toFloat() / counter.maxProgress else 1f)
    val angle by rememberUpdatedState(360 * ratio)
    val degree by rememberUpdatedState((angle * PI / 180).toFloat())
    val currentLineColor by rememberUpdatedState(
        Color(
            red = endColor.red + (startColor.red - endColor.red) * ratio,
            green = endColor.green + (startColor.green - endColor.green) * ratio,
            blue = endColor.blue + (startColor.blue - endColor.blue) * ratio,
        )
    )
    val currentDotColor by rememberUpdatedState(
        Color(
            red = endColor.red + (dotColor.red - endColor.red) * ratio,
            green = endColor.green + (dotColor.green - endColor.green) * ratio,
            blue = endColor.blue + (dotColor.blue - endColor.blue) * ratio,
        )
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .drawBehind {
                var startSize = 0.1.sp
                val floatWidth = (this.size.width - 2 * dotSize) * 0.98f
                val stringWidth = density.run { floatWidth.toSp() }
                var width = density.run { textMeasurer.measure(sample, TextStyle(fontSize = startSize)).size.width.toSp() }
                while (width < stringWidth) {
                    width = density.run { textMeasurer.measure(sample, TextStyle(fontSize = (startSize.value + 0.1f).sp)).size.width.toSp() }
                    if (width < stringWidth)
                        startSize = (startSize.value + 0.1f).sp
                }
                textSize = startSize
                val path = Path()
                path.addArc(Rect(Offset(dotSize / 2, dotSize / 2), Size(this.size.width - dotSize, this.size.height - dotSize)), -90f, -angle)
                val radius = size.width / 2 - dotSize / 2
                drawCircle(
                    color = backgroundColor,
                    radius = radius,
                    center = Offset(size.width / 2, size.height / 2),
                    style = Stroke(
                        width = lineWidth
                    )
                )
                if (counter.maxProgress > 0) {
                    drawPath(
                        path = path,
                        color = currentLineColor,
                        style = Stroke(
                            width = lineWidth
                        )

                    )

                    val posX = size.width / 2 - radius * sin(degree)
                    val posY = size.width / 2 - radius * cos(degree)
                    drawCircle(
                        color = currentDotColor,
                        radius = dotSize / 2,
                        center = Offset(posX, posY)
                    )
                }

            }
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = sample,
            fontSize = textSize,
            fontFamily = fontFamily400
        )
    }

}

@Preview(widthDp = 60, heightDp = 60)
@Composable
private fun CounterWidgetPreview() {
    CounterWidget(
        counter = Counter(
            id = 0,
            currentProgress = 100,
            maxProgress = 200,
            startTime = 0,
            state = CounterState.PAUSED,
            title = "dghdfghdfgh"
        ),
        onClick = {}
    )
}