package ru.veider.multitimer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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
import ru.veider.multitimer.const.ALARM_CHANNEL_ID
import ru.veider.multitimer.const.SIMPLE_CHANNEL_ID
import ru.veider.multitimer.ui.screens.MainState
import ru.veider.multitimer.ui.assets.SetSystemBarsContrast
import ru.veider.multitimer.ui.theme.MultiTimerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val notificationManager = NotificationManagerCompat.from(this)
        if (notificationManager.getNotificationChannel(ALARM_CHANNEL_ID) != null)
            notificationManager.deleteNotificationChannel(ALARM_CHANNEL_ID)
        if (notificationManager.getNotificationChannel(SIMPLE_CHANNEL_ID) != null)
            notificationManager.deleteNotificationChannel(SIMPLE_CHANNEL_ID)

        enableEdgeToEdge()
        setContent {
            MultiTimerTheme {
                if (
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                        checkPermission("android.permission.POST_NOTIFICATIONS")
                    else
                        true
                ) {
                    MainScreen()
                } else
                    PermissionHandler(
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
        SetSystemBarsContrast()
        Scaffold(modifier = Modifier.fillMaxSize()) {
            MainState()
        }
    }

    fun checkPermission(permission: String) =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

}

@Composable
fun PermissionHandler(
    onPermissionsGranted: @Composable () -> Unit
) {
    val context = LocalContext.current
    var showPermissionScreen by remember { mutableStateOf(true) }

    // Определяем необходимые разрешения
    val requiredPermissions = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//             Android 13+ - нужно POST_NOTIFICATIONS
            listOf(
                "android.permission.POST_NOTIFICATIONS",
            )
        } else {
            // Для старых Android - разрешений не требуется
            listOf(
            )
        }
    }

    // Лончер для запроса разрешений
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            showPermissionScreen = false
        } else {
            // Можно показать объяснение или открыть настройки
            showPermissionRationaleDialog(context)
        }
    }

    // Проверяем разрешения при запуске
    LaunchedEffect(Unit) {
        if (requiredPermissions.isEmpty()) {
            // Для старых версий Android разрешения не нужны
            showPermissionScreen = false
        } else {
            val hasPermission = hasPermission(context, requiredPermissions[0])
            if (hasPermission) {
                showPermissionScreen = false
            } else {
                // Запрашиваем разрешение
                permissionLauncher.launch(requiredPermissions[0])
            }
        }
    }

    if (showPermissionScreen) {
        PermissionRequestScreen(
            permissions = requiredPermissions,
            onRequestPermission = {
                if (requiredPermissions.isNotEmpty()) {
                    permissionLauncher.launch(requiredPermissions[0])
                }
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
    permissions: List<String>,
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

private fun hasPermission(context: Context, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

private fun showPermissionRationaleDialog(context: Context) {
    AlertDialog.Builder(context)
        .setTitle("Уведомления отключены")
        .setMessage("Без разрешения на уведомления таймер не сможет:\n\n• Показывать оповещения\n• Работать в фоновом режиме\n• Вибрировать при завершении\n\nВы можете включить уведомления в настройках.")
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