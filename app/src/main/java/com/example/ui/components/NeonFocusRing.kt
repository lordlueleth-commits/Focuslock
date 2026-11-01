package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.FocusAmber
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun NeonFocusRing(
    remainingSeconds: Long,
    progress: Float,
    isSessionActive: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(500),
        label = "FocusProgress"
    )

    val minutes = (remainingSeconds / 60)
    val seconds = (remainingSeconds % 60)
    val timeDisplay = if (isSessionActive) {
        String.format("%02d:%02d", minutes, seconds)
    } else {
        "--:--"
    }

    Box(
        modifier = modifier.size(260.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            val strokeWidth = 10.dp.toPx()
            val diameter = size.minDimension - strokeWidth
            val topLeft = Offset(
                (size.width - diameter) / 2f,
                (size.height - diameter) / 2f
            )

            // Background subtle track
            drawArc(
                color = DarkBorder,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(diameter, diameter),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Active neon progress arc
            if (isSessionActive && animatedProgress > 0f) {
                drawArc(
                    color = ElectricCyan,
                    startAngle = -90f,
                    sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(diameter, diameter),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isSessionActive) "DEEP WORK" else "READY",
                style = MaterialTheme.typography.labelSmall,
                color = if (isSessionActive) ElectricCyan else TextMuted,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = timeDisplay,
                color = TextPrimary,
                fontSize = 46.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                letterSpacing = (-1).sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isSessionActive) "${(progress * 100).toInt()}% COMPLETED" else "START FOCUS",
                style = MaterialTheme.typography.labelSmall,
                color = if (isSessionActive) FocusAmber else TextMuted,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
