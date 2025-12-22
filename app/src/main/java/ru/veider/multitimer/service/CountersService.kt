package ru.veider.multitimer.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioAttributes.*
import android.media.AudioManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.ui.text.input.KeyboardType.Companion.Uri
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.net.toUri
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.android.ext.android.inject
import ru.veider.multitimer.MainActivity
import ru.veider.multitimer.R
import ru.veider.multitimer.SingleAppWidget
import ru.veider.multitimer.app
import ru.veider.multitimer.const.*
import ru.veider.multitimer.data.Counter
import ru.veider.multitimer.domain.entity.Preferences
import ru.veider.multitimer.utils.createAlarmNotificationChannel
import ru.veider.multitimer.utils.createSimpleNotificationChannel
import ru.veider.multitimer.utils.sendAlarmNotification
import ru.veider.multitimer.utils.sendTickNotification
import ru.veider.multitimer.utils.setWidget
import ru.veider.multitimer.viewmodel.MainViewModel
import java.util.*
import kotlin.collections.toSortedMap
import kotlin.math.ceil


class CountersService() : LifecycleService() {

    private var timers: Hashtable<Int, CountersService.CounterTimer> = Hashtable()
    private var alarmes: Hashtable<Int, CountersService.AlarmTimer> = Hashtable()
    private val viewModel: MainViewModel by inject()
    private val prefs: Preferences by inject()
    private val gson: Gson by inject()

    override fun onCreate() {
        super.onCreate()
        Log.d("Notification", "CountersService onCreate")
        setWidget(100, 100, SingleAppWidget.Companion.WidgetStatus.STOP.toString())
        createSimpleNotificationChannel(
            channelId = prefs.simpleChannelId.value
        )
        Log.d("SoundDebug", "alarmChannelId: ${prefs.alarmChannelId.value}")
        createAlarmNotificationChannel(
            uri = prefs.sound.value.uri.toUri(),
            channelId = prefs.alarmChannelId.value
        )
        setIdleMessage()
    }

    private fun setIdleMessage() {
        startForeground(
            -1, NotificationCompat.Builder(this, prefs.simpleChannelId.value)
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
                ON_RUN_CLICK -> {
                    getCounterFromBundle(intent)?.apply {
                        addTimer(this)
                        removeIdleMessage()
                    }
                }

                ON_PAUSE_CLICK -> {
                    getCounterFromBundle(intent)?.apply {
                        removeTimer(this)
                        if (timers.isEmpty)
                            NotificationManagerCompat.from(this@CountersService).cancel(prefs.simpleChannelNum.value)
                        removeIdleMessage()
                        if (timers.isEmpty && alarmes.isEmpty) stopSelf()
                    }
                }

                ON_STOP_CLICK -> {
                    getCounterFromBundle(intent)?.apply {
                        removeTimer(this)
                        removeAlarmed(this)
                        if (alarmes.isEmpty)
                            NotificationManagerCompat.from(this@CountersService).cancel(prefs.alarmChannelNum.value)
                        if (timers.isEmpty)
                            NotificationManagerCompat.from(this@CountersService).cancel(prefs.simpleChannelNum.value)

                        viewModel.timerFinish(id)
                        removeIdleMessage()
                        if (timers.isEmpty && alarmes.isEmpty) stopSelf()
                        setWidget(0, 0, SingleAppWidget.Companion.WidgetStatus.STOP.toString())
                    }
                }

                ON_ALARM_TIMER -> {
                    getCounterFromBundle(intent)?.apply {
                        removeTimer(this)
                        addAlarmed(this)
                        if (timers.isEmpty)
                            NotificationManagerCompat.from(this@CountersService).cancel(prefs.simpleChannelNum.value)
                        sendAlarmNotification(alarmes)
                        viewModel.timerAlarmed(id)
                    }
                }

                ON_START_SERVICE -> {
                    getCountersFromBundle(intent)?.apply {
                        var hasRunCounters = false
                        for (counter in this) {
                            when (counter.state) {
                                CounterState.RUN -> {
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
                                    } else
                                        onAlarmed(counter)
                                }

                                CounterState.ALARMED -> {
                                    hasRunCounters = true
                                    if (alarmes.containsKey(counter.id)) continue
                                    onAlarmed(counter)
                                }

                                else -> {}
                            }
                        }
                        if (!hasRunCounters) {
                            stopSelf()
                        }
                    }
                }

                ON_STOP_SERVICE -> {
                    stopSelf()
                }
            }
        }
        Log.d("Counter", "CountersService viewModel=$viewModel timerTick: ${viewModel.counters}")
        return START_NOT_STICKY
    }



    private fun getCounterFromBundle(intent: Intent?) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
            intent?.getSerializableExtra(COUNTER) as Counter?
        else
            intent?.getSerializableExtra(COUNTER, Counter::class.java)

    private fun getCountersFromBundle(intent: Intent?): List<Counter>? =
        intent?.getBundleExtra(COUNTERS)?.getString(COUNTERS_BUNDLE)?.let { gson.fromJson(it, object : TypeToken<List<Counter>>() {}.type) }


    private fun addAlarmed(counter: Counter) {
        if (!alarmes.containsKey(counter.id))
            alarmes[counter.id] = AlarmTimer(counter).also {
                it.start()
            }
    }

    private fun removeAlarmed(counter: Counter) {
        alarmes[counter.id]?.apply {
            this.cancel()
            alarmes.remove(counter.id)
        }
    }

    private fun addTimer(counter: Counter) {
        if (!timers.containsKey(counter.id))
            timers[counter.id] = CounterTimer(counter).also {
                it.start()
            }
    }

    private fun removeTimer(counter: Counter) {
        timers[counter.id]?.apply {
            this.cancel()
            timers.remove(counter.id)
        }
    }

    private fun runService(intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            ContextCompat.startForegroundService(this, intent)
        else
            startService(intent)
    }

    override fun onDestroy() {
        NotificationManagerCompat.from(this).cancelAll()
        super.onDestroy()
    }

    abstract inner class Timer(msec: Long, interval: Long) : CountDownTimer(msec, interval),
        Comparator<Counter> {
        override fun compare(counter0: Counter?, counter1: Counter?): Int =
            if (counter0 != null || counter1 != null)
                counter0!!.currentProgress - counter1!!.currentProgress
            else 0
    }

    inner class AlarmTimer(val counter: Counter) : Timer(600 * 1000L, 10 * 1000L) {
        val unlimited = prefs.unlimitedNotification.value
        var repeats = prefs.notificationLimits.value
        override fun onTick(millisUntilFinished: Long) {
            Log.d("AlarmTimer", repeats.toString())
            if (!unlimited && repeats > 0)
                sendAlarmNotification(alarmes)
            else
                onFinish()
            repeats = if (repeats > 0) repeats - 1 else 0
        }

        override fun onFinish() {
            if (unlimited)
                onAlarmed(counter)
            else
                removeAlarmed(counter)
        }

    }

    inner class CounterTimer(val counter: Counter) : Timer(counter.currentProgress * 1000L, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val currentProgress = ceil((millisUntilFinished.toDouble() / 1000)).toInt()
            counter.currentProgress = currentProgress
            viewModel.timerTick(counter.id, currentProgress)
            sendTickNotification(timers)
        }

        override fun onFinish() {
            if (timers.size == 1) NotificationManagerCompat.from(this@CountersService).cancel(1)
            onAlarmed(counter)
        }

    }

    fun onAlarmed(counter: Counter) {
        runService(Intent(this@CountersService, CountersService::class.java).apply {
            putExtra(EVENT, ON_ALARM_TIMER)
            putExtra(COUNTER, counter)
        })
    }




}