package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: Long,
    val endTime: Long,
    val targetDurationMinutes: Int,
    val actualDurationMinutes: Int,
    val isCompleted: Boolean,
    val tasksJson: String,
    val blockedAttemptsCount: Int,
    val escapeMethodUsed: String? = null
)
