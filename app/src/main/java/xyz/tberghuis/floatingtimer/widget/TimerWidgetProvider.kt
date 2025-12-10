package xyz.tberghuis.floatingtimer.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import xyz.tberghuis.floatingtimer.MainActivity
import xyz.tberghuis.floatingtimer.R

class TimerWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, "00:00", "Idle")
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            timeText: String,
            label: String
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_timer)
            views.setTextViewText(R.id.widget_timer_text, timeText)
            views.setTextViewText(R.id.widget_Timer_label, label)

            // Intent to open app
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_bg, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
        
        fun updateAllWidgets(context: Context, timeText: String, label: String) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, TimerWidgetProvider::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            for (appWidgetId in allWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId, timeText, label)
            }
        }
    }
}
