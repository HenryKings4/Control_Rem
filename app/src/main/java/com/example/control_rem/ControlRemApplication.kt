package com.example.control_rem

import android.app.Application
import com.example.control_rem.data.network.NetworkLockManager
import com.example.control_rem.data.repository.RepositoryProvider

/**
 * Custom Application class for initializing application-wide services,
 * DataStore preferences, and NetworkLockManager.
 */
class ControlRemApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        RepositoryProvider.initialize(this)
        NetworkLockManager.initialize(this)
    }
}
