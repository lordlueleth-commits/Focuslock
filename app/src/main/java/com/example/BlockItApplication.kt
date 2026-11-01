package com.example

import android.app.Application
import com.example.data.repository.FocusRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BlockItApplication : Application() {

    lateinit var repository: FocusRepository
        private set

    override fun onCreate() {
        super.onCreate()
        repository = FocusRepository(this)

        // Seed initial essential apps in background
        CoroutineScope(Dispatchers.IO).launch {
            repository.populateInstalledAppsIfEmpty()
        }
    }
}
