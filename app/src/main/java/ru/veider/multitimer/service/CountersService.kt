package ru.veider.multitimer.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.CountDownTimer
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import ru.veider.multitimer.MultiTimer
import ru.veider.multitimer.R
import ru.veider.multitimer.const.*
import ru.veider.multitimer.data.Counter
import ru.veider.multitimer.const.CounterState
import ru.veider.multitimer.data.Counters
import ru.veider.multitimer.viewmodel.CountersViewModel
import ru.veider.multitimer.viewmodel.CountersViewModelFactory
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil

class CountersService : LifecycleAndViewStoreOwnerService() {

    private lateinit var alarmChannelName: String
    private lateinit var alarmChannelDescription: String
    private lateinit var simpleChannelName: String
    private lateinit var simpleChannelDescription: String
    private var timers: Hashtable<Int, CountersService.CounterTimer> = Hashtable()
    private var alarmes: Hashtable<Int, CountersService.AlarmTimer> = Hashtable()
    private lateinit var viewModel: CountersViewModel

    override fun onCreate() {
        super.onCreate()
        viewModel = ViewModelProvider(this.viewModelStore, CountersViewModelFactory.getInstance())[CountersViewModel::class.java]
        alarmChannelName = resources.getString(R.string.alarm_channel_name)
        alarmChannelDescription = resources.getString(R.string.alarm_channel_description)
        simpleChannelName = resources.getString(R.string.simple_channel_name)
        simpleChannelDescription = resources.getString(R.string.simple_channel_description)

        createSimpleNotificationChannel()
        createAlarmNotificationChannel()
        setIdleMessage()
//        checkCounters(viewModel.getCounters)
    }

    private fun setIdleMessage() {
        startForeground(-1, NotificationCompat.Builder(this, SIMPLE_CHANNEL_ID)
            .setContentText(resources.getString(R.string.notification_title))
            .build()
        )
    }

