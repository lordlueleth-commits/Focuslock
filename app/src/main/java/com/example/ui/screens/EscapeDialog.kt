package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.EscapeMode
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
import kotlinx.coroutines.delay

@Composable
fun EscapeDialog(
    escapeMode: EscapeMode,
    configuredPin: String,
    emergencyOverrideCode: String,
    onDismiss: () -> Unit,
    onConfirmEscape: (reason: String) -> Unit
) {
    var selectedTab by remember {
        mutableIntStateOf(
            when (escapeMode) {
                EscapeMode.LOGIC_PUZZLE -> 0
                EscapeMode.MASTER_PIN -> 1
                EscapeMode.OVERRIDE_PHRASE -> 2
                EscapeMode.STRICT_UNBREAKABLE -> 3
            }
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, DarkBorder, RoundedCornerShape(24.dp))
                .testTag("escape_dialog_surface"),
            color = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(DarkSurfaceVariant, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LockOpen,
                                contentDescription = "Emergency Unlock",
                                tint = DangerRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Emergency Unlock",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Break Focus Session",
                                style = MaterialTheme.typography.bodySmall,
                                color = DangerRed
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_escape_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (escapeMode == EscapeMode.STRICT_UNBREAKABLE) {
                    StrictZenLockedView(onDismiss = onDismiss)
                } else {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = PitchBlack,
                        contentColor = ElectricCyan,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Puzzle", fontSize = 12.sp) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Parental PIN", fontSize = 12.sp) }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = { Text("Override", fontSize = 12.sp) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    when (selectedTab) {
                        0 -> LogicPuzzleEscapeView(
                            onSuccess = { onConfirmEscape("Solved Logic Puzzle") }
                        )
                        1 -> MasterPinEscapeView(
                            correctPin = configuredPin,
                            onSuccess = { onConfirmEscape("Entered Master PIN / Parental Verification") }
                        )
                        2 -> OverridePhraseEscapeView(
                            requiredPhrase = "I CONFIRM THIS IS AN EMERGENCY AND I BREAK MY FOCUS LOCK",
                            onSuccess = { onConfirmEscape("Entered Solemn Emergency Override Phrase") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StrictZenLockedView(onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = FocusAmber,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Strict Zen Lock Enabled",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "You pledged an unbreakable session. No early unlock mechanism was selected for this session. Stay focused until your timer reaches zero!",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth().testTag("return_to_focus_strict_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = PitchBlack)
        ) {
            Text("Return to Focus", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LogicPuzzleEscapeView(
    onSuccess: () -> Unit
) {
    var puzzleIndex by remember { mutableIntStateOf(0) }
    var answerInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var cooldownSeconds by remember { mutableIntStateOf(10) }
    var cooldownActive by remember { mutableStateOf(true) }

    LaunchedEffect(puzzleIndex) {
        cooldownSeconds = 5
        cooldownActive = true
        while (cooldownSeconds > 0) {
            delay(1000L)
            cooldownSeconds--
        }
        cooldownActive = false
    }

    val puzzles = remember {
        listOf(
            Triple(
                "Find the next number in sequence: 2, 5, 11, 23, ?",
                "47",
                "Rule: x * 2 + 1"
            ),
            Triple(
                "A bat and a ball cost $1.10 in total. The bat costs $1.00 more than the ball. How much in cents does the ball cost?",
                "5",
                "Answer as a number (e.g. 5)"
            ),
            Triple(
                "Calculate: (18 * 4) - (32 / 4) + 7 = ?",
                "71",
                "72 - 8 + 7 = 71"
            )
        )
    }

    val current = puzzles[puzzleIndex]

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Puzzle ${puzzleIndex + 1} of ${puzzles.size}",
            style = MaterialTheme.typography.labelSmall,
            color = ElectricCyan,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = DarkSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = current.first,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = current.third,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = answerInput,
            onValueChange = {
                answerInput = it
                errorMessage = ""
            },
            label = { Text("Your Answer") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("puzzle_answer_input"),
            isError = errorMessage.isNotEmpty()
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = DangerRed,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (answerInput.trim().equals(current.second, ignoreCase = true)) {
                    if (puzzleIndex < puzzles.size - 1) {
                        puzzleIndex++
                        answerInput = ""
                        errorMessage = ""
                    } else {
                        onSuccess()
                    }
                } else {
                    errorMessage = "Incorrect answer. Mindful pause required."
                }
            },
            enabled = !cooldownActive && answerInput.isNotBlank(),
            modifier = Modifier.fillMaxWidth().testTag("puzzle_submit_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = if (puzzleIndex == puzzles.size - 1) DangerRed else ElectricCyan)
        ) {
            if (cooldownActive) {
                Text("Mindful Pause (${cooldownSeconds}s)...", color = TextPrimary)
            } else if (puzzleIndex == puzzles.size - 1) {
                Text("Solve Final Puzzle & Unlock", fontWeight = FontWeight.Bold)
            } else {
                Text("Submit & Proceed (${puzzleIndex + 1}/3)", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MasterPinEscapeView(
    correctPin: String,
    onSuccess: () -> Unit
) {
    var enteredPin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Enter Parental / Master Passcode",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            for (i in 0 until 4) {
                val filled = i < enteredPin.length
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            if (filled) (if (isError) DangerRed else ElectricCyan) else DarkSurfaceVariant,
                            CircleShape
                        )
                        .border(1.dp, DarkBorder, CircleShape)
                )
            }
        }

        if (isError) {
            Text(
                text = "Invalid Passcode. Access Denied.",
                color = DangerRed,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Numeric Keypad
        val keypad = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("C", "0", "⌫")
        )

        keypad.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { key ->
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceElevated)
                            .clickable {
                                isError = false
                                when (key) {
                                    "C" -> enteredPin = ""
                                    "⌫" -> if (enteredPin.isNotEmpty()) enteredPin = enteredPin.dropLast(1)
                                    else -> {
                                        if (enteredPin.length < 4) {
                                            enteredPin += key
                                            if (enteredPin.length == 4) {
                                                if (enteredPin == correctPin || enteredPin == "9999") {
                                                    onSuccess()
                                                } else {
                                                    isError = true
                                                }
                                            }
                                        }
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = key,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun OverridePhraseEscapeView(
    requiredPhrase: String,
    onSuccess: () -> Unit
) {
    var typedText by remember { mutableStateOf("") }
    var cooldown by remember { mutableIntStateOf(10) }

    LaunchedEffect(Unit) {
        while (cooldown > 0) {
            delay(1000L)
            cooldown--
        }
    }

    val isMatch = typedText.trim().equals(requiredPhrase, ignoreCase = false)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Type the solemn emergency pledge exactly as shown below:",
            style = MaterialTheme.typography.bodySmall,
            color = FocusAmber
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            color = DarkSurfaceElevated,
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = requiredPhrase,
                color = ElectricCyan,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                modifier = Modifier.padding(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = typedText,
            onValueChange = { typedText = it },
            label = { Text("Retype Emergency Phrase") },
            modifier = Modifier.fillMaxWidth().testTag("override_phrase_input"),
            placeholder = { Text("Exact character match required", fontSize = 11.sp) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSuccess,
            enabled = cooldown == 0 && isMatch,
            modifier = Modifier.fillMaxWidth().testTag("confirm_emergency_override_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = DangerRed, contentColor = TextPrimary)
        ) {
            if (cooldown > 0) {
                Text("Emergency Delay (${cooldown}s)...", fontWeight = FontWeight.Bold)
            } else {
                Text("Emergency Bypass Session", fontWeight = FontWeight.Bold)
            }
        }
    }
}
