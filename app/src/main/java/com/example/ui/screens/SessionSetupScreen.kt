package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EscapeMode
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
fun SessionSetupScreen(
    permissions: PermissionStatus,
    onStartSession: (duration: Int, tasks: List<String>, escapeMode: EscapeMode) -> Unit,
    onRequestAccessibility: () -> Unit,
    onRequestUsageStats: () -> Unit,
    onRequestOverlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDuration by remember { mutableIntStateOf(25) }
    var sliderValue by remember { mutableFloatStateOf(25f) }
    val tasks = remember { mutableStateListOf("Deep Work & Focus") }
    var taskInputText by remember { mutableStateOf("") }
    var selectedEscapeMode by remember { mutableStateOf(EscapeMode.LOGIC_PUZZLE) }

    val presetDurations = listOf(15, 25, 45, 60, 90, 120)

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("session_setup_screen"),
        color = PitchBlack
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp)
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
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "New Focus Session",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "Lock distractions • Pure deep work",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Permissions Alert Banner if Accessibility is Missing
            if (!permissions.accessibility) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .testTag("accessibility_permission_banner"),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, FocusAmber),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = FocusAmber,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Accessibility Permission Required",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = FocusAmber
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "BlockIt requires Accessibility Service to monitor window state and instantly block distracting apps when session is active.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = onRequestAccessibility,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("enable_accessibility_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = FocusAmber, contentColor = PitchBlack)
                            ) {
                                Text("Enable Focus Interceptor", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Duration Selector Card
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
                                text = "Focus Duration",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "$selectedDuration min",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = ElectricCyan
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Preset chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(presetDurations) { duration ->
                                val isSelected = selectedDuration == duration
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            selectedDuration = duration
                                            sliderValue = duration.toFloat()
                                        },
                                    color = if (isSelected) ElectricCyan else DarkSurfaceElevated,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) ElectricCyan else DarkBorder
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = "${duration}m",
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) PitchBlack else TextSecondary,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Slider(
                            value = sliderValue,
                            onValueChange = {
                                sliderValue = it
                                selectedDuration = it.toInt()
                            },
                            valueRange = 5f..180f,
                            steps = 34,
                            colors = SliderDefaults.colors(
                                thumbColor = ElectricCyan,
                                activeTrackColor = ElectricCyan,
                                inactiveTrackColor = DarkSurfaceVariant
                            ),
                            modifier = Modifier.testTag("duration_slider")
                        )
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Session Goals / Active Tasks Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Focus Intentions & Tasks",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Visible on your locked home screen to keep you on target",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Existing Tasks
                        tasks.forEachIndexed { index, task ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(ElectricCyan, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = task,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                if (tasks.size > 1) {
                                    IconButton(
                                        onClick = { tasks.removeAt(index) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove",
                                            tint = TextMuted,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Add Task Input
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = taskInputText,
                                onValueChange = { taskInputText = it },
                                placeholder = { Text("Add specific focus goal...", fontSize = 13.sp) },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("task_input_field"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElectricCyan,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedContainerColor = DarkSurfaceElevated,
                                    unfocusedContainerColor = DarkSurfaceElevated
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (taskInputText.isNotBlank()) {
                                        tasks.add(taskInputText.trim())
                                        taskInputText = ""
                                    }
                                },
                                enabled = taskInputText.isNotBlank(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated),
                                modifier = Modifier.testTag("add_task_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add",
                                    tint = ElectricCyan
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Escape Mechanism Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Emergency Escape Mechanism",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Enforce commitment while ensuring emergency safety",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        EscapeMode.values().forEach { mode ->
                            val isSelected = selectedEscapeMode == mode
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedEscapeMode = mode },
                                color = if (isSelected) DarkSurfaceElevated else DarkSurface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) ElectricCyan else DarkBorder
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .border(
                                                2.dp,
                                                if (isSelected) ElectricCyan else TextMuted,
                                                CircleShape
                                            )
                                            .padding(3.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(ElectricCyan, CircleShape)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = mode.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) TextPrimary else TextSecondary
                                        )
                                        Text(
                                            text = mode.description,
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
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Start Session Button
            item {
                Button(
                    onClick = {
                        val finalTasks = if (tasks.isEmpty()) listOf("Deep Work") else tasks.toList()
                        onStartSession(selectedDuration, finalTasks, selectedEscapeMode)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("start_session_lock_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricCyan,
                        contentColor = PitchBlack
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "START SESSION LOCK",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
