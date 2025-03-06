package ru.veider.multitimer.ui.compose.assets.dialogs.wrappers

import androidx.compose.foundation.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import ru.veider.multitimer.ui.theme.colorPrimary

@Composable
fun TwoButtonDialog(
	title: String,
	message: String,
	modifier: Modifier = Modifier,
	cornerRadius: Dp = 24.dp,
	color: Color = MaterialTheme.colorScheme.surface,
	border: BorderStroke = BorderStroke(width = 1.dp, color = colorPrimary),
	acceptButtonText: String,
	onAccept: () -> Unit,
	cancelButtonText: String,
	onCancel: () -> Unit,
	usePlatformDefaultWidth: Boolean = true
) {
	TitledTwoButtonsDialogWrapper(
		title = title,
		modifier = modifier,
		cornerRadius = cornerRadius,
		color = color,
		border = border,
		acceptButtonText = acceptButtonText,
		onAccept = onAccept,
		cancelButtonText = cancelButtonText,
		onCancel = onCancel,
		usePlatformDefaultWidth = usePlatformDefaultWidth
	) {
		Text(
			text = message
		)
	}
}

@Preview
@Composable
private fun TwoButtonDialogShow() {
	TwoButtonDialog(
		title = "Ошибка!",
		message = "Название события не должно быть пустым",
		acceptButtonText = "Принять",
		onAccept = {},
		cancelButtonText = "Отменить",
		onCancel = {})
}