package ru.veider.multitimer.ui.compose.assets.dialogs.wrappers

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import ru.veider.multitimer.ui.theme.colorPrimary
import ru.veider.multitimer.ui.theme.paddingsDouble
import ru.veider.multitimer.ui.theme.paddingsSingle
import ru.veider.multitimer.ui.theme.textStyle_16_400

@Composable
fun TitledOneButtonDialogWrapper(
	title: String,
	modifier: Modifier = Modifier,
	cornerRadius: Dp = 24.dp,
	color: Color = MaterialTheme.colorScheme.surface,
	border: BorderStroke = BorderStroke(width = 1.dp, color = colorPrimary),
	buttonText: String,
	onClick: () -> Unit,
	usePlatformDefaultWidth: Boolean = true,
	content: @Composable (ColumnScope) -> Unit
) {
	TitledDialog(
		title = title,
		modifier = modifier,
		cornerRadius = cornerRadius,
		color = color,
		border = border,
		usePlatformDefaultWidth = usePlatformDefaultWidth
	) {
		Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
			content(this)
			Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
				Button(
					onClick = onClick,
					shape = RoundedCornerShape(6.dp),
					modifier = Modifier.padding(start = paddingsDouble, bottom = paddingsDouble, end = paddingsDouble),
					colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = colorPrimary),
					elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
					contentPadding = PaddingValues(vertical = paddingsSingle, horizontal = paddingsDouble)
				) {
					Text(text = buttonText.uppercase(), style = textStyle_16_400)
				}
			}
		}

	}
}

@Preview
@Composable
fun TitledOneButtonDialogWrapperShow() {
	TitledOneButtonDialogWrapper(
		title = "Заголовок",
		buttonText = "Отменить",
		onClick = {}) {

	}
}