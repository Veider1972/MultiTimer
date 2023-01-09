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
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (widgetId in appWidgetIds) {
            val pendingIntent = getPendingIntent(context)
            val views = RemoteViews(context.packageName, R.layout.single_app_widget).apply {
                setOnClickPendingIntent(R.id.widgetText, pendingIntent)
            }

            appWidgetManager.updateAppWidget(widgetId, views)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent?) {

        intent?.let {

            val pendingIntent = getPendingIntent(context)
            val widgetManager = AppWidgetManager.getInstance(context)
            val widgetIds = widgetManager.getAppWidgetIds(ComponentName(context, SingleAppWidget::class.java))
            val views = RemoteViews(context.packageName, R.layout.single_app_widget).apply {
                setOnClickPendingIntent(R.id.widgetProgressBody, pendingIntent)
            }

            if (it.hasExtra(WIDGET_STATUS)) {

                when (WidgetStatus.valueOf(it.extras!!.getString(WIDGET_STATUS)!!)) {
                    WidgetStatus.RUN   -> {
                        for (widgetId in widgetIds) {
                            var currentTime = 0
                            var maxTime = 0
                            if (it.hasExtra(WIDGET_CURRENT_TIME)) {
                                currentTime = it.extras?.getInt(WIDGET_CURRENT_TIME) ?: 0
                            }
                            if (it.hasExtra(WIDGET_MAX_TIME)) {
                                maxTime = it.extras?.getInt(WIDGET_MAX_TIME) ?: 0
                            }

                            views.apply {
                                setViewVisibility(R.id.widgetText, View.VISIBLE)
                                setViewVisibility(R.id.widgetBell, View.GONE)
                                setProgressBar(R.id.progress_bar, maxTime, currentTime, false)
                                setTextViewText(R.id.widgetText, currentTime.toShortTime())
                            }
                            widgetManager.updateAppWidget(widgetId, views)
                        }
                    }
                    WidgetStatus.ALARM -> {
                        for (widgetId in widgetIds) {
                            views.apply {
                                setViewVisibility(R.id.widgetText, View.GONE)
                                setProgressBar(R.id.progress_bar, 100, 100, false)
                                setViewVisibility(R.id.widgetBell, View.VISIBLE)
                            }

                            widgetManager.updateAppWidget(widgetId, views)
                        }
                    }
                    else               -> {
                        for (widgetId in widgetIds) {
                            views.apply {
                                setViewVisibility(R.id.widgetText, View.VISIBLE)
                                setViewVisibility(R.id.widgetBell, View.GONE)
                                setProgressBar(R.id.progress_bar, 100, 100, false)
                                setTextViewText(R.id.widgetText, 0.toShortTime())
                            }
                            widgetManager.updateAppWidget(widgetId, views)
                        }
                    }

                }
            }
        }
        super.onReceive(context, intent)
    }

    private fun getPendingIntent(context: Context): PendingIntent =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                PendingIntent.getActivity(context, 0, Intent(context, MultiTimer::class.java),
                                          PendingIntent.FLAG_UPDATE_CURRENT
                ) else
                PendingIntent.getActivity(context, 0, Intent(context, MultiTimer::class.java),
                                          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

    override fun onEnabled(context: Context) {
        onReceive(context, Intent())
    }
}