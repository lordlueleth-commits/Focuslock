package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "whitelisted_apps")
data class WhitelistedAppEntity(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val isAllowed: Boolean = true,
    val isEssential: Boolean = false,
    val category: String = "Utility"
)
