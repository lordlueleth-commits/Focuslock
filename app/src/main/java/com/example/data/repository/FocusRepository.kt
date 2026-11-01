package com.example.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.example.data.local.AppDatabase
import com.example.data.local.SessionPreferences
import com.example.data.model.EscapeMode
import com.example.data.model.FocusSessionEntity
import com.example.data.model.WhitelistedAppEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray

class FocusRepository(
    private val context: Context,
    private val database: AppDatabase = AppDatabase.getDatabase(context),
    val preferences: SessionPreferences = SessionPreferences(context)
) {
    private val focusDao = database.focusDao()

    val allSessions: Flow<List<FocusSessionEntity>> = focusDao.getAllSessions()
    val recentSessions: Flow<List<FocusSessionEntity>> = focusDao.getRecentSessions()
    val totalFocusMinutes: Flow<Int?> = focusDao.getTotalCompletedFocusMinutes()
    val totalBlockedAttempts: Flow<Int?> = focusDao.getTotalBlockedAttempts()
    val completedSessionCount: Flow<Int> = focusDao.getCompletedSessionCount()
    val whitelistedApps: Flow<List<WhitelistedAppEntity>> = focusDao.getAllWhitelistedApps()

    suspend fun populateInstalledAppsIfEmpty() = withContext(Dispatchers.IO) {
        val existing = focusDao.getCurrentlyAllowedAppsSync()
        if (existing.isNotEmpty()) return@withContext

        val pm = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = pm.queryIntentActivities(mainIntent, 0)
        val defaultEssentials = setOf(
            "dialer", "phone", "telecom", "clock", "calculator", "calendar", "notes", "camera"
        )

        val appEntities = resolveInfos.mapNotNull { resolveInfo ->
            val pkg = resolveInfo.activityInfo.packageName
            if (pkg == context.packageName) return@mapNotNull null
            val appName = resolveInfo.loadLabel(pm).toString()
            val lower = pkg.lowercase() + " " + appName.lowercase()
            val isEssential = defaultEssentials.any { lower.contains(it) }

            val category = when {
                lower.contains("dialer") || lower.contains("phone") || lower.contains("call") -> "Communication"
                lower.contains("clock") || lower.contains("timer") || lower.contains("alarm") -> "Time"
                lower.contains("calc") || lower.contains("note") || lower.contains("calendar") -> "Productivity"
                lower.contains("cam") || lower.contains("photo") -> "Media"
                else -> "Utility"
            }

            WhitelistedAppEntity(
                packageName = pkg,
                appName = appName,
                isAllowed = isEssential, // essential tools are whitelisted by default
                isEssential = isEssential,
                category = category
            )
        }.distinctBy { it.packageName }

        if (appEntities.isNotEmpty()) {
            focusDao.insertApps(appEntities)
            val allowedPackages = appEntities.filter { it.isAllowed }.map { it.packageName }.toSet()
            preferences.allowedPackages = allowedPackages + preferences.defaultAllowedPackages
        } else {
            // Fallback default system tools
            val fallback = listOf(
                WhitelistedAppEntity("com.google.android.dialer", "Phone", isAllowed = true, isEssential = true, category = "Communication"),
                WhitelistedAppEntity("com.google.android.calculator", "Calculator", isAllowed = true, isEssential = true, category = "Productivity"),
                WhitelistedAppEntity("com.google.android.calendar", "Calendar", isAllowed = true, isEssential = true, category = "Productivity"),
                WhitelistedAppEntity("com.google.android.deskclock", "Clock", isAllowed = true, isEssential = true, category = "Time")
            )
            focusDao.insertApps(fallback)
            preferences.allowedPackages = fallback.map { it.packageName }.toSet() + preferences.defaultAllowedPackages
        }
    }

    suspend fun setAppAllowed(packageName: String, allowed: Boolean) = withContext(Dispatchers.IO) {
        // Only allow changing if session is NOT currently active
        if (preferences.isSessionActive) {
            return@withContext
        }
        focusDao.setAppAllowed(packageName, allowed)
        val currentAllowed = focusDao.getCurrentlyAllowedAppsSync().map { it.packageName }.toSet()
        preferences.allowedPackages = currentAllowed + preferences.defaultAllowedPackages
    }

    suspend fun startSession(
        durationMinutes: Int,
        tasks: List<String>,
        escapeMode: EscapeMode
    ): Long = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val tasksJson = JSONArray(tasks).toString()

        val allowedApps = focusDao.getCurrentlyAllowedAppsSync().map { it.packageName }.toSet()
        val allAllowed = allowedApps + preferences.defaultAllowedPackages

        val session = FocusSessionEntity(
            startTime = now,
            endTime = now + (durationMinutes * 60 * 1000L),
            targetDurationMinutes = durationMinutes,
            actualDurationMinutes = 0,
            isCompleted = false,
            tasksJson = tasksJson,
            blockedAttemptsCount = 0
        )
        val sessionId = focusDao.insertSession(session)
        preferences.startSession(
            sessionId = sessionId,
            durationMinutes = durationMinutes,
            tasks = tasks.toSet(),
            mode = escapeMode,
            allowedPkgs = allAllowed
        )
        sessionId
    }

    suspend fun endSession(completed: Boolean, escapeMethod: String? = null) = withContext(Dispatchers.IO) {
        val sessionId = preferences.currentSessionId
        val startTime = preferences.sessionStartTime
        val now = System.currentTimeMillis()
        val elapsedMinutes = (((now - startTime).coerceAtLeast(0L)) / 60000L).toInt()
        val blockedAttempts = preferences.blockedAttemptsCount

        if (sessionId > 0) {
            val updated = FocusSessionEntity(
                id = sessionId,
                startTime = startTime,
                endTime = now,
                targetDurationMinutes = preferences.sessionDurationMinutes,
                actualDurationMinutes = if (completed) preferences.sessionDurationMinutes else elapsedMinutes,
                isCompleted = completed,
                tasksJson = JSONArray(preferences.currentTasks.toList()).toString(),
                blockedAttemptsCount = blockedAttempts,
                escapeMethodUsed = escapeMethod
            )
            focusDao.updateSession(updated)
        }
        preferences.endSession()
    }

    fun recordBlockedAttempt(pkg: String) {
        preferences.incrementBlockedAttempts(pkg)
    }
}
