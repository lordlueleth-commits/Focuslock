package com.example.ui.viewmodel

import android.app.AppOpsManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.os.Process
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BlockItApplication
import com.example.data.local.SessionPreferences
import com.example.data.model.EscapeMode
import com.example.data.model.FocusSessionEntity
import com.example.data.model.WhitelistedAppEntity
import com.example.service.BlockItFocusService
import com.example.widget.BlockItAppWidgetProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class MainNavTab {
    FOCUS,
    WHITELIST,
    ANALYTICS,
    SETTINGS
}

data class PermissionStatus(
    val accessibility: Boolean = false,
    val usageStats: Boolean = false,
    val overlay: Boolean = false,
    val notifications: Boolean = false,
    val batteryOptimization: Boolean = false
) {
    val allRequiredGranted: Boolean
        get() = accessibility
}

class FocusViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as BlockItApplication).repository
    val preferences: SessionPreferences = repository.preferences

    private val _currentTab = MutableStateFlow(MainNavTab.FOCUS)
    val currentTab: StateFlow<MainNavTab> = _currentTab.asStateFlow()

    private val _isSessionActive = MutableStateFlow(preferences.isSessionActive)
    val isSessionActive: StateFlow<Boolean> = _isSessionActive.asStateFlow()

    private val _remainingSeconds = MutableStateFlow(0L)
    val remainingSeconds: StateFlow<Long> = _remainingSeconds.asStateFlow()

    private val _sessionProgress = MutableStateFlow(0f)
    val sessionProgress: StateFlow<Float> = _sessionProgress.asStateFlow()

    private val _activeTasks = MutableStateFlow(preferences.currentTasks.toList())
    val activeTasks: StateFlow<List<String>> = _activeTasks.asStateFlow()

    private val _completedTasks = MutableStateFlow<Set<String>>(emptySet())
    val completedTasks: StateFlow<Set<String>> = _completedTasks.asStateFlow()

    private val _blockedAttemptsCount = MutableStateFlow(preferences.blockedAttemptsCount)
    val blockedAttemptsCount: StateFlow<Int> = _blockedAttemptsCount.asStateFlow()

    private val _currentEscapeMode = MutableStateFlow(preferences.escapeMode)
    val currentEscapeMode: StateFlow<EscapeMode> = _currentEscapeMode.asStateFlow()

    private val _permissions = MutableStateFlow(PermissionStatus())
    val permissions: StateFlow<PermissionStatus> = _permissions.asStateFlow()

    private val _showEscapeDialog = MutableStateFlow(false)
    val showEscapeDialog: StateFlow<Boolean> = _showEscapeDialog.asStateFlow()

    private val _blockedInterventionPkg = MutableStateFlow<String?>(null)
    val blockedInterventionPkg: StateFlow<String?> = _blockedInterventionPkg.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val whitelistedApps: StateFlow<List<WhitelistedAppEntity>> = repository.whitelistedApps
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalFocusMinutes: StateFlow<Int?> = repository.totalFocusMinutes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalBlockedAttempts: StateFlow<Int?> = repository.totalBlockedAttempts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completedSessionCount: StateFlow<Int> = repository.completedSessionCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val recentSessions: StateFlow<List<FocusSessionEntity>> = repository.recentSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var timerJob: Job? = null

    init {
        refreshSessionState()
        checkPermissions()
    }

    fun selectTab(tab: MainNavTab) {
        _currentTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setEscapeDialogVisible(visible: Boolean) {
        _showEscapeDialog.value = visible
    }

    fun setInterventionPkg(pkg: String?) {
        _blockedInterventionPkg.value = pkg
    }

    fun dismissIntervention() {
        _blockedInterventionPkg.value = null
    }

    fun refreshSessionState() {
        val active = preferences.isSessionActive
        _isSessionActive.value = active
        _activeTasks.value = preferences.currentTasks.toList()
        _blockedAttemptsCount.value = preferences.blockedAttemptsCount
        _currentEscapeMode.value = preferences.escapeMode

        if (active) {
            startLocalTimer()
        } else {
            timerJob?.cancel()
            _remainingSeconds.value = 0L
            _sessionProgress.value = 0f
        }
    }

    private fun startLocalTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive && preferences.isSessionActive) {
                val now = System.currentTimeMillis()
                val totalMs = preferences.sessionDurationMinutes * 60 * 1000L
                val remainingMs = (preferences.sessionEndTime - now).coerceAtLeast(0L)
                val remainingSec = remainingMs / 1000L

                _remainingSeconds.value = remainingSec
                _blockedAttemptsCount.value = preferences.blockedAttemptsCount

                if (totalMs > 0) {
                    val progress = (1f - (remainingMs.toFloat() / totalMs.toFloat())).coerceIn(0f, 1f)
                    _sessionProgress.value = progress
                }

                if (remainingSec <= 0) {
                    endSession(completed = true)
                    break
                }
                delay(1000L)
            }
        }
    }

    fun startSession(durationMinutes: Int, tasks: List<String>, escapeMode: EscapeMode) {
        viewModelScope.launch {
            repository.startSession(durationMinutes, tasks, escapeMode)
            _isSessionActive.value = true
            _activeTasks.value = tasks
            _completedTasks.value = emptySet()
            _currentEscapeMode.value = escapeMode
            _blockedAttemptsCount.value = 0
            _currentTab.value = MainNavTab.FOCUS

            BlockItFocusService.start(getApplication())
            BlockItAppWidgetProvider.updateAllWidgets(getApplication())
            startLocalTimer()
        }
    }

    fun endSession(completed: Boolean, escapeMethod: String? = null) {
        viewModelScope.launch {
            repository.endSession(completed, escapeMethod)
            _isSessionActive.value = false
            timerJob?.cancel()
            _remainingSeconds.value = 0L
            _sessionProgress.value = 0f
            _showEscapeDialog.value = false
            _blockedInterventionPkg.value = null

            BlockItFocusService.stop(getApplication())
            BlockItAppWidgetProvider.updateAllWidgets(getApplication())
        }
    }

    fun toggleTaskCompleted(task: String) {
        val current = _completedTasks.value.toMutableSet()
        if (current.contains(task)) {
            current.remove(task)
        } else {
            current.add(task)
        }
        _completedTasks.value = current
    }

    fun toggleAppAllowed(pkg: String, allowed: Boolean) {
        // Strict focus requirement: Whitelist cannot be modified during active session
        if (_isSessionActive.value) return
        viewModelScope.launch {
            repository.setAppAllowed(pkg, allowed)
        }
    }

    fun updateMasterPin(pin: String) {
        preferences.masterPin = pin
    }

    fun checkPermissions() {
        val context = getApplication<Application>()
        val hasAccessibility = isAccessibilityServiceEnabled(context)
        val hasUsage = hasUsageStatsPermission(context)
        val hasOverlay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else true
        val hasNotifications = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else true
        val isBatteryOpt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            pm?.isIgnoringBatteryOptimizations(context.packageName) ?: true
        } else true

        _permissions.value = PermissionStatus(
            accessibility = hasAccessibility,
            usageStats = hasUsage,
            overlay = hasOverlay,
            notifications = hasNotifications,
            batteryOptimization = isBatteryOpt
        )
    }

    private fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val expected = "${context.packageName}/com.example.service.BlockItAccessibilityService"
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabledServices.split(":").any { it.equals(expected, ignoreCase = true) }
    }

    private fun hasUsageStatsPermission(context: Context): Boolean {
        return try {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOps.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName
                )
            } else {
                @Suppress("DEPRECATION")
                appOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName
                )
            }
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            false
        }
    }

    fun openAccessibilitySettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    fun openUsageAccessSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    fun openOverlaySettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${context.packageName}")
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (_: Exception) {}
        }
    }

    fun openBatteryOptimizationSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val intent = Intent(
                    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                    Uri.parse("package:${context.packageName}")
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (_: Exception) {}
        }
    }

    fun openHomeLauncherPicker(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_HOME_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }
}
