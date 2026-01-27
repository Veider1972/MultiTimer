package ru.veider.multitimer.ui.assets.dialogs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.veider.multitimer.R
import ru.veider.multitimer.const.singlePadding
import ru.veider.multitimer.ui.assets.dialogs.wrappers.TitledTwoButtonsDialogWrapper
import ru.veider.multitimer.ui.theme.colorTimerSimple
import ru.veider.multitimer.ui.theme.paddingsDouble
import ru.veider.multitimer.ui.theme.textStyle_18_400
import ru.veider.multitimer.ui.theme.textStyle_20_400

@Composable
fun TitleEditor(
    title: String,
    onTitleChange: (String) -> Unit,
    onDismiss: () -> Unit
) {

    var text by remember { mutableStateOf(title) }

    TitledTwoButtonsDialogWrapper(
        title = stringResource(R.string.timerTitle),
        acceptButtonText = stringResource(R.string.button_text_accept),
        cancelButtonText = stringResource(R.string.button_text_cancel),
        onAccept = { onTitleChange(text) },
        onCancel = onDismiss,
        content = {
            BasicTextField(
                modifier = Modifier.fillMaxWidth().padding(vertical = paddingsDouble, horizontal = singlePadding),
                value = text,
                onValueChange = { text = it },
                textStyle = textStyle_18_400,
                decorationBox = { innerTextField ->
                    if (text.isEmpty()) {
                        Text(
                            text = stringResource(R.string.set_timer_name),
                            style = textStyle_20_400,
                            color = colorTimerSimple
                        )
                    }
                    innerTextField()
                }
            )
        }
    )
}

@Preview
@Composable
private fun TitleEditDialogPreview() {
    TitleEditor(
        title = "",
        {}, {}
    )
}