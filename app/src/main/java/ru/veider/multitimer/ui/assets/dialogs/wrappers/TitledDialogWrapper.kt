package ru.veider.multitimer.ui.assets.dialogs.wrappers

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import ru.veider.multitimer.ui.theme.colorPrimary
import ru.veider.multitimer.ui.theme.colorSurface
import ru.veider.multitimer.ui.theme.paddingsDouble
import ru.veider.multitimer.ui.theme.textStyle_18_700

@Composable
fun TitledDialogWrapper(
    title: String,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    color: Color = MaterialTheme.colorScheme.surface,
    border: BorderStroke = BorderStroke(width = 0.dp, color = Color.Transparent),
    usePlatformDefaultWidth: Boolean = true,
    show: Boolean = true,
    content: @Composable () -> Unit
) {

    val density = LocalDensity.current
    var width by remember {mutableStateOf(0.dp)}

    DialogWrapper(
        modifier = modifier,
        cornerRadius = cornerRadius,
        color = color,
        border = border,
        usePlatformDefaultWidth = usePlatformDefaultWidth,
        show = show && width > 0.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.onSizeChanged{ (w,_) ->
                width = density.run { w.toDp() }
            }
        ) {
            Row(modifier = Modifier
                .then(
                    if (width > 0.dp)
                        Modifier.width(width)
                    else
                        Modifier
                )
                .background(color = colorPrimary)
                .padding(paddingsDouble),
                horizontalArrangement = Arrangement.Center){
                Text(
                    text = title.uppercase(),
                    color = colorSurface,
                    style = textStyle_18_700,

                    )
            }

            Column(
                modifier = Modifier
                    .padding(start = paddingsDouble, top = paddingsDouble, end = paddingsDouble),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content()
            }
        }
    }
}

@Preview
@Composable
fun TitledDialogShow() {
    TitledDialogWrapper(title = "Заголовок") {

    }
}