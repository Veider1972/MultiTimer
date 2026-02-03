package ru.veider.multitimer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.TypedArray
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.IconCompat
import ru.veider.multitimer.ui.screens.MainState
import ru.veider.multitimer.ui.assets.SetSystemBarsContrast
import ru.veider.multitimer.ui.screens.ruStore.RunCounter
import ru.veider.multitimer.ui.theme.MultiTimerTheme

class MainActivity : ComponentActivity() {

    val permissions =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            listOf(
                "android.permission.POST_NOTIFICATIONS",
                Manifest.permission.READ_MEDIA_AUDIO
            ).toTypedArray()
    else
        emptyList<String>().toTypedArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            MultiTimerTheme {
                if (
                    if (Build.VERSION.SDK_INT >= 32)
                        hasPermissions(this.applicationContext, permissions)
                    else
                        true
                ) {
                    MainScreen()
                } else
                    PermissionHandler(
                        permissions = permissions,
                        onPermissionsGranted = {
                            // Запускаем основное приложение
                            MainScreen()
                        }
                    )

            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable()
    fun MainScreen() {
        RunCounter()
        SetSystemBarsContrast()
        Scaffold(modifier = Modifier.fillMaxSize()) {
            MainState()
        }
    }

}

@Composable
fun PermissionHandler(
    permissions: Array<String>,
    onPermissionsGranted: @Composable () -> Unit
) {
    val context = LocalContext.current
    var showPermissionScreen by remember { mutableStateOf(false) }

    // Лончер для запроса разрешений
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->

        permissionsMap.entries.forEach {
            Log.d("Permissions", "${it.key} - ${it.value}")
        }

        val allGranted = permissionsMap.values.all { it }

        if (allGranted) {
            // Все разрешения получены
            //Toast.makeText(context, "All permissions granted!", Toast.LENGTH_SHORT).show()
            showPermissionScreen = false
        } else {
            // Некоторые разрешения не получены
//            val deniedPermissions = permissionsMap.filter { !it.value }.keys
//            Log.w("Permission", "Denied: $deniedPermissions")
            showPermissionRationaleDialog(context)
        }
    }

    // Проверяем разрешения при запуске
    LaunchedEffect(Unit) {
        if (permissions.isNotEmpty() && !hasPermissions(context, permissions)){
            showPermissionScreen = true
        }
    }

    if (showPermissionScreen) {
        PermissionRequestScreen(
            onRequestPermission = {
                    permissionLauncher.launch(permissions)
            },
            onSkip = {
                showPermissionScreen = false
            }
        )
    } else {
        onPermissionsGranted()
    }
}

@Composable
fun PermissionRequestScreen(
    onRequestPermission: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = null,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Разрешение на уведомления",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Для работы таймера в фоне и показа оповещений необходимо разрешение на уведомления.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRequestPermission,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Разрешить уведомления")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onSkip,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Пропустить")
        }
    }
}

private fun hasPermissions(context: Context, permissions: Array<String>): Boolean =
    permissions.all { permission ->
        ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

private fun showPermissionRationaleDialog(context: Context) {
    AlertDialog.Builder(context)
        .setTitle("Не все разрешения предоставлены")
        .setMessage("Без разрешений таймер не сможет полноценно работать")
        .setPositiveButton("Открыть настройки") { _, _ ->
            openAppSettings(context)
        }
        .setNegativeButton("Позже", null)
        .show()
}

private fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
}