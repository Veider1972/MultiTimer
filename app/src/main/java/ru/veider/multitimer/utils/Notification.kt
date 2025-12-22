package ru.veider.multitimer.utils

import android.Manifest
import android.R.attr.description
import android.R.attr.path
import android.R.attr.priority
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioAttributes.Builder
import android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION
import android.media.AudioAttributes.USAGE_ALARM
import android.media.RingtoneManager
import android.media.RingtoneManager.getRingtone
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.net.toUri
import org.koin.java.KoinJavaComponent.inject
import ru.veider.multitimer.MainActivity
import ru.veider.multitimer.R
import ru.veider.multitimer.SingleAppWidget
import ru.veider.multitimer.app
import ru.veider.multitimer.const.ALARM_CHANNEL_ID
import ru.veider.multitimer.const.ALARM_CHANNEL_NUM
import ru.veider.multitimer.const.SIMPLE_CHANNEL_ID
import ru.veider.multitimer.const.SIMPLE_CHANNEL_NUM
import ru.veider.multitimer.const.vibroPattern
import ru.veider.multitimer.data.preferences.PreferencesImpl
import ru.veider.multitimer.domain.entity.Preferences
import ru.veider.multitimer.service.CountersService
import java.util.Hashtable
import kotlin.text.ifEmpty

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


fun Context.sendAlarmNotification(
    alarmes: Hashtable<Int, CountersService.AlarmTimer>
) {

    val prefs: Preferences by inject(Preferences::class.java)

    val notificationBuilder = Notification.Builder(this, prefs.alarmChannelId.value).apply {
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
    val notificationManager = NotificationManagerCompat.from(this)
    Log.d("SoundDebug", "setSound: ${uri.toString()}")
    val audioAttributes = AudioAttributes.Builder()
//        .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
//        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()
    val notificationChannel = NotificationChannelCompat.Builder(
        channelId,
        NotificationManager.IMPORTANCE_HIGH
    )
        .setName(resources.getString(R.string.alarm_channel_name))
        .setDescription(resources.getString(R.string.alarm_channel_description))
        .setSound(uri, audioAttributes)
        .setVibrationEnabled(true)
        .setVibrationPattern(vibroPattern)
        .setLightsEnabled(true)
        .setLightColor(Color.WHITE)
        .build()
    notificationManager.createNotificationChannel(notificationChannel)
    Log.d("SoundDebug", "getSound: ${notificationManager.getNotificationChannel(channelId)?.sound.toString()}")
}

fun Context.createSimpleNotificationChannel(
    channelId: String
) {
    val notificationManager = NotificationManagerCompat.from(this)
    val notificationChannel = NotificationChannelCompat.Builder(
        channelId,
        NotificationManager.IMPORTANCE_NONE
    )
        .setName(resources.getString(R.string.simple_channel_name))
        .setDescription(resources.getString(R.string.simple_channel_description))
        .setVibrationEnabled(false)
        .setLightsEnabled(false)
        .build()
    notificationManager.createNotificationChannel(notificationChannel)
}

fun Context.deleteChannel(
    id: String
) {
    val notificationManager = NotificationManagerCompat.from(this)
    notificationManager.deleteNotificationChannel(id)
}