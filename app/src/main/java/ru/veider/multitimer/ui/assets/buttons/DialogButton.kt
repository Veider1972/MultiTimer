package ru.veider.multitimer.ui.assets.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.veider.multitimer.ui.theme.colorPrimary
import ru.veider.multitimer.ui.theme.paddingsDouble
import ru.veider.multitimer.ui.theme.paddingsSingle
import ru.veider.multitimer.ui.theme.textStyle_16_400

@Composable
fun DialogButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.padding(
            start = paddingsDouble,
            bottom = paddingsDouble,
            end = paddingsDouble
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = colorPrimary
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        contentPadding = PaddingValues(
            vertical = paddingsSingle,
            horizontal = paddingsSingle
        )
    ) {
        Text(text = text.uppercase(), style = textStyle_16_400)
    }
}