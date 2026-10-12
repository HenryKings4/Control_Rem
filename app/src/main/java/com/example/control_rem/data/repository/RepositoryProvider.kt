package com.example.control_rem.data.repository

import com.example.control_rem.wearable.WearableMessageHandlerProvider

/**
 * Global provider for accessing the singleton [RemoteControlRepository] instance.
 * Automatically binds the repository to [WearableMessageHandlerProvider].
 */
object RepositoryProvider {

    val repository: RemoteControlRepository by lazy {
        RemoteControlRepositoryImpl().also { repo ->
            WearableMessageHandlerProvider.getHandler().bindRepository(repo)
        }
    }
}
