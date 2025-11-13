package ru.veider.multitimer.ui.assets.dialogs.wrappers

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import org.koin.core.KoinApplication.Companion.init
import ru.veider.multitimer.const.alphaTransition
import java.lang.System.exit

@Composable
fun DialogWrapper(
	modifier: Modifier = Modifier,
	cornerRadius: Dp = 24.dp,
	color: Color = MaterialTheme.colorScheme.surface,
	border: BorderStroke = BorderStroke(width = 0.dp, color = Color.Transparent),
	usePlatformDefaultWidth: Boolean = true,
	show: Boolean = true,
	content: @Composable () -> Unit
) {

	val alpha by animateFloatAsState(if (show) 1f else 0f, tween(alphaTransition))

		Dialog(
			onDismissRequest = {},
			properties = DialogProperties(
				dismissOnClickOutside = false,
				dismissOnBackPress = false,
				usePlatformDefaultWidth = usePlatformDefaultWidth
			)
		) {
			Surface(
				modifier = modifier
					.alpha(alpha),
				shape = RoundedCornerShape(cornerRadius),
				color = color,
				border = border
			){
				content()
		}
	}
}