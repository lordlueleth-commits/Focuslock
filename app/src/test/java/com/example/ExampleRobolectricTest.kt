package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.SessionPreferences
import com.example.data.model.EscapeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read app name from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("BlockIt", appName)
  }

  @Test
  fun `session preferences start and end lifecycle`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = SessionPreferences(context)

    prefs.endSession()
    assertFalse(prefs.isSessionActive)

    prefs.startSession(
      sessionId = 101L,
      durationMinutes = 25,
      tasks = setOf("Draft Article", "Deep Coding"),
      mode = EscapeMode.LOGIC_PUZZLE,
      allowedPkgs = setOf("com.example.blockit")
    )

    assertTrue(prefs.isSessionActive)
    assertEquals(25, prefs.sessionDurationMinutes)
    assertEquals(EscapeMode.LOGIC_PUZZLE, prefs.escapeMode)
    assertEquals(2, prefs.currentTasks.size)

    prefs.incrementBlockedAttempts("com.instagram.android")
    assertEquals(1, prefs.blockedAttemptsCount)
    assertEquals("com.instagram.android", prefs.lastBlockedPackage)

    prefs.endSession()
    assertFalse(prefs.isSessionActive)
  }
}
