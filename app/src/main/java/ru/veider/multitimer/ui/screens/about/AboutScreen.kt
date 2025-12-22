package ru.veider.multitimer.ui.screens.about

import android.R.attr.text
import android.R.attr.thickness
import android.R.id.message
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.veider.multitimer.BuildConfig
import ru.veider.multitimer.R
import ru.veider.multitimer.const.singlePadding
import ru.veider.multitimer.ui.theme.colorPrimary
import ru.veider.multitimer.ui.theme.colorPrimaryDark
import ru.veider.multitimer.ui.theme.textStyle_10_300
import ru.veider.multitimer.ui.theme.textStyle_11_500
import ru.veider.multitimer.ui.theme.textStyle_12_500
import ru.veider.multitimer.ui.theme.textStyle_13_400
import ru.veider.multitimer.ui.theme.textStyle_14_400
import ru.veider.multitimer.ui.theme.textStyle_14_500
import ru.veider.multitimer.ui.theme.textStyle_14_700
import ru.veider.multitimer.ui.theme.textStyle_17_700
import ru.veider.multitimer.ui.theme.textStyle_18_400
import androidx.core.net.toUri
import ru.veider.multitimer.ui.theme.textStyle_13_500
import ru.veider.multitimer.ui.theme.textStyle_15_500

@Composable
fun AboutScreen() {

    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()){
        Column(
            modifier = Modifier.padding(singlePadding)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.about_timer),
                    contentDescription = null,
                    modifier = Modifier.size(150.dp, 168.dp)
                )
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.app_name).uppercase(),
                        style = textStyle_17_700,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(
                        text = "${stringResource(R.string.about_version)} ${BuildConfig.VERSION_NAME}",
                        style = textStyle_15_500,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(
                        text = BuildConfig.BUILD_DATE.replaceFirstChar { it.uppercase() },
                        style = textStyle_13_500,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(
                        text = buildAnnotatedString {
                            pushStringAnnotation(tag = "URL", annotation = "mailto:${stringResource(R.string.my_email)}")
                            withStyle(style = SpanStyle(color = colorPrimary, textDecoration = TextDecoration.Underline)) {
                                append(stringResource(R.string.my_email))
                            }
                        },
                        style = textStyle_14_700,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier
                            .clickable{
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = "mailto:${context.applicationContext.getString(R.string.my_email)}".toUri()
                                }
                                if (intent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(intent)
                                }
                            }
                    )
                }
            }
            Title(
                text = stringResource(R.string.about_description_title)
            )
            Message(
                text = stringResource(R.string.about_description_1)
            )
            Message(
                text = stringResource(R.string.about_description_2)
            )
            Message(
                text = stringResource(R.string.about_description_3)
            )
            Message(
                text = stringResource(R.string.about_description_4)
            )
            Title(
                text = stringResource(R.string.about_guide_title)
            )
            Message(
                text = stringResource(R.string.about_guide_0)
            )
             Message(
                text = stringResource(R.string.about_guide_1)
            )
            Message(
                text = stringResource(R.string.about_guide_2)
            )
            Message(
                text = stringResource(R.string.about_guide_3)
            )
        }
        Text(
            text = "${stringResource(R.string.about_copyrights)}, ${BuildConfig.BUILD_YEAR}",
            style = textStyle_10_300,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = singlePadding)
        )
    }

}

@Composable
private fun Title(
    text: String
) {
    Column(
        modifier = Modifier.padding(top = singlePadding)
    ) {
        Text(
            text = text,
            style = textStyle_17_700
        )
        HorizontalDivider(
            thickness = 2.dp,
            color = colorPrimaryDark,
            modifier = Modifier.padding(bottom = 3.dp)
        )
    }
}

@Composable
private fun Message(
    text: String
) {
    Column {
        Text(
            text = text,
            style = textStyle_14_400,
            modifier = Modifier.padding(bottom = 3.dp)
        )
    }
}

@Preview(locale = "ru")
@Composable
private fun AboutScreenPreview() {
    AboutScreen()
}