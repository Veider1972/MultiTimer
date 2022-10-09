package ru.veider.multitimer.service

import android.R.attr.data
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.CountDownTimer
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import ru.veider.multitimer.MultiTimer
import ru.veider.multitimer.R
import ru.veider.multitimer.SingleAppWidget
import ru.veider.multitimer.const.*
import ru.veider.multitimer.data.Counter
import ru.veider.multitimer.viewmodel.CountersViewModel
import ru.veider.multitimer.viewmodel.CountersViewModelFactory
import java.util.*
import kotlin.math.ceil


class CountersService : LifecycleService(),
    ViewModelStoreOwner, HasDefaultViewModelProviderFactory {

    private lateinit var alarmChannelName: String
    private lateinit var alarmChannelDescription: String
    private lateinit var simpleChannelName: String
    private lateinit var simpleChannelDescription: String
    private var timers: Hashtable<Int, CountersService.CounterTimer> = Hashtable()
    private var alarmes: Hashtable<Int, CountersService.AlarmTimer> = Hashtable()
    private lateinit var viewModel: CountersViewModel

    val mViewModelStore = ViewModelStore()
    private var mFactory: ViewModelProvider.Factory? = null
    override fun getViewModelStore(): ViewModelStore {
        return mViewModelStore
    }

    override fun onCreate() {
        super.onCreate()
        setWidget(100,100,SingleAppWidget.Companion.WidgetStatus.stop.toString())
        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (source.lifecycle.currentState == Lifecycle.State.DESTROYED) {
                    mViewModelStore.clear()
                    source.lifecycle.removeObserver(this)
                }
            }
        })
        viewModel = ViewModelProvider(this.viewModelStore, CountersViewModelFactory.getInstance())[CountersViewModel::class.java]
        alarmChannelName = resources.getString(R.string.alarm_channel_name)
        alarmChannelDescription = resources.getString(R.string.alarm_channel_description)
        simpleChannelName = resources.getString(R.string.simple_channel_name)
        simpleChannelDescription = resources.getString(R.string.simple_channel_description)

        createSimpleNotificationChannel()
        createAlarmNotificationChannel()
        setIdleMessage()
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
                        if (timers.size == 0)
                            NotificationManagerCompat.from(this@CountersService).cancel(SIMPLE_CHANNEL_NUM)
                        removeIdleMessage()
                        if (timers.size == 0 && alarmes.size == 0) stopSelf()
                    }
                }
                ON_STOP_CLICK    -> {
                    getCounterFromBundle(intent)?.apply {
                        removeTimer(this)
                        removeAlarmed(this)
                        if (alarmes.size==0)
                            NotificationManagerCompat.from(this@CountersService).cancel(ALARM_CHANNEL_NUM)
                        if (timers.size == 0)
                            NotificationManagerCompat.from(this@CountersService).cancel(SIMPLE_CHANNEL_NUM)

                        viewModel.timerFinish(id)
                        removeIdleMessage()
                        if (timers.size == 0 && alarmes.size == 0) stopSelf()
                        setWidget(0,0,SingleAppWidget.Companion.WidgetStatus.stop.toString())
                    }
                }

                ON_ALARM_TIMER   -> {
                    getCounterFromBundle(intent)?.apply {
                        removeTimer(this)
                        addAlarmed(this)
                        if (timers.size == 0)
                            NotificationManagerCompat.from(this@CountersService).cancel(SIMPLE_CHANNEL_NUM)
                        sendAlarmNotification()
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
                                else                 -> {}
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

    private fun setWidget(currentTime: Int, maxTime: Int, status:String){
        val updateIntent = Intent().apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(SingleAppWidget.WIDGET_CURRENT_TIME, currentTime)
            putExtra(SingleAppWidget.WIDGET_MAX_TIME, maxTime)
            putExtra(SingleAppWidget.WIDGET_STATUS,status)
        }
        sendBroadcast(updateIntent)
    }

    private fun getCounterFromBundle(intent: Intent?) =
            intent?.getSerializableExtra(COUNTER) as Counter?

    private fun getCountersFromBundle(intent: Intent?) =
            intent?.getBundleExtra(COUNTERS)?.getSerializable(COUNTERS_BUNDLE) as ArrayList<Counter>?

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

    private fun sendTickNotification() {
        val notificationBuilder = NotificationCompat.Builder(this, SIMPLE_CHANNEL_ID).apply {
            setCategory(Notification.CATEGORY_ALARM)
            setContentTitle(resources.getText(R.string.notification_title))

            val notificationStyle = NotificationCompat.InboxStyle()
            var minTime = Int.MAX_VALUE
            var setTime = Int.MAX_VALUE
            var i = 1
            for (timer in timers.toSortedMap()) {
                val title = if (timer.value.counter.title.isEmpty()) "Таймер ${i}" else timer.value.counter.title
                val message = timer.value.counter.currentProgress.toMinSec()
                notificationStyle.addLine("${title}: ${message}")
                notificationStyle.setBigContentTitle(resources.getText(R.string.notification_title))
                i++
                if (timer.value.counter.currentProgress < minTime) {
                    minTime = timer.value.counter.currentProgress
                    setTime = timer.value.counter.maxProgress
                }
            }
            setContentText("${resources.getText(R.string.notification_description)}${minTime.toMinSec()}")
            setWidget(minTime,setTime,SingleAppWidget.Companion.WidgetStatus.run.toString())

            setStyle(notificationStyle)

            setSmallIcon(R.drawable.clock)
            priority = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager.IMPORTANCE_NONE
            } else {
                NotificationCompat.PRIORITY_MIN
            }
            val intent = Intent(this@CountersService, MultiTimer::class.java)
            val pendingIntent = PendingIntent.getActivity(this@CountersService, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            setContentIntent(pendingIntent)
        }
        with(NotificationManagerCompat.from(this)) {
            notify(SIMPLE_CHANNEL_NUM, notificationBuilder.build())
        }
    }


    private fun sendAlarmNotification() {
        val notificationBuilder = NotificationCompat.Builder(this, ALARM_CHANNEL_ID).apply {
            setCategory(Notification.CATEGORY_ALARM)
            setContentTitle(if (alarmes.size == 1)
                                resources.getText(R.string.notification_alarm_finished)
                            else
                                resources.getText(R.string.notification_alarm_multi_finished)
            )

            setStyle(NotificationCompat.InboxStyle().also {
                var i = 1
                for (timer in alarmes.toSortedMap()) {
                    it.addLine(if (timer.value.counter.title.isEmpty()) "Таймер ${i}" else timer.value.counter.title)
                    i++
                }
            })
            setOngoing(true)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            setVibrate(vibroPattern)
            setLights(Color.RED, 1000, 500)
            setAutoCancel(true)
            color = Color.RED
            setSmallIcon(R.drawable.animated_timer)
            priority = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager.IMPORTANCE_HIGH
            } else {
                NotificationCompat.PRIORITY_MAX
            }
            val intent = Intent(this@CountersService, MultiTimer::class.java)
            val pendingIntent = PendingIntent.getActivity(this@CountersService, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            setContentIntent(pendingIntent)
        }
        with(NotificationManagerCompat.from(this))
        {
            notify(ALARM_CHANNEL_NUM, notificationBuilder.build())
        }
        setWidget(0,0,SingleAppWidget.Companion.WidgetStatus.alarm.toString())

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

    abstract inner class Timer(millisInFuture:Long, countDownInterval:Long): CountDownTimer(millisInFuture, countDownInterval), Comparator<Counter>{
        override fun compare(counter0: Counter?, counter1: Counter?): Int {
            if (counter0!=null || counter1!=null)
                return counter0!!.currentProgress-counter1!!.currentProgress
            return 0
        }

    }

    inner class AlarmTimer(val counter: Counter) : Timer(600 * 1000L, 10 * 1000L) {
        override fun onTick(millisUntilFinished: Long) {
            sendAlarmNotification()
        }

        override fun onFinish() {
            onAlarmed(counter)
        }

    }

    inner class CounterTimer(val counter: Counter) : Timer(counter.currentProgress * 1000L, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val currentProgress = ceil((millisUntilFinished.toDouble() / 1000)).toInt()
            counter.currentProgress = currentProgress
            viewModel.timerTick(counter.id, currentProgress)
            sendTickNotification()
        }

        override fun onFinish() {
            if (timers.size == 1) NotificationManagerCompat.from(this@CountersService).cancel(1)
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

    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory {
        return mFactory ?: ViewModelProvider.AndroidViewModelFactory(application).also {
            mFactory = it
        }
    }

}