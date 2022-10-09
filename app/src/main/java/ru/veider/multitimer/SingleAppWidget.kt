package ru.veider.multitimer

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.content.res.Resources
import android.graphics.*
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toRect
import ru.veider.multitimer.const.toShortTime
import ru.veider.multitimer.const.toTime

/**
 * Implementation of App Widget functionality.
 */
class SingleAppWidget : AppWidgetProvider() {

    companion object {
        enum class WidgetStatus {
            run, stop, alarm
        }

        const val WIDGET_STATUS = "WIDGET_STATUS"
        const val WIDGET_CURRENT_TIME = "WIDGET_CURRENT_TIME"
        const val WIDGET_MAX_TIME = "WIDGET_MAX_TIME"
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (widgetId in appWidgetIds) {
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, Intent(context, MultiTimer::class.java),
                                                                         PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val views = RemoteViews(context.packageName, R.layout.single_app_widget).apply {
                setOnClickPendingIntent(R.id.widgetText, pendingIntent)
                setImageViewBitmap(R.id.widgetProgressBody, getWidgetBitmap(context, getWidgetSize(context, widgetId).toInt(), 5.0f,
                                                                            1.0f
                )
                )
                setTextViewTextSize(R.id.widgetText, TypedValue.COMPLEX_UNIT_DIP, getTextSize(context, widgetId))
            }

            appWidgetManager.updateAppWidget(widgetId, views)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent?) {

        intent?.let {

            val strokeWidth = context.resources.getInteger(R.integer.widget_stroke_width).toFloat()

            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, Intent(context, MultiTimer::class.java),
                                                                         PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val widgetManager = AppWidgetManager.getInstance(context)
            val widgetIds = widgetManager.getAppWidgetIds(ComponentName(context, SingleAppWidget::class.java))
            val views = RemoteViews(context.packageName, R.layout.single_app_widget).apply {
                setOnClickPendingIntent(R.id.widgetProgressBody, pendingIntent)
            }

            if (it.hasExtra(WIDGET_STATUS)) {

                val status: WidgetStatus = WidgetStatus.valueOf(it.extras!!.getString(WIDGET_STATUS)!!)

                when (status) {
                    WidgetStatus.run   -> {
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
                                                   getWidgetBitmap(context, getWidgetSize(context, widgetId).toInt(), strokeWidth,
                                                                   currentTime.toFloat() / maxTime
                                                   )
                                )
                                setTextViewText(R.id.widgetText, currentTime.toShortTime())
                                setTextViewTextSize(R.id.widgetText, TypedValue.COMPLEX_UNIT_DIP, getTextSize(context, widgetId))

                            }
                            widgetManager.updateAppWidget(widgetId, views)
                        }
                    }
                    WidgetStatus.alarm -> {
                        for (widgetId in widgetIds) {
                            views.apply {
                                setViewVisibility(R.id.widgetText, View.GONE)
                                setImageViewBitmap(R.id.widgetProgressBody,
                                                   getWidgetBitmap(context, getWidgetSize(context, widgetId).toInt(), strokeWidth,
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
                                                   getWidgetBitmap(context, getWidgetSize(context, widgetId).toInt(), strokeWidth,
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


    private fun getWidgetSize(context: Context, widgetId: Int): Float {
        val isPortrait = context.resources.configuration.orientation == ORIENTATION_PORTRAIT
        val width = getWidgetWidth(context, isPortrait, widgetId)
        val height = getWidgetHeight(context, isPortrait, widgetId)
        val widthInPx = context.dip(width)
        val heightInPx = context.dip(height)

        return Math.min(widthInPx, heightInPx).toFloat()
    }

    private fun getWidgetWidth(context: Context, isPortrait: Boolean, widgetId: Int): Int =
            if (isPortrait) {
                getWidgetSizeInDp(context, widgetId, AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
            } else {
                getWidgetSizeInDp(context, widgetId, AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH)
            }

    private fun getWidgetHeight(context: Context, isPortrait: Boolean, widgetId: Int): Int =
            if (isPortrait) {
                getWidgetSizeInDp(context, widgetId, AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)
            } else {
                getWidgetSizeInDp(context, widgetId, AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
            }

    private fun getWidgetSizeInDp(context: Context, widgetId: Int, key: String): Int =
            AppWidgetManager.getInstance(context).getAppWidgetOptions(widgetId).getInt(key, 0)

    private fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()


    private fun getTextSize(context: Context, widgetId: Int): Float {
        return getWidgetSize(context, widgetId) / 12
    }

    private fun getWidgetBitmap(context: Context, size: Int, stroke: Float, percentage: Float): Bitmap {
        val strokeWidth = context.resources.getInteger(R.integer.widget_stroke_width) * 100 / size
        val paint = Paint(Paint.FILTER_BITMAP_FLAG and Paint.DITHER_FLAG and Paint.ANTI_ALIAS_FLAG).apply {
            setStrokeWidth(strokeWidth.toFloat())
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }
        val arc = RectF().apply {
            set((stroke / 2), (stroke / 2), size - (stroke / 2), size - (stroke / 2))
        }

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        paint.apply {
            style = Paint.Style.FILL
            setColor(context.resources.getColor(R.color.white, context.theme))
        }
        canvas.drawOval(arc, paint)

        paint.apply {
            style = Paint.Style.STROKE
            setColor(context.resources.getColor(R.color.color_primary, context.theme))
        }
        val startAngle = 270 - 360 * percentage
        canvas.drawArc(arc, startAngle, 360 * percentage, false, paint)

        return bitmap
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}