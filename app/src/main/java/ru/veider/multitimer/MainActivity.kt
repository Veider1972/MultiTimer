package ru.veider.multitimer

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ru.veider.multitimer.ui.compose.MainState
import ru.veider.multitimer.ui.compose.assets.SetSystemBarsContrast
import ru.veider.multitimer.ui.theme.MultiTimerTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MultiTimerTheme {
                SetSystemBarsContrast()
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    MainState()
                }
            }
        }
    }
}