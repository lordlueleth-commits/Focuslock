package com.example.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.accessibility.AccessibilityEvent
import com.example.MainActivity
import com.example.data.local.SessionPreferences
import com.example.widget.BlockItAppWidgetProvider

class BlockItAccessibilityService : AccessibilityService() {

    private lateinit var preferences: SessionPreferences
    private var lastInterventionTime: Long = 0L
    private var lastIntervenedPkg: String = ""

    companion object {
        const val ACTION_APP_BLOCKED = "com.example.blockit.ACTION_APP_BLOCKED"
        const val EXTRA_BLOCKED_PKG = "extra_blocked_pkg"
    }

    override fun onCreate() {
        super.onCreate()
        preferences = SessionPreferences(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return
        }

        val pkgCharSequence = event.packageName ?: return
        val currentPkg = pkgCharSequence.toString()

        // Verify if session is currently active
        if (!preferences.isSessionActive) {
            return
        }

        // Do not block our own app or system UI / keyguard
        if (currentPkg == packageName || currentPkg == "android" || currentPkg.contains("systemui") || currentPkg.contains("keyguard")) {
            return
        }

        // Check against allowed packages
        val allowed = preferences.allowedPackages
        if (allowed.contains(currentPkg)) {
            return
        }

        // Check if package is a system dialer/emergency service
        if (isEmergencyOrDialer(currentPkg)) {
            return
        }

        // Throttle back-to-back trigger loops for same package within 800ms
        val now = System.currentTimeMillis()
        if (currentPkg == lastIntervenedPkg && now - lastInterventionTime < 800) {
            return
        }
        lastInterventionTime = now
        lastIntervenedPkg = currentPkg

        // Record blocked attempt
        preferences.incrementBlockedAttempts(currentPkg)
        triggerHapticWarning()

        // Immediately send user back to safety (HOME or redirect to BlockIt focus screen)
        performGlobalAction(GLOBAL_ACTION_HOME)

        // Bring BlockIt to the front with Intervention Notice
        val intent = Intent(this, MainActivity::class.java).apply {
            action = ACTION_APP_BLOCKED
            putExtra(EXTRA_BLOCKED_PKG, currentPkg)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)

        // Sync with home widget
        BlockItAppWidgetProvider.updateAllWidgets(this)
    }

    private fun isEmergencyOrDialer(pkg: String): Boolean {
        val lower = pkg.lowercase()
        return lower.contains("dialer") || lower.contains("phone") || lower.contains("telecom") || lower.contains("emergency")
    }

    private fun triggerHapticWarning() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(120)
            }
        } catch (_: Exception) {
            // Ignore if vibration permission or hardware unavailable
        }
    }

    override fun onInterrupt() {
        // Required callback for accessibility service
    }
}
