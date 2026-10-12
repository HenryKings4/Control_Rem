package com.example.control_rem.wearable

import com.example.control_rem.data.model.RemoteCommand
import com.example.control_rem.data.repository.RemoteControlRepository
import kotlinx.coroutines.flow.SharedFlow

/**
 * Interface contract for receiving, parsing, and dispatching Wear OS smartwatch messages
 * (e.g., from Moto 360, Galaxy Watch, Pixel Watch) to the PC receiver server.
 */
interface WearableMessageHandler {
    val receivedWearableCommands: SharedFlow<RemoteCommand>

    /**
     * Binds a [RemoteControlRepository] so incoming Wear OS messages are automatically forwarded to PC.
     */
    fun bindRepository(repository: RemoteControlRepository)

    /**
     * Handles an incoming message path and byte payload from Wear OS.
     * Parses the path/payload into a [RemoteCommand] and forwards it to the bound repository.
     */
    suspend fun handleWearableMessage(path: String, data: ByteArray?): Result<RemoteCommand>
}
