package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FocusSessionEntity
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AnalyticsScreen(
    totalFocusMinutes: Int,
    totalBlockedAttempts: Int,
    completedSessionsCount: Int,
    recentSessions: List<FocusSessionEntity>,
    modifier: Modifier = Modifier
) {
    val totalHours = totalFocusMinutes / 60
    val remMinutes = totalFocusMinutes % 60
    val timeFormatted = if (totalHours > 0) "${totalHours}h ${remMinutes}m" else "${remMinutes}m"

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("analytics_screen"),
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
                            imageVector = Icons.Default.Insights,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Focus Telemetry",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "100% offline & private session analytics",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Stat Cards Grid (Row 1)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "TOTAL FOCUS",
                        value = timeFormatted,
                        subtitle = "Deep work logged",
                        icon = Icons.Default.Timer,
                        iconTint = ElectricCyan,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "DEFLECTED",
                        value = "$totalBlockedAttempts",
                        subtitle = "Distractions blocked",
                        icon = Icons.Default.Shield,
                        iconTint = DangerRed,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Stat Cards Grid (Row 2)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "SESSIONS",
                        value = "$completedSessionsCount",
                        subtitle = "Sessions completed",
                        icon = Icons.Default.CheckCircle,
                        iconTint = ZenGreen,
                        modifier = Modifier.weight(1f)
                    )
                    val avgAttempts = if (completedSessionsCount > 0) {
                        totalBlockedAttempts / completedSessionsCount
                    } else 0
                    StatCard(
                        title = "AVG DEFENSE",
                        value = "$avgAttempts / session",
                        subtitle = "Shield rate",
                        icon = Icons.Default.CrisisAlert,
                        iconTint = FocusAmber,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Recent Sessions Section Header
            item {
                Text(
                    text = "Recent Lock Sessions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (recentSessions.isEmpty()) {
                item {
                    Surface(
                        color = DarkSurface,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier.padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No focus sessions logged yet.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextMuted
                                )
                                Text(
                                    text = "Start a session to build your productivity telemetry.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            } else {
                items(recentSessions) { session ->
                    val dateFormat = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
                    val formattedDate = dateFormat.format(Date(session.startTime))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(
                                                if (session.isCompleted) ZenGreen else FocusAmber,
                                                CircleShape
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${session.targetDurationMinutes} min session",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextPrimary
                                    )
                                }
                                Text(
                                    text = formattedDate,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (session.isCompleted) "Completed Full Duration" else "Early Exit: ${session.escapeMethodUsed ?: "Interrupted"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (session.isCompleted) ZenGreen else FocusAmber,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "🛡️ ${session.blockedAttemptsCount} blocked",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (session.blockedAttemptsCount > 0) DangerRed else TextMuted,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}
