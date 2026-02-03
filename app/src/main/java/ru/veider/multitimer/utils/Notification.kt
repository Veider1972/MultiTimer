package ru.veider.multitimer.utils

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager.getRingtone
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import org.koin.java.KoinJavaComponent.inject
import ru.veider.multitimer.BuildConfig
import ru.veider.multitimer.MainActivity
import ru.veider.multitimer.R
import ru.veider.multitimer.SingleAppWidget
import ru.veider.multitimer.domain.entity.Preferences
import ru.veider.multitimer.service.CountersService
import java.util.Hashtable

fun Context.sendTickNotification(
    timers: Hashtable<Int, CountersService.CounterTimer>
) {

    val prefs: Preferences by inject(Preferences::class.java)

    val notificationBuilder = Notification.Builder(this, prefs.simpleChannelId.value).apply {
        setCategory(Notification.CATEGORY_ALARM)
        setContentTitle(resources.getText(R.string.notification_title))

        val notificationStyle = Notification.InboxStyle()
        var minTime = Int.MAX_VALUE
        var setTime = Int.MAX_VALUE
        var i = 1
        for (timer in timers.toSortedMap()) {
            val title = timer.value.counter.title.ifEmpty { "Таймер ${i++}" }
            val message = timer.value.counter.currentProgress.toMinSec(this@sendTickNotification)
            with(notificationStyle) {
                setBigContentTitle(resources.getText(R.string.notification_title))
                addLine("$title: $message")
            }
            if (timer.value.counter.currentProgress < minTime) {
                with(timer.value.counter) {
                    minTime = currentProgress
                    setTime = maxProgress
                }
            }
        }
        setContentText("${resources.getText(R.string.notification_description)}${minTime.toMinSec(this@sendTickNotification)}")
        setWidget(minTime, setTime, SingleAppWidget.Companion.WidgetStatus.RUN.toString())
        style = notificationStyle
        setSmallIcon(R.drawable.clock)
        val intent = Intent(this@sendTickNotification, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(
                this@sendTickNotification,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        setContentIntent(pendingIntent)
    }
    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        NotificationManagerCompat.from(this).notify(prefs.simpleChannelNum.value, notificationBuilder.build())
    }

}


val android12 = Build.VERSION.SDK_INT in listOf(Build.VERSION_CODES.S, Build.VERSION_CODES.S_V2)

fun Context.sendAlarmNotification(
    alarmes: Hashtable<Int, CountersService.AlarmTimer>
) {

    val prefs: Preferences by inject(Preferences::class.java)

    if (android12) {
        val sound = prefs.sound.value.uri.toUri()
        val player = getRingtone(this, sound)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P)
            player.isLooping = false
        player.play()
    }

    val notificationBuilder = Notification.Builder(
        this,
        prefs.alarmChannelId.value
    ).apply {
        setCategory(Notification.CATEGORY_ALARM)
        setContentTitle(getAlarmTitle(alarmes.size))

        style = Notification.InboxStyle().also {
            var i = 1
            it.setBigContentTitle(getAlarmTitle(alarmes.size))

            for (timer in alarmes.toSortedMap()) {
                it.addLine(timer.value.counter.title.ifEmpty { "Таймер ${i++}" })
            }
        }

        setOngoing(true)
        setVisibility(Notification.VISIBILITY_PUBLIC)
        setAutoCancel(true)
        setSmallIcon(R.drawable.animated_timer)
        val intent = Intent(this@sendAlarmNotification, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(
                this@sendAlarmNotification, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        setContentIntent(pendingIntent)
    }
    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    )
        NotificationManagerCompat.from(this).notify(prefs.alarmChannelNum.value, notificationBuilder.build())
    setWidget(0, 0, SingleAppWidget.Companion.WidgetStatus.ALARM.toString())
}

fun Context.createAlarmNotificationChannel(
    uri: Uri,
    channelId: String
) {
    val manager = getSystemService(NotificationManager::class.java)
    val channel = NotificationChannel(
        channelId,
        resources.getString(R.string.alarm_channel_name),
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = resources.getString(R.string.alarm_channel_description)
        if (!android12) {
            setSound(
                uri,
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .build()
            )
        }

        enableVibration(true)
        vibrationPattern = arrayOf(500L, 500L, 500L, 500L, 500L, 500L, 500L, 500L, 500L).toLongArray()
        enableLights(true)
        lightColor = Color.WHITE
        lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        setBypassDnd(true)
    }
    manager.createNotificationChannel(channel)
}

fun Context.createSimpleNotificationChannel(
    channelId: String
) {
    val manager = getSystemService(NotificationManager::class.java)
    val channel = NotificationChannel(
        channelId,
        resources.getString(R.string.simple_channel_name),
        NotificationManager.IMPORTANCE_NONE
    ).apply {
        description = resources.getString(R.string.simple_channel_description)
        enableVibration(false)
        enableLights(false)
    }
    manager.createNotificationChannel(channel)
}

fun Context.deleteChannel(
    id: String
) {
    val notificationManager = NotificationManagerCompat.from(this)
    notificationManager.deleteNotificationChannel(id)
}