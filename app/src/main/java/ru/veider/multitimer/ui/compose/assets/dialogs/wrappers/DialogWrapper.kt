package ru.veider.multitimer.ui.compose.assets.dialogs.wrappers

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*

@Composable
fun DialogWrapper(
	modifier: Modifier = Modifier,
	cornerRadius: Dp = 24.dp,
	color: Color = MaterialTheme.colorScheme.surface,
	border: BorderStroke = BorderStroke(width = 0.dp, color = Color.Transparent),
	usePlatformDefaultWidth: Boolean = true,
	content: @Composable () -> Unit
) {
	AnimatedVisibility(
		visible = true,
		enter = fadeIn(animationSpec = tween(durationMillis = 1000)),
		exit = fadeOut(animationSpec = tween(durationMillis = 1000))
	) {
		Dialog(
			onDismissRequest = {},
			properties = DialogProperties(
				dismissOnClickOutside = false,
				dismissOnBackPress = false,
				usePlatformDefaultWidth = usePlatformDefaultWidth
			)
		) {
			Surface(
				modifier = modifier,
				shape = RoundedCornerShape(cornerRadius),
				color = color,
				border = border
			){
				content()
			}
		}
	}
}