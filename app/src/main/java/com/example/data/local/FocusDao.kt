package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.FocusSessionEntity
import com.example.data.model.WhitelistedAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: FocusSessionEntity): Long

    @Update
    suspend fun updateSession(session: FocusSessionEntity)

    @Query("SELECT * FROM focus_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<FocusSessionEntity>>

    @Query("SELECT * FROM focus_sessions ORDER BY startTime DESC LIMIT 10")
    fun getRecentSessions(): Flow<List<FocusSessionEntity>>

    @Query("SELECT SUM(actualDurationMinutes) FROM focus_sessions WHERE isCompleted = 1")
    fun getTotalCompletedFocusMinutes(): Flow<Int?>

    @Query("SELECT SUM(blockedAttemptsCount) FROM focus_sessions")
    fun getTotalBlockedAttempts(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM focus_sessions WHERE isCompleted = 1")
    fun getCompletedSessionCount(): Flow<Int>

    // Whitelist queries
    @Query("SELECT * FROM whitelisted_apps ORDER BY isEssential DESC, appName ASC")
    fun getAllWhitelistedApps(): Flow<List<WhitelistedAppEntity>>

    @Query("SELECT * FROM whitelisted_apps WHERE isAllowed = 1")
    suspend fun getCurrentlyAllowedAppsSync(): List<WhitelistedAppEntity>

    @Query("SELECT * FROM whitelisted_apps WHERE isAllowed = 1")
    fun getCurrentlyAllowedApps(): Flow<List<WhitelistedAppEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertApps(apps: List<WhitelistedAppEntity>)

    @Update
    suspend fun updateApp(app: WhitelistedAppEntity)

    @Query("UPDATE whitelisted_apps SET isAllowed = :allowed WHERE packageName = :pkg")
    suspend fun setAppAllowed(pkg: String, allowed: Boolean)
}
