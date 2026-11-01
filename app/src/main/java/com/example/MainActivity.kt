package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.BlockItAccessibilityService
import com.example.ui.screens.ActiveLockScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.BlockInterventionDialog
import com.example.ui.screens.EscapeDialog
import com.example.ui.screens.SessionSetupScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WhitelistManagerScreen
import com.example.ui.theme.BlockItTheme
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.FocusViewModel
import com.example.ui.viewmodel.MainNavTab

class MainActivity : ComponentActivity() {

    private val viewModel: FocusViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleIncomingIntent(intent)

        setContent {
            BlockItTheme {
                BlockItMainApp(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshSessionState()
        viewModel.checkPermissions()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent?.action == BlockItAccessibilityService.ACTION_APP_BLOCKED) {
            val blockedPkg = intent.getStringExtra(BlockItAccessibilityService.EXTRA_BLOCKED_PKG)
            if (!blockedPkg.isNullOrBlank()) {
                viewModel.setInterventionPkg(blockedPkg)
            }
        }
    }
}

@Composable
fun BlockItMainApp(viewModel: FocusViewModel) {
    val context = LocalContext.current

    val currentTab by viewModel.currentTab.collectAsState()
    val isSessionActive by viewModel.isSessionActive.collectAsState()
    val remainingSeconds by viewModel.remainingSeconds.collectAsState()
    val progress by viewModel.sessionProgress.collectAsState()
    val activeTasks by viewModel.activeTasks.collectAsState()
    val completedTasks by viewModel.completedTasks.collectAsState()
    val blockedAttemptsCount by viewModel.blockedAttemptsCount.collectAsState()
    val currentEscapeMode by viewModel.currentEscapeMode.collectAsState()
    val permissions by viewModel.permissions.collectAsState()

    val whitelistedApps by viewModel.whitelistedApps.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    val totalFocusMinutes by viewModel.totalFocusMinutes.collectAsState()
    val totalBlockedAttempts by viewModel.totalBlockedAttempts.collectAsState()
    val completedSessionCount by viewModel.completedSessionCount.collectAsState()
    val recentSessions by viewModel.recentSessions.collectAsState()

    val showEscapeDialog by viewModel.showEscapeDialog.collectAsState()
    val blockedInterventionPkg by viewModel.blockedInterventionPkg.collectAsState()

    // Prevent system back button from escaping active session
    BackHandler(enabled = isSessionActive) {
        if (showEscapeDialog) {
            viewModel.setEscapeDialogVisible(false)
        } else if (blockedInterventionPkg != null) {
            viewModel.dismissIntervention()
        } else if (currentTab != MainNavTab.FOCUS) {
            viewModel.selectTab(MainNavTab.FOCUS)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = PitchBlack,
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_navigation_bar"),
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == MainNavTab.FOCUS,
                    onClick = { viewModel.selectTab(MainNavTab.FOCUS) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainNavTab.FOCUS) Icons.Filled.Lock else Icons.Outlined.Lock,
                            contentDescription = "Focus"
                        )
                    },
                    label = { Text("Focus", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricCyan,
                        selectedTextColor = ElectricCyan,
                        indicatorColor = PitchBlack,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_tab_focus")
                )

                NavigationBarItem(
                    selected = currentTab == MainNavTab.WHITELIST,
                    onClick = { viewModel.selectTab(MainNavTab.WHITELIST) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainNavTab.WHITELIST) Icons.Filled.Apps else Icons.Outlined.Apps,
                            contentDescription = "Whitelist"
                        )
                    },
                    label = { Text("Whitelist", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricCyan,
                        selectedTextColor = ElectricCyan,
                        indicatorColor = PitchBlack,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_tab_whitelist")
                )

                NavigationBarItem(
                    selected = currentTab == MainNavTab.ANALYTICS,
                    onClick = { viewModel.selectTab(MainNavTab.ANALYTICS) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainNavTab.ANALYTICS) Icons.Filled.Insights else Icons.Outlined.Insights,
                            contentDescription = "Stats"
                        )
                    },
                    label = { Text("Stats", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricCyan,
                        selectedTextColor = ElectricCyan,
                        indicatorColor = PitchBlack,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_tab_analytics")
                )

                NavigationBarItem(
                    selected = currentTab == MainNavTab.SETTINGS,
                    onClick = { viewModel.selectTab(MainNavTab.SETTINGS) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainNavTab.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                            contentDescription = "Settings"
                        )
                    },
                    label = { Text("Settings", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricCyan,
                        selectedTextColor = ElectricCyan,
                        indicatorColor = PitchBlack,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_tab_settings")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                MainNavTab.FOCUS -> {
                    if (isSessionActive) {
                        ActiveLockScreen(
                            remainingSeconds = remainingSeconds,
                            progress = progress,
                            activeTasks = activeTasks,
                            completedTasks = completedTasks,
                            blockedAttemptsCount = blockedAttemptsCount,
                            allowedApps = whitelistedApps,
                            onToggleTask = { viewModel.toggleTaskCompleted(it) },
                            onEmergencyUnlockClick = { viewModel.setEscapeDialogVisible(true) }
                        )
                    } else {
                        SessionSetupScreen(
                            permissions = permissions,
                            onStartSession = { duration, tasks, mode ->
                                viewModel.startSession(duration, tasks, mode)
                            },
                            onRequestAccessibility = { viewModel.openAccessibilitySettings(context) },
                            onRequestUsageStats = { viewModel.openUsageAccessSettings(context) },
                            onRequestOverlay = { viewModel.openOverlaySettings(context) }
                        )
                    }
                }
                MainNavTab.WHITELIST -> {
                    WhitelistManagerScreen(
                        apps = whitelistedApps,
                        isSessionActive = isSessionActive,
                        searchQuery = searchQuery,
                        selectedCategory = selectedCategory,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onCategoryChange = { viewModel.setSelectedCategory(it) },
                        onToggleApp = { pkg, allowed -> viewModel.toggleAppAllowed(pkg, allowed) }
                    )
                }
                MainNavTab.ANALYTICS -> {
                    AnalyticsScreen(
                        totalFocusMinutes = totalFocusMinutes ?: 0,
                        totalBlockedAttempts = totalBlockedAttempts ?: 0,
                        completedSessionsCount = completedSessionCount,
                        recentSessions = recentSessions
                    )
                }
                MainNavTab.SETTINGS -> {
                    SettingsScreen(
                        currentPin = viewModel.preferences.masterPin,
                        emergencyOverrideCode = viewModel.preferences.emergencyOverrideCode,
                        permissions = permissions,
                        onSavePin = { viewModel.updateMasterPin(it) },
                        onRequestAccessibility = { viewModel.openAccessibilitySettings(context) },
                        onRequestUsageStats = { viewModel.openUsageAccessSettings(context) },
                        onRequestOverlay = { viewModel.openOverlaySettings(context) },
                        onRequestBatteryOpt = { viewModel.openBatteryOptimizationSettings(context) },
                        onOpenHomeLauncherSettings = { viewModel.openHomeLauncherPicker(context) }
                    )
                }
            }

            // Escape Dialog Modal
            if (showEscapeDialog) {
                EscapeDialog(
                    escapeMode = currentEscapeMode,
                    configuredPin = viewModel.preferences.masterPin,
                    emergencyOverrideCode = viewModel.preferences.emergencyOverrideCode,
                    onDismiss = { viewModel.setEscapeDialogVisible(false) },
                    onConfirmEscape = { reason ->
                        viewModel.endSession(completed = false, escapeMethod = reason)
                    }
                )
            }

            // Blocked Intervention Modal
            if (blockedInterventionPkg != null) {
                BlockInterventionDialog(
                    blockedPackage = blockedInterventionPkg ?: "Distracting App",
                    currentTask = activeTasks.firstOrNull(),
                    blockedCount = blockedAttemptsCount,
                    onDismiss = { viewModel.dismissIntervention() }
                )
            }
        }
    }
}
