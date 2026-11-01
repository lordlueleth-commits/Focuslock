package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WhitelistedAppEntity
import com.example.ui.components.NeonFocusRing
import com.example.ui.theme.DangerRed
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

@Composable
fun ActiveLockScreen(
    remainingSeconds: Long,
    progress: Float,
    activeTasks: List<String>,
    completedTasks: Set<String>,
    blockedAttemptsCount: Int,
    allowedApps: List<WhitelistedAppEntity>,
    onToggleTask: (String) -> Unit,
    onEmergencyUnlockClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("active_lock_screen"),
        color = PitchBlack
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp)
        ) {
            // Top Shield Status Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = DarkSurfaceElevated,
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SESSION LOCKED",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = ElectricCyan,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Surface(
                        color = DarkSurfaceElevated,
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (blockedAttemptsCount > 0) DangerRed else DarkBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = if (blockedAttemptsCount > 0) DangerRed else ZenGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$blockedAttemptsCount Intercepted",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (blockedAttemptsCount > 0) DangerRed else TextSecondary
                            )
                        }
                    }
                }
            }

            // Neon Timer Ring
            item {
                Spacer(modifier = Modifier.height(16.dp))
                NeonFocusRing(
                    remainingSeconds = remainingSeconds,
                    progress = progress,
                    isSessionActive = true
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Active Tasks Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("active_tasks_card"),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = ZenGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Active Focus Mission",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Text(
                                text = "${completedTasks.size}/${activeTasks.size} Done",
                                style = MaterialTheme.typography.labelSmall,
                                color = FocusAmber,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (activeTasks.isEmpty()) {
                            Text(
                                text = "Single-task deep work session in progress.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMuted
                            )
                        } else {
                            activeTasks.forEach { task ->
                                val isDone = completedTasks.contains(task)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onToggleTask(task) }
                                        .padding(vertical = 8.dp, horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = if (isDone) "Completed" else "Pending",
                                        tint = if (isDone) ZenGreen else TextMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = task,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isDone) TextMuted else TextPrimary,
                                        textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None,
                                        fontWeight = if (isDone) FontWeight.Normal else FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Permitted Essential Tools Shelf
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Permitted Tools",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Whitelisted Only",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricCyan
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val whitelistedOnly = allowedApps.filter { it.isAllowed }
                        if (whitelistedOnly.isEmpty()) {
                            Text(
                                text = "No external apps permitted during this session.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        } else {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(whitelistedOnly) { app ->
                                    Surface(
                                        color = DarkSurfaceElevated,
                                        shape = RoundedCornerShape(12.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                launchWhitelistedApp(context, app.packageName)
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .background(DarkSurfaceVariant, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = app.appName.take(1).uppercase(),
                                                    color = ElectricCyan,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = app.appName,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Medium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.Launch,
                                                contentDescription = "Open",
                                                tint = TextMuted,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Strict Uncompromising Lock Notice
            item {
                Surface(
                    color = DarkSurfaceElevated,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = FocusAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Settings & Whitelist Locked. No modifications allowed until session expires.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Emergency Escape / Early Exit Button
            item {
                OutlinedButton(
                    onClick = onEmergencyUnlockClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("emergency_unlock_btn"),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Emergency,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Emergency Unlock / Bypass",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

private fun launchWhitelistedApp(context: Context, packageName: String) {
    try {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
        }
    } catch (_: Exception) {}
}
