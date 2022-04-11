package ru.veider.multitimer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.veider.multitimer.MultiTimer
import ru.veider.multitimer.R
import ru.veider.multitimer.const.*
import ru.veider.multitimer.data.Counter
import ru.veider.multitimer.data.CounterState
import ru.veider.multitimer.data.Counters
import ru.veider.multitimer.viewmodel.CountersViewModel
import ru.veider.multitimer.viewmodel.CountersViewModelFactory
import java.util.*

class CountersService : LifecycleAndViewStoreOwnerService() {


    private lateinit var alarmChannelName: String
    private lateinit var alarmChannelDescription: String
    private lateinit var simpleChannelName: String
    private lateinit var simpleChannelDescription: String
    private val vibroPattern =
            arrayOf(500L, 500L, 500L, 500L, 500L, 500L, 500L, 500L, 500L).toLongArray()


    private var timers: Hashtable<Int, CountersService.CounterTimer> = Hashtable()
    private var alarmes: Hashtable<Int, CountersService.AlarmTimer> = Hashtable()
    private lateinit var viewModel: CountersViewModel

    override fun onCreate() {
        alarmChannelName = resources.getString(R.string.alarm_channel_name)
        alarmChannelDescription = resources.getString(R.string.alarm_channel_description)
        simpleChannelName = resources.getString(R.string.simple_channel_name)
        simpleChannelDescription = resources.getString(R.string.simple_channel_description)
        super.onCreate()
//        sendSimpleNotification(resources.getString(R.string.notification_timer_status), "")

        viewModel = ViewModelProvider(
            this.viewModelStore,
            CountersViewModelFactory.getInstance()
        )[CountersViewModel::class.java]
        val observer = Observer<Counter> { counter -> counterModeChanged(counter) }
        viewModel.serviceCounter().observe(this, observer)
        val counters = viewModel.getCounters
        checkCounters(counters)
        createSimpleNotificationChannel()
        createAlarmNotificationChannel()
        setIdleMessage()
    }

    fun setIdleMessage() {
        startForeground(-1, NotificationCompat.Builder(this, SIMPLE_CHANNEL_ID)
            .setContentTitle(resources.getString(R.string.notification_title))
            .build()
        )
    }

    fun removeIdleMessage() {
        NotificationManagerCompat.from(this@CountersService).cancel(-1)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        viewModel.saveCounters()
        super.onTaskRemoved(rootIntent)
    }

    private fun counterModeChanged(counter: Counter?) {
        counter?.apply {
            val intent = Intent(this@CountersService, CountersService::class.java).apply {
                putExtra(EVENT, EVENT_BUTTON)
                putExtra(COUNTER, Bundle().apply {
                    putParcelable(COUNTER, counter)
                })
            }
            runService(intent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.getStringExtra(EVENT)?.apply {
            when (this) {
                EVENT_BUTTON -> {
                    getCounterFromBundle(intent)?.apply {
                        when (state) {
                            CounterState.RUN      -> {
                                addTimer(this)
                                removeIdleMessage()
                            }
                            CounterState.PAUSED   -> {
                                removeTimer(this)
                                NotificationManagerCompat.from(this@CountersService).cancel(id)
                                viewModel.timerPause(id)
                                removeIdleMessage()
                            }
                            CounterState.FINISHED -> {
                                removeTimer(this)
                                removeAlarmed(this)
                                NotificationManagerCompat.from(this@CountersService).cancel(id)
                                viewModel.timerFinish(id)
                                removeIdleMessage()
                            }
                        }
                    }
                }
                EVENT_TIMER  -> {
                    getCounterFromBundle(intent)?.apply {
                        removeTimer(this)
                        addAlarmed(this)
                        //sendAlarmMessage(this)
                        viewModel.timerAlarmed(id)
                    }
                }
            }
        }
        if (timers.size == 0 && alarmes.size == 0) setIdleMessage()
        return START_STICKY
    }

    private fun getCounterFromBundle(intent: Intent?) = intent?.getBundleExtra(COUNTER)?.getParcelable(COUNTER) as Counter?

    private fun addAlarmed(counter: Counter) {
        alarmes[counter.id]?.let {} ?: AlarmTimer(counter).apply {
            this.start()
            alarmes[counter.id] = this
        }
    }

    private fun removeAlarmed(counter: Counter) {
        alarmes[counter.id]?.apply {
            this.cancel()
            alarmes.remove(counter.id)
        }
    }

    private fun addTimer(counter: Counter) {
        timers[counter.id]?.let {} ?: CounterTimer(counter).apply {
            this.start()
            timers[counter.id] = this
        }
    }

    private fun removeTimer(counter: Counter) {
        timers[counter.id]?.apply {
            this.cancel()
            timers.remove(counter.id)
        }
//
//        val timer: CounterTimer? = timers[id]
//        if (timer != null) {
//            timer.cancel()
//            timers.remove(id)
//        }
    }

    private fun runService(intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            ContextCompat.startForegroundService(this, intent)
        else
            startService(intent)
    }

    private fun checkCounters(counters: Counters) {
        for (counter in counters) {
            when (counter.state) {
                CounterState.RUN     -> {
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
                    onAlarmed(counter)
                }
            }
        }
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

    inner class AlarmTimer(val counter: Counter) : CountDownTimer(10 * 1000L, 3 * 1000L) {
        override fun onTick(millisUntilFinished: Long) {
            sendAlarmMessage(counter)
        }

        override fun onFinish() {
            onAlarmed(counter)
        }
    }

    inner class CounterTimer(val counter: Counter) : CountDownTimer(counter.currentProgress * 1000L, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val currentProgress = Math.ceil((millisUntilFinished.toDouble() / 1000)).toInt()
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
            putExtra(EVENT, EVENT_TIMER)
            putExtra(COUNTER, Bundle().apply {
                putParcelable(COUNTER, counter)
            })
        }
        runService(intent)
    }

    fun Int.toMinSec(): String {
        val minutes: Int = this / 60
        val seconds: Int = this - 60 * minutes
        return String.format(resources.getString(R.string.time_min_sec_pattern), minutes, seconds)
    }
}