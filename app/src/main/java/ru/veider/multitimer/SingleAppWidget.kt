package ru.veider.multitimer

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import ru.veider.multitimer.const.toShortTime

class SingleAppWidget : AppWidgetProvider() {

    companion object {
        enum class WidgetStatus {
            RUN, STOP, ALARM
        }

        const val WIDGET_STATUS = "WIDGET_STATUS"
        const val WIDGET_CURRENT_TIME = "WIDGET_CURRENT_TIME"
        const val WIDGET_MAX_TIME = "WIDGET_MAX_TIME"
        const val ACTION_WIDGET_UPDATE_STATUS = "ru.veider.multitimer.ACTION_WIDGET_UPDATE_STATUS"

        private const val STATE_PREFS = "single_app_widget_state"
        private const val KEY_STATUS = "status"
        private const val KEY_CURRENT_TIME = "currentTime"
        private const val KEY_MAX_TIME = "maxTime"

        private fun readState(context: Context): WidgetState {
            val prefs = context.getSharedPreferences(STATE_PREFS, Context.MODE_PRIVATE)
            val status = runCatching {
                WidgetStatus.valueOf(prefs.getString(KEY_STATUS, WidgetStatus.STOP.name)!!)
            }.getOrDefault(WidgetStatus.STOP)
            return WidgetState(
                status,
                prefs.getInt(KEY_CURRENT_TIME, 0),
                prefs.getInt(KEY_MAX_TIME, 0)
            )
        }

        private fun writeState(context: Context, state: WidgetState) {
            context.getSharedPreferences(STATE_PREFS, Context.MODE_PRIVATE).edit()
                .putString(KEY_STATUS, state.status.name)
                .putInt(KEY_CURRENT_TIME, state.currentTime)
                .putInt(KEY_MAX_TIME, state.maxTime)
                .apply()
        }
    }

    private data class WidgetState(val status: WidgetStatus, val currentTime: Int, val maxTime: Int)

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val state = readState(context)
        val views = buildViews(context, state)
        for (widgetId in appWidgetIds) {
            appWidgetManager.updateAppWidget(widgetId, views)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == ACTION_WIDGET_UPDATE_STATUS) {
            val state = WidgetState(
                runCatching { WidgetStatus.valueOf(intent.getStringExtra(WIDGET_STATUS)!!) }
                    .getOrDefault(WidgetStatus.STOP),
                intent.getIntExtra(WIDGET_CURRENT_TIME, 0),
                intent.getIntExtra(WIDGET_MAX_TIME, 0)
            )
            writeState(context, state)
            val widgetManager = AppWidgetManager.getInstance(context)
            val widgetIds = widgetManager.getAppWidgetIds(ComponentName(context, SingleAppWidget::class.java))
            val views = buildViews(context, state)
            for (widgetId in widgetIds) {
                widgetManager.updateAppWidget(widgetId, views)
            }
        }
        super.onReceive(context, intent)
    }

    private fun buildViews(context: Context, state: WidgetState): RemoteViews {
        val pendingIntent = getPendingIntent(context)
        return RemoteViews(context.packageName, R.layout.single_app_widget).apply {
            setOnClickPendingIntent(R.id.widgetText, pendingIntent)
            setOnClickPendingIntent(R.id.widgetProgressBody, pendingIntent)
            when (state.status) {
                WidgetStatus.RUN   -> {
                    setViewVisibility(R.id.widgetText, View.VISIBLE)
                    setViewVisibility(R.id.widgetBell, View.GONE)
                    setProgressBar(R.id.progress_bar, state.maxTime, state.currentTime, false)
                    setTextViewText(R.id.widgetText, state.currentTime.toShortTime())
                }
                WidgetStatus.ALARM -> {
                    setViewVisibility(R.id.widgetText, View.GONE)
                    setViewVisibility(R.id.widgetBell, View.VISIBLE)
                    setProgressBar(R.id.progress_bar, 100, 100, false)
                }
                WidgetStatus.STOP  -> {
                    setViewVisibility(R.id.widgetText, View.VISIBLE)
                    setViewVisibility(R.id.widgetBell, View.GONE)
                    setProgressBar(R.id.progress_bar, 100, 100, false)
                    setTextViewText(R.id.widgetText, 0.toShortTime())
                }
            }
        }
    }

    private fun getPendingIntent(context: Context): PendingIntent =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java),
                                          PendingIntent.FLAG_UPDATE_CURRENT
                ) else
                PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java),
                                          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

    override fun onEnabled(context: Context) {
        onReceive(context, Intent())
    }
}
