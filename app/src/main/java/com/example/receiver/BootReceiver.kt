package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.SessionPreferences
import com.example.data.repository.FocusRepository
import com.example.service.BlockItFocusService
import com.example.widget.BlockItAppWidgetProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        if (action == Intent.ACTION_BOOT_COMPLETED || action == "android.intent.action.QUICKBOOT_POWERON") {
            val prefs = SessionPreferences(context)
            if (prefs.isSessionActive) {
                val now = System.currentTimeMillis()
                if (prefs.sessionEndTime > now) {
                    // Session still has remaining time: resurrect focus service immediately
                    BlockItFocusService.start(context)
                    BlockItAppWidgetProvider.updateAllWidgets(context)
                } else {
                    // Session expired during power-off: record completion
                    val repository = FocusRepository(context)
                    CoroutineScope(Dispatchers.IO).launch {
                        repository.endSession(completed = true)
                        BlockItAppWidgetProvider.updateAllWidgets(context)
                    }
                }
            }
        }
    }
}
