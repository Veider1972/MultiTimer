package ru.veider.multitimer.ui.compose.assets.dialogs.wrappers

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import ru.veider.multitimer.ui.theme.colorPrimary
import ru.veider.multitimer.ui.theme.colorSurface
import ru.veider.multitimer.ui.theme.paddingsDouble
import ru.veider.multitimer.ui.theme.textStyle_18_700

@Composable
fun TitledDialog(
	title: String,
	modifier: Modifier = Modifier,
	cornerRadius: Dp = 24.dp,
	color: Color = MaterialTheme.colorScheme.surface,
	border: BorderStroke = BorderStroke(width = 0.dp, color = Color.Transparent),
	usePlatformDefaultWidth: Boolean = true,
	content: @Composable () -> Unit
) {
	DialogWrapper(
		modifier = modifier,
		cornerRadius = cornerRadius,
		color = color,
		border = border,
		usePlatformDefaultWidth = usePlatformDefaultWidth
	) {
		Column(horizontalAlignment = Alignment.CenterHorizontally) {
			Text(
				text = title.uppercase(),
				textAlign = TextAlign.Center,
				color = colorSurface,
				style = textStyle_18_700,
				modifier = Modifier
					.fillMaxWidth()
					.background(color = colorPrimary)
					.padding(paddingsDouble)
			)
			Column(
				modifier = Modifier
					.fillMaxWidth()
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
fun TitledDialogShow(){
	TitledDialog(title = "Заголовок") {
		
	}
}