package com.example.control_rem.data.repository

import android.content.Context
import com.example.control_rem.data.preferences.NetworkPreferencesDataSource
import com.example.control_rem.wearable.WearableMessageHandlerProvider

/**
 * Global provider for accessing the singleton [RemoteControlRepository] instance.
 * Automatically binds the repository to [WearableMessageHandlerProvider] and persists
 * network configuration using [NetworkPreferencesDataSource].
 */
object RepositoryProvider {

    @Volatile
    private var repositoryInstance: RemoteControlRepository? = null

    fun initialize(context: Context) {
        if (repositoryInstance == null) {
            synchronized(this) {
                if (repositoryInstance == null) {
                    val preferences = NetworkPreferencesDataSource(context.applicationContext)
                    val repo = RemoteControlRepositoryImpl(preferencesDataSource = preferences)
                    WearableMessageHandlerProvider.getHandler().bindRepository(repo)
                    repositoryInstance = repo
                }
            }
        }
    }

    val repository: RemoteControlRepository
        get() = repositoryInstance ?: synchronized(this) {
            repositoryInstance ?: RemoteControlRepositoryImpl().also { repo ->
                WearableMessageHandlerProvider.getHandler().bindRepository(repo)
                repositoryInstance = repo
            }
        }

    fun setRepository(repo: RemoteControlRepository) {
        synchronized(this) {
            repositoryInstance = repo
            WearableMessageHandlerProvider.getHandler().bindRepository(repo)
        }
    }
}
