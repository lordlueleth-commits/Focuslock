package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.local.SessionPreferences
import com.example.data.repository.FocusRepository
import com.example.widget.BlockItAppWidgetProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class BlockItFocusService : Service() {

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private lateinit var preferences: SessionPreferences
    private lateinit var repository: FocusRepository
    private var tickerJob: Job? = null

    companion object {
        const val CHANNEL_ID = "blockit_focus_session_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "com.example.blockit.action.START_SERVICE"
        const val ACTION_STOP = "com.example.blockit.action.STOP_SERVICE"

        fun start(context: Context) {
            val intent = Intent(context, BlockItFocusService::class.java).apply {
                action = ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, BlockItFocusService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        preferences = SessionPreferences(this)
        repository = FocusRepository(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopCountdown()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
            else -> {
                startForeground(NOTIFICATION_ID, buildNotification("Focus Session Active", "Protecting your deep work"))
                startCountdown()
            }
        }
        return START_STICKY // Ensures service is restarted if terminated by system
    }

    private fun startCountdown() {
        tickerJob?.cancel()
        tickerJob = serviceScope.launch {
            while (isActive) {
                if (!preferences.isSessionActive) {
                    stopSelf()
                    break
                }
                val now = System.currentTimeMillis()
                val remainingMs = (preferences.sessionEndTime - now).coerceAtLeast(0L)
                if (remainingMs <= 0) {
                    // Session Completed!
                    repository.endSession(completed = true)
                    BlockItAppWidgetProvider.updateAllWidgets(this@BlockItFocusService)
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                    break
                }

                val minutes = (remainingMs / 1000) / 60
                val seconds = (remainingMs / 1000) % 60
                val timeFormatted = String.format("%02d:%02d", minutes, seconds)
                val topTask = preferences.currentTasks.firstOrNull() ?: "Deep Focus"
                val blockedCount = preferences.blockedAttemptsCount

                val notification = buildNotification(
                    title = "⚡ Focus Session: $timeFormatted",
                    text = "$topTask • $blockedCount distractions blocked"
                )
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(NOTIFICATION_ID, notification)

                // Update Widget periodically
                if (seconds % 10 == 0L) {
                    BlockItAppWidgetProvider.updateAllWidgets(this@BlockItFocusService)
                }

                delay(1000L)
            }
        }
    }

    private fun stopCountdown() {
        tickerJob?.cancel()
        tickerJob = null
    }

    private fun buildNotification(title: String, text: String): Notification {
        val launchIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_STOPWATCH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "BlockIt Focus Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows live remaining focus time and blocked distraction statistics"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
