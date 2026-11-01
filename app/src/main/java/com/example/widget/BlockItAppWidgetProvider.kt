package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.local.SessionPreferences

class BlockItAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_SESSION_UPDATE || intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            updateAllWidgets(context)
        }
    }

    companion object {
        const val ACTION_SESSION_UPDATE = "com.example.blockit.ACTION_SESSION_UPDATE"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val prefs = SessionPreferences(context)
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            val openIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            if (prefs.isSessionActive) {
                val now = System.currentTimeMillis()
                val remainingMs = (prefs.sessionEndTime - now).coerceAtLeast(0L)
                val minutes = (remainingMs / 1000) / 60
                val seconds = (remainingMs / 1000) % 60
                val formattedTime = String.format("%02d:%02d", minutes, seconds)
                val topTask = prefs.currentTasks.firstOrNull() ?: "Deep Focus"
                val blocked = prefs.blockedAttemptsCount

                views.setTextViewText(R.id.widget_status, "SESSION LOCK ACTIVE")
                views.setTextColor(R.id.widget_status, 0xFF00E5FF.toInt())
                views.setTextViewText(R.id.widget_time, formattedTime)
                views.setTextViewText(R.id.widget_task, "🎯 $topTask")
                views.setTextViewText(R.id.widget_blocked_count, "🛡️ $blocked Blocked")
            } else {
                views.setTextViewText(R.id.widget_status, "READY FOR FOCUS")
                views.setTextColor(R.id.widget_status, 0xFFB0B3C0.toInt())
                views.setTextViewText(R.id.widget_time, "--:--")
                views.setTextViewText(R.id.widget_task, "Tap to start deep work session")
                views.setTextViewText(R.id.widget_blocked_count, "0 Blocked")
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, BlockItAppWidgetProvider::class.java)
            val ids = appWidgetManager.getAppWidgetIds(componentName)
            if (ids.isNotEmpty()) {
                for (id in ids) {
                    updateAppWidget(context, appWidgetManager, id)
                }
            }
        }
    }
}
