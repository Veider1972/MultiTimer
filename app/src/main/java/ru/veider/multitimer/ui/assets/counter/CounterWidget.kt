package ru.veider.multitimer.ui.assets.counter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CounterWidget(
    counter: Counter,
    alarmed: Boolean,
    dotColor: Color = colorPrimaryDark,
    startColor: Color = colorPrimary,
    endColor: Color = colorTimerAlarm,
    backgroundColor: Color = colorTimerSimple,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val sample by rememberUpdatedState(if (counter.maxProgress == 0) stringResource(R.string.set_time) else counter.currentProgress.HhMmSs)
    val density = LocalDensity.current
    val dotSize = remember { density.run { 7.dp.toPx() } }
    val lineWidth = remember { density.run { 3.dp.toPx() } }
    val textMeasurer = rememberTextMeasurer()
    var textSize by remember { mutableStateOf(0.1.sp) }
    val ratio by rememberUpdatedState(
        if (alarmed)
            1f
        else
            if (counter.maxProgress > 0) counter.currentProgress.toFloat() / counter.maxProgress else 1f
    )
    val angle by rememberUpdatedState(360 * ratio)
    val degree by rememberUpdatedState((angle * PI / 180).toFloat())

    var color by remember { mutableStateOf(Color.Black) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var scale by remember { mutableFloatStateOf(0f) }

    val steps = 20

    LaunchedEffect(alarmed) {
        color = Color.Black
        rotation = 0f
        scale = 0f
        launch {
            while (alarmed) {
                val delta = (500 / steps).toLong()
                (0..steps).forEach {
                    rotation = getValue(0f, 1f, steps, it)
                    scale = rotation
                    delay(delta)
                }
                (0..2 * steps).forEach {
                    rotation = getValue(1f, -1f, 2 * steps, it)
                    scale = rotation
                    delay(delta)
                }
                (0..steps).forEach {
                    rotation = getValue(-1f, 0f, steps, it)
                    scale = rotation
                    delay(delta)
                }
            }

        }
        launch {
            while (alarmed) {
                val delta = (500 / steps).toLong()
                (0..steps).forEach {
                    color = getColor(Color.Black, Color.Red, steps, it)
                    delay(delta)
                }
                (0..steps).forEach {
                    color = getColor(Color.Red, Color.Black, steps, it)
                    delay(delta)
                }
            }

        }
    }

    val currentLineColor by rememberUpdatedState(
        if (alarmed)
            color
        else
            Color(ColorUtils.blendARGB(endColor.toArgb(), startColor.toArgb(), ratio))
    )
    val currentDotColor by rememberUpdatedState(
        if (alarmed)
            Color.Transparent
        else
            Color(ColorUtils.blendARGB(endColor.toArgb(), dotColor.toArgb(), ratio))
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
            .clip(CircleShape)
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = alarmed,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Icon(
                painter = painterResource(R.drawable.notifications),
                contentDescription = null,
                modifier = Modifier
                    .scale(1.5f + 0.3f * abs(scale))
                    .rotate(rotation * 5),
                tint = color
            )
        }
        AnimatedVisibility(
            visible = !alarmed,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = sample,
                fontSize = textSize,
                fontFamily = fontFamily400
            )
        }

    }

}

fun getValue(
    start: Float,
    end: Float,
    steps: Int,
    currentStep: Int
): Float {
    if (steps == 0 || start == end) return start
    val progress = currentStep.toFloat() / steps.toFloat()
    return start + (end - start) * progress
}

fun getColor(
    start: Color,
    end: Color,
    steps: Int,
    currentStep: Int
): Color {
    if (steps == 0 || start == end) return start
    val progress = currentStep.toFloat() / steps.toFloat()
    // Конвертируем в HSV
    val startHsv = FloatArray(3)
    android.graphics.Color.RGBToHSV(
        (start.red * 255).toInt(),
        (start.green * 255).toInt(),
        (start.blue * 255).toInt(),
        startHsv
    )
    val endHsv = FloatArray(3)
    android.graphics.Color.RGBToHSV(
        (end.red * 255).toInt(),
        (end.green * 255).toInt(),
        (end.blue * 255).toInt(),
        endHsv
    )

    // Интерполируем компоненты HSV
    val currentHsv = FloatArray(3)
    for (i in 0..2) {
        currentHsv[i] = startHsv[i] + (endHsv[i] - startHsv[i]) * progress
    }

    // Особенность Hue: интерполируем по кратчайшему пути
    val hueDiff = endHsv[0] - startHsv[0]
    val hueDistance = if (abs(hueDiff) > 180) {
        if (hueDiff > 0) hueDiff - 360 else hueDiff + 360
    } else {
        hueDiff
    }
    currentHsv[0] = (startHsv[0] + hueDistance * progress + 360) % 360

    // Конвертируем обратно в RGB
    val rgbColor = android.graphics.Color.HSVToColor(currentHsv)

    // Интерполируем альфа-канал отдельно
    val alpha = start.alpha + (end.alpha - start.alpha) * progress

    return Color(rgbColor).copy(alpha = alpha)
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
            state = CounterState.ALARMED,
            title = "dghdfghdfgh"
        ),
        alarmed = true,
        onClick = {}
    )
}