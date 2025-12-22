package ru.veider.multitimer.ui.assets.dialogs.wrappers

import android.R.attr.height
import android.R.attr.onClick
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.veider.multitimer.const.alphaTransition
import ru.veider.multitimer.ui.assets.buttons.DialogButton
import ru.veider.multitimer.ui.theme.colorPrimary
import ru.veider.multitimer.ui.theme.paddingsDouble
import ru.veider.multitimer.ui.theme.paddingsSingle
import ru.veider.multitimer.ui.theme.textStyle_16_400

@Composable
fun TitledTwoButtonsDialogWrapper(
    title: String,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    color: Color = MaterialTheme.colorScheme.surface,
    border: BorderStroke = BorderStroke(width = 1.dp, color = colorPrimary),
    cancelButtonText: String,
    acceptButtonText: String,
    onCancel: () -> Unit,
    onAccept: () -> Unit,
    usePlatformDefaultWidth: Boolean = true,
    show: Boolean = true,
    content: @Composable (ColumnScope) -> Unit
) {

    val scope = rememberCoroutineScope()
    var dialogShow by remember(show) { mutableStateOf(show) }

    TitledDialogWrapper(
        title = title,
        modifier = modifier,
        cornerRadius = cornerRadius,
        color = color,
        border = border,
        usePlatformDefaultWidth = usePlatformDefaultWidth,
        show = dialogShow
    ) {

        val density = LocalDensity.current
        var width by remember { mutableStateOf(0.dp) }


        Column(
            modifier = Modifier
                .onSizeChanged {
                    width = density.run { it.width.toDp() }
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
                content(this)
            Row(
                modifier = Modifier
                    .then(
                        if (width > 0.dp) Modifier.width(width) else Modifier
                    ),
                horizontalArrangement = Arrangement.End
            ) {
                DialogButton(
                    text = cancelButtonText.uppercase(),
                    onClick = {
                        scope.launch {
                            dialogShow = false
                            delay(alphaTransition.toLong())
                            onCancel()
                        }
                    }
                )
                DialogButton(
                    text = acceptButtonText.uppercase(),
                    onClick = {
                        scope.launch {
                            dialogShow = false
                            delay(alphaTransition.toLong())
                            onAccept()
                        }
                    }
                )
            }
        }

    }
}

@Preview
@Composable
fun TitledTwoButtonsDialogWrapperShow() {
    TitledTwoButtonsDialogWrapper(
        title = "Заголовок",
        cancelButtonText = "Отменить",
        acceptButtonText = "Принять",
        onCancel = {},
        onAccept = {}) {

    }
}