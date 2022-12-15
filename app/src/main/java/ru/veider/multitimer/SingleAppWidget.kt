package ru.veider.multitimer

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import ru.veider.multitimer.const.toShortTime
import kotlin.math.max

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
                setImageViewBitmap(R.id.widgetProgressBody,
                                   getWidgetBitmap(context,
                                                   getWidgetSize(context, widgetId).toInt(),
                                                   1.0f
                                   )
                )
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
                                setImageViewBitmap(R.id.widgetProgressBody,
                                                   getWidgetBitmap(context,
                                                                   getWidgetSize(context, widgetId).toInt(),
                                                                   currentTime.toFloat() / maxTime
                                                   )
                                )
                                setTextViewText(R.id.widgetText, currentTime.toShortTime())
                            }
                            widgetManager.updateAppWidget(widgetId, views)
                        }
                    }
                    WidgetStatus.ALARM -> {
                        for (widgetId in widgetIds) {
                            views.apply {
                                setViewVisibility(R.id.widgetText, View.GONE)
                                setImageViewBitmap(R.id.widgetProgressBody,
                                                   getWidgetBitmap(context,
                                                                   getWidgetSize(context, widgetId).toInt(),
                                                                   1.0f
                                                   )
                                )
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
                                setImageViewBitmap(R.id.widgetProgressBody,
                                                   getWidgetBitmap(context,
                                                                   getWidgetSize(context, widgetId).toInt(),
                                                                   1.0f
                                                   )
                                )
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

    private fun getWidgetSize(context: Context, widgetId: Int): Float {
        val providerInfo = AppWidgetManager.getInstance(context).getAppWidgetInfo(widgetId)
        var widgetLandWidth = providerInfo.minWidth
        var widgetPortHeight = providerInfo.minHeight
        var widgetPortWidth = providerInfo.minWidth
        var widgetLandHeight = providerInfo.minHeight
        AppWidgetManager.getInstance(context).getAppWidgetOptions(widgetId)?.run {
            if (getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) > 0) {
                widgetPortWidth = getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
                widgetLandWidth = getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH)
                widgetLandHeight = getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
                widgetPortHeight = getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)
            }
        }
        val maxSize = max(max(widgetPortWidth, widgetLandWidth), max(widgetPortHeight, widgetLandHeight))

        return context.dip(maxSize).toFloat()
    }

    private fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun getWidgetBitmap(context: Context, size: Int, percentage: Float): Bitmap {
        val strokeWidth = context.resources.getInteger(R.integer.widget_stroke_width) / 100.0f * size


        val strokeBold = strokeWidth * 1.5f
        val arc = RectF().apply {
            set(strokeBold / 2, strokeBold / 2, size - strokeBold / 2, size - strokeBold / 2)
        }

//        val paintBold = Paint(Paint.FILTER_BITMAP_FLAG and Paint.DITHER_FLAG and Paint.ANTI_ALIAS_FLAG).apply {
//            setStrokeWidth(strokeBold)
//            style = Paint.Style.STROKE
//            strokeCap = Paint.Cap.ROUND
//            color = context.resources.getColor(R.color.color_on_primary, context.theme)
//        }
        val paintLight = Paint(Paint.FILTER_BITMAP_FLAG and Paint.DITHER_FLAG and Paint.ANTI_ALIAS_FLAG).apply {
            setStrokeWidth(strokeWidth)
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            color =
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                        context.resources.getColor(R.color.color_primary)
                    else
                        context.resources.getColor(R.color.color_primary, context.theme)
        }

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val startAngle = 270 - 360 * percentage
        canvas.drawArc(arc, startAngle, 360 * percentage, false, paintLight)

        return bitmap
    }

    override fun onEnabled(context: Context) {
        onReceive(context, Intent())// Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}