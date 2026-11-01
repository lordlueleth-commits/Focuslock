package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.FocusAmber
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.ZenGreen
import com.example.ui.viewmodel.PermissionStatus

@Composable
fun SettingsScreen(
    currentPin: String,
    emergencyOverrideCode: String,
    permissions: PermissionStatus,
    onSavePin: (String) -> Unit,
    onRequestAccessibility: () -> Unit,
    onRequestUsageStats: () -> Unit,
    onRequestOverlay: () -> Unit,
    onRequestBatteryOpt: () -> Unit,
    onOpenHomeLauncherSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pinInput by remember { mutableStateOf(currentPin) }
    var pinSavedSuccess by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        color = PitchBlack
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 64.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(DarkSurfaceVariant, CircleShape)
                            .border(1.dp, DarkBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Shield & Security",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "Emergency overrides & focus enforcement",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Parental Verification / Master Passcode Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Master / Parental Passcode",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Used to unlock active sessions when Master PIN escape mode is selected.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = pinInput,
                                onValueChange = {
                                    if (it.length <= 6 && it.all { ch -> ch.isDigit() }) {
                                        pinInput = it
                                        pinSavedSuccess = false
                                    }
                                },
                                label = { Text("4-6 Digit Passcode") },
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("master_pin_input")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (pinInput.length >= 4) {
                                        onSavePin(pinInput)
                                        pinSavedSuccess = true
                                    }
                                },
                                enabled = pinInput.length >= 4,
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = PitchBlack),
                                modifier = Modifier.testTag("save_pin_btn")
                            ) {
                                Text("Save", fontWeight = FontWeight.Bold)
                            }
                        }

                        if (pinSavedSuccess) {
                            Text(
                                text = "Passcode updated successfully",
                                color = ZenGreen,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Block Phone's Home Screen (Launcher Option)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = FocusAmber,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Block Phone's Home Screen",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Set BlockIt as your default home app so you only see active tasks and permitted tools instead of distracting app grids.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedButton(
                            onClick = onOpenHomeLauncherSettings,
                            modifier = Modifier.fillMaxWidth().testTag("choose_home_app_btn"),
                            border = androidx.compose.foundation.BorderStroke(1.dp, FocusAmber),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = FocusAmber)
                        ) {
                            Text("Configure Default Home Launcher", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Home Screen Widget Information
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Widgets,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Home Screen Focus Widget",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Long-press your device's home screen, tap 'Widgets', and drag the 'BlockIt Focus' widget to your desktop. It displays live remaining focus time, active tasks, and blocked distractions in real-time.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Permissions Status Checklist
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Permission & Battery Health",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        PermissionRowItem(
                            title = "Accessibility Focus Interceptor",
                            subtitle = "Required to detect and deflect forbidden apps",
                            isGranted = permissions.accessibility,
                            onClick = onRequestAccessibility
                        )

                        PermissionRowItem(
                            title = "Usage Statistics Access",
                            subtitle = "Monitors app foreground state accurately",
                            isGranted = permissions.usageStats,
                            onClick = onRequestUsageStats
                        )

                        PermissionRowItem(
                            title = "Display Over Other Apps",
                            subtitle = "Presents full-screen lock and deflection modal",
                            isGranted = permissions.overlay,
                            onClick = onRequestOverlay
                        )

                        PermissionRowItem(
                            title = "Unrestricted Battery Mode",
                            subtitle = "Prevents OS from killing focus timer during sleep",
                            isGranted = permissions.batteryOptimization,
                            onClick = onRequestBatteryOpt
                        )
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Privacy & Offline Guarantee
            item {
                Surface(
                    color = DarkSurfaceElevated,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = ZenGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "100% Offline & Free Forever",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = ZenGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "BlockIt contains zero ads, zero trackers, and zero network calls. All session data, whitelists, and passcodes are stored exclusively on your device.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionRowItem(
    title: String,
    subtitle: String,
    isGranted: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isGranted) Icons.Default.CheckCircle else Icons.Default.Security,
            contentDescription = null,
            tint = if (isGranted) ZenGreen else FocusAmber,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                fontSize = 11.sp
            )
        }
        Text(
            text = if (isGranted) "Active" else "Enable",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (isGranted) ZenGreen else ElectricCyan
        )
    }
}
