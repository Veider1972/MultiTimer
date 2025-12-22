package ru.veider.multitimer.utils

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import ru.veider.multitimer.R
import ru.veider.multitimer.SingleAppWidget

fun Context.setWidget(currentTime: Int, maxTime: Int, status: String) {
    val updateIntent = Intent().apply {
        action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        putExtra(SingleAppWidget.WIDGET_CURRENT_TIME, currentTime)
        putExtra(SingleAppWidget.WIDGET_MAX_TIME, maxTime)
        putExtra(SingleAppWidget.WIDGET_STATUS, status)
    }
    sendBroadcast(updateIntent)
}
fun Context.getAlarmTitle(count: Int) =
    if (count == 1)
        resources.getText(R.string.notification_alarm_finished)
    else
        resources.getText(R.string.notification_alarm_multi_finished)
