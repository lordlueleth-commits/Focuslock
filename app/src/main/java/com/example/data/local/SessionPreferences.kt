package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.EscapeMode

class SessionPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("blockit_session_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_ACTIVE = "is_session_active"
        private const val KEY_SESSION_ID = "current_session_id"
        private const val KEY_START_TIME = "session_start_time"
        private const val KEY_END_TIME = "session_end_time"
        private const val KEY_DURATION_MINUTES = "session_duration_minutes"
        private const val KEY_ESCAPE_MODE = "session_escape_mode"
        private const val KEY_MASTER_PIN = "master_pin"
        private const val KEY_TASKS = "session_tasks"
        private const val KEY_BLOCKED_COUNT = "session_blocked_count"
        private const val KEY_ALLOWED_PACKAGES = "allowed_packages"
        private const val KEY_OVERRIDE_CODE = "override_code"
        private const val KEY_LAST_BLOCKED_PKG = "last_blocked_pkg"
        private const val KEY_LAST_BLOCKED_TIME = "last_blocked_time"
    }

    var isSessionActive: Boolean
        get() = prefs.getBoolean(KEY_IS_ACTIVE, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_ACTIVE, value).apply()

    var currentSessionId: Long
        get() = prefs.getLong(KEY_SESSION_ID, -1L)
        set(value) = prefs.edit().putLong(KEY_SESSION_ID, value).apply()

    var sessionStartTime: Long
        get() = prefs.getLong(KEY_START_TIME, 0L)
        set(value) = prefs.edit().putLong(KEY_START_TIME, value).apply()

    var sessionEndTime: Long
        get() = prefs.getLong(KEY_END_TIME, 0L)
        set(value) = prefs.edit().putLong(KEY_END_TIME, value).apply()

    var sessionDurationMinutes: Int
        get() = prefs.getInt(KEY_DURATION_MINUTES, 25)
        set(value) = prefs.edit().putInt(KEY_DURATION_MINUTES, value).apply()

    var escapeMode: EscapeMode
        get() {
            val name = prefs.getString(KEY_ESCAPE_MODE, EscapeMode.LOGIC_PUZZLE.name)
            return try {
                EscapeMode.valueOf(name ?: EscapeMode.LOGIC_PUZZLE.name)
            } catch (e: Exception) {
                EscapeMode.LOGIC_PUZZLE
            }
        }
        set(value) = prefs.edit().putString(KEY_ESCAPE_MODE, value.name).apply()

    var masterPin: String
        get() = prefs.getString(KEY_MASTER_PIN, "1234") ?: "1234"
        set(value) = prefs.edit().putString(KEY_MASTER_PIN, value).apply()

    var currentTasks: Set<String>
        get() = prefs.getStringSet(KEY_TASKS, emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet(KEY_TASKS, value).apply()

    var blockedAttemptsCount: Int
        get() = prefs.getInt(KEY_BLOCKED_COUNT, 0)
        set(value) = prefs.edit().putInt(KEY_BLOCKED_COUNT, value).apply()

    var allowedPackages: Set<String>
        get() = prefs.getStringSet(KEY_ALLOWED_PACKAGES, defaultAllowedPackages) ?: defaultAllowedPackages
        set(value) = prefs.edit().putStringSet(KEY_ALLOWED_PACKAGES, value).apply()

    var emergencyOverrideCode: String
        get() = prefs.getString(KEY_OVERRIDE_CODE, "FOCUS-BREAK-9821") ?: "FOCUS-BREAK-9821"
        set(value) = prefs.edit().putString(KEY_OVERRIDE_CODE, value).apply()

    var lastBlockedPackage: String
        get() = prefs.getString(KEY_LAST_BLOCKED_PKG, "") ?: ""
        set(value) = prefs.edit().putString(KEY_LAST_BLOCKED_PKG, value).apply()

    var lastBlockedTime: Long
        get() = prefs.getLong(KEY_LAST_BLOCKED_TIME, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_BLOCKED_TIME, value).apply()

    fun incrementBlockedAttempts(pkg: String) {
        val count = blockedAttemptsCount + 1
        prefs.edit()
            .putInt(KEY_BLOCKED_COUNT, count)
            .putString(KEY_LAST_BLOCKED_PKG, pkg)
            .putLong(KEY_LAST_BLOCKED_TIME, System.currentTimeMillis())
            .apply()
    }

    fun startSession(
        sessionId: Long,
        durationMinutes: Int,
        tasks: Set<String>,
        mode: EscapeMode,
        allowedPkgs: Set<String>
    ) {
        val now = System.currentTimeMillis()
        val end = now + (durationMinutes * 60 * 1000L)
        prefs.edit()
            .putBoolean(KEY_IS_ACTIVE, true)
            .putLong(KEY_SESSION_ID, sessionId)
            .putLong(KEY_START_TIME, now)
            .putLong(KEY_END_TIME, end)
            .putInt(KEY_DURATION_MINUTES, durationMinutes)
            .putString(KEY_ESCAPE_MODE, mode.name)
            .putStringSet(KEY_TASKS, tasks)
            .putInt(KEY_BLOCKED_COUNT, 0)
            .putStringSet(KEY_ALLOWED_PACKAGES, allowedPkgs)
            .apply()
    }

    fun endSession() {
        prefs.edit()
            .putBoolean(KEY_IS_ACTIVE, false)
            .putLong(KEY_END_TIME, 0L)
            .apply()
    }

    val defaultAllowedPackages: Set<String>
        get() = setOf(
            "com.android.phone",
            "com.google.android.dialer",
            "com.samsung.android.dialer",
            "com.android.server.telecom",
            "com.android.incallui",
            "com.android.systemui",
            "com.google.android.calculator",
            "com.android.calculator2",
            "com.google.android.calendar",
            "com.android.calendar",
            "com.google.android.deskclock",
            "com.android.deskclock"
        )
}