    private fun removeIdleMessage() {
        NotificationManagerCompat.from(this@CountersService).cancel(-1)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        viewModel.saveCounters()
        super.onTaskRemoved(rootIntent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.getStringExtra(EVENT)?.apply {
            when (this) {
                ON_RUN_CLICK     -> {
                    getCounterFromBundle(intent)?.apply {
                        addTimer(this)
                        removeIdleMessage()
                    }
                }
                ON_PAUSE_CLICK   -> {
                    getCounterFromBundle(intent)?.apply {
                        removeTimer(this)
                        NotificationManagerCompat.from(this@CountersService).cancel(id)
                        removeIdleMessage()
                        if (timers.size == 0 && alarmes.size == 0) stopSelf()
                    }
                }
                ON_STOP_CLICK    -> {
                    getCounterFromBundle(intent)?.apply {
                        removeTimer(this)
                        removeAlarmed(this)
                        NotificationManagerCompat.from(this@CountersService).cancel(id)
                        viewModel.timerFinish(id)
                        removeIdleMessage()
                        if (timers.size == 0 && alarmes.size == 0) stopSelf()
                    }
                }

                ON_ALARM_TIMER   -> {
                    getCounterFromBundle(intent)?.apply {
                        removeTimer(this)
                        addAlarmed(this)
                        //sendAlarmMessage(this)
                        viewModel.timerAlarmed(id)
                    }
                }
                ON_START_SERVICE -> {
                    getCountersFromBundle(intent)?.apply {
                        var hasRunCounters = false
                        for (counter in this) {
                            when (counter.state) {
                                CounterState.RUN     -> {
                                    hasRunCounters = true
                                    if (timers.containsKey(counter.id)) continue
                                    val currentTime = Date().time
                                    val startTime = counter.startTime
                                    val timePass = (currentTime - startTime) / 1000
                                    val setTime = counter.maxProgress
                                    if (timePass < setTime) {
                                        counter.currentProgress = (setTime - timePass).toInt()
                                        val timer = CounterTimer(counter).apply { start() }
                                        timers[counter.id] = timer
                                    } else {
                                        onAlarmed(counter)
                                    }
                                }
                                CounterState.ALARMED -> {
                                    hasRunCounters = true
                                    if (alarmes.containsKey(counter.id)) continue
                                    onAlarmed(counter)
                                }
                            }
                        }
                        if (!hasRunCounters) {
                            stopSelf()
                        }
                    }
                }
                ON_STOP_SERVICE  -> {
                    stopSelf()
                }
            }
        }
        return START_NOT_STICKY
    }

    private fun getCounterFromBundle(intent: Intent?) =
            intent?.
            getSerializableExtra(COUNTER) as Counter?

    private fun getCountersFromBundle(intent: Intent?) =
            intent?.
            getBundleExtra(COUNTERS)?.
            getSerializable(COUNTERS_BUNDLE) as ArrayList<Counter>?

    private fun checkCounters(counters: Counters) {

    }

    private fun addAlarmed(counter: Counter) {
        if (!alarmes.containsKey(counter.id))
            alarmes.put(counter.id, AlarmTimer(counter).also {
                it.start()
            })
    }

    private fun removeAlarmed(counter: Counter) {
        alarmes[counter.id]?.apply {
            this.cancel()
            alarmes.remove(counter.id)
        }
    }

    private fun addTimer(counter: Counter) {
        if (!timers.containsKey(counter.id))
            timers.put(counter.id, CounterTimer(counter).also {
                it.start()
            })
    }

    private fun removeTimer(counter: Counter) {
        timers[counter.id]?.apply {
            this.cancel()
            timers.remove(counter.id)
        }
    }

    private fun runService(intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            ContextCompat.startForegroundService(this, intent)
        else
            startService(intent)
    }

    override fun onDestroy() {
        NotificationManagerCompat.from(this).cancelAll()
        super.onDestroy()
    }

    private fun sendAlarmMessage(counter: Counter) {
        val message = if (counter.title.isEmpty()) resources.getString(R.string.notification_alarm_without_title) else String.format(
            resources.getString(R.string.notification_alarm_with_title), counter.title
        )
        sendAlarmNotification(counter.id, resources.getString(R.string.attention), message)
    }

    private fun sendSimpleNotification(channel: Int, title: String, message: String) {
        sendNotification(channel, title, message, SIMPLE_CHANNEL_ID)
    }

    private fun sendAlarmNotification(channel: Int, title: String, message: String) {
        sendNotification(channel, title, message, ALARM_CHANNEL_ID)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun sendNotification(
        channel: Int,
        title: String,
        message: String,
        notificationType: String
    ) {
        val notificationBuilder = NotificationCompat.Builder(this, notificationType).apply {
            setCategory(Notification.CATEGORY_ALARM)

            if (title.isNotEmpty()) setContentTitle(title)
            if (message.isNotEmpty()) setContentText(message)
            setStyle(NotificationCompat.BigTextStyle())

            if (notificationType == SIMPLE_CHANNEL_ID) {
                setSmallIcon(R.drawable.clock)
                priority = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationManager.IMPORTANCE_NONE
                } else {
                    NotificationCompat.PRIORITY_MIN
                }
            } else {
                val intent = Intent(this@CountersService, MultiTimer::class.java).apply {
                    putExtra(COUNTER_ID, channel)
                }
                val pendingIntent = PendingIntent.getActivity(this@CountersService, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                setContentIntent(pendingIntent)
                setOngoing(true)
                setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                setVibrate(vibroPattern)
                setLights(Color.RED, 1000, 500)
                setAutoCancel(true)
                setUsesChronometer(true)
                color = Color.RED
                setSmallIcon(R.drawable.animated_timer)
                priority = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationManager.IMPORTANCE_HIGH
                } else {
                    NotificationCompat.PRIORITY_MAX
                }
            }
        }
        with(NotificationManagerCompat.from(this)) {
            notify(channel, notificationBuilder.build())
        }
    }

    private fun createAlarmNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            createNotificationChannel(
                ALARM_CHANNEL_ID,
                alarmChannelName,
                alarmChannelDescription,
                NotificationManager.IMPORTANCE_HIGH
            )
        }
    }

    private fun createSimpleNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            createNotificationChannel(
                SIMPLE_CHANNEL_ID,
                simpleChannelName,
                simpleChannelDescription,
                NotificationManager.IMPORTANCE_NONE
            )
        }
    }

    private fun createNotificationChannel(
        channelId: String,
        channelName: String,
        description: String,
        importance: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                importance
            ).apply {
                setDescription(description)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                if (channelId == ALARM_CHANNEL_ID) {
                    enableVibration(true)
                    vibrationPattern = vibroPattern
                    enableLights(true)
                    lightColor = Color.WHITE
                } else {
                    enableVibration(false)
                    enableLights(false)
                }
            }
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
                createNotificationChannel(notificationChannel)
            }
        }
    }

    inner class AlarmTimer(val counter: Counter) : CountDownTimer(3600 * 1000L, 3 * 1000L) {
        override fun onTick(millisUntilFinished: Long) {
            sendAlarmMessage(counter)
        }

        override fun onFinish() {
            onAlarmed(counter)
        }
    }

    inner class CounterTimer(val counter: Counter) : CountDownTimer(counter.currentProgress * 1000L, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val currentProgress = ceil((millisUntilFinished.toDouble() / 1000)).toInt()
            viewModel.timerTick(counter.id, currentProgress)
            sendSimpleNotification(counter.id, counter.title,
                                   String.format(resources.getString(R.string.time_rest_pattern), currentProgress.toMinSec())
            )
        }

        override fun onFinish() {
            onAlarmed(counter)
        }
    }

    fun onAlarmed(counter: Counter) {
        val intent = Intent(this@CountersService, CountersService::class.java).apply {
            putExtra(EVENT, ON_ALARM_TIMER)
            putExtra(COUNTER, counter)
        }
        runService(intent)
    }


    fun Int.toMinSec(): String {
        val hours: Int = this / 3600
        val minutes: Int = (this - hours * 3600) / 60
        val seconds: Int = this - hours * 3600 - 60 * minutes
        return if (hours == 0)
            String.format(resources.getString(R.string.time_min_sec_pattern), minutes, seconds)
        else
            String.format(resources.getString(R.string.time_hours_min_sec_pattern), hours, minutes, seconds
            )
    }
}