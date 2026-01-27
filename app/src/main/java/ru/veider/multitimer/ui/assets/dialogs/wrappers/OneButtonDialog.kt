package ru.veider.multitimer.ui.assets.dialogs.wrappers

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import ru.veider.multitimer.const.doublePadding
import ru.veider.multitimer.const.singlePadding
import ru.veider.multitimer.ui.theme.colorPrimary
import ru.veider.multitimer.ui.theme.textStyle_18_400

@Composable
fun OneButtonDialog(
	title: String,
	message: String,
	modifier: Modifier = Modifier,
	cornerRadius: Dp = 24.dp,
	color: Color = MaterialTheme.colorScheme.surface,
	border: BorderStroke = BorderStroke(width = 1.dp, color = colorPrimary),
	buttonText: String,
	onClick: () -> Unit,
	usePlatformDefaultWidth: Boolean = true
) {
	TitledOneButtonDialogWrapper(
		title = title,
		modifier = modifier,
		cornerRadius = cornerRadius,
		color = color,
		border = border,
		buttonText = buttonText,
		onClick = onClick,
		usePlatformDefaultWidth = usePlatformDefaultWidth
	) {scope ->
        with(scope){
            Text(
                text = message,
                style = textStyle_18_400,
                modifier = Modifier.padding(horizontal = singlePadding, vertical = doublePadding)
            )
        }
	}
}

@Preview
@Composable
private fun OneButtonDialogShow() {
	OneButtonDialog(
		title = "Ошибка!",
		message = "Название события не должно быть пустым",
		buttonText = "Закрыть",
		onClick = {})
}