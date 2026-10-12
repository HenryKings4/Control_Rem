package com.example.control_rem.data.repository

import com.example.control_rem.data.model.ConnectionState
import com.example.control_rem.data.model.NetworkConfig
import com.example.control_rem.data.model.ProtocolType
import com.example.control_rem.data.model.RemoteCommand
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository for managing remote presentation and touchpad connection state, configuration,
 * and dispatching commands asynchronously.
 */
interface RemoteControlRepository {
    val connectionState: StateFlow<ConnectionState>
    val currentConfig: StateFlow<NetworkConfig>
    val commandHistory: StateFlow<List<RemoteCommand>>

    suspend fun connect(ipAddress: String, port: Int, protocolType: ProtocolType = ProtocolType.TCP): Result<Unit>
    suspend fun disconnect()
    suspend fun sendCommand(command: RemoteCommand): Result<Unit>
    suspend fun sendRawPayload(payload: String): Result<Unit>
    suspend fun updateConfig(config: NetworkConfig)
    fun clearCommandHistory()
}
