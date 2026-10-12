package com.example.control_rem.data.network

import com.example.control_rem.data.model.ConnectionState
import com.example.control_rem.data.model.ProtocolType
import com.example.control_rem.data.model.RemoteCommand
import kotlinx.coroutines.flow.StateFlow

/**
 * Socket client interface abstraction for communicating with PC receiver servers over TCP or UDP.
 */
interface SocketClient {
    val connectionState: StateFlow<ConnectionState>

    suspend fun connect(ipAddress: String, port: Int, protocolType: ProtocolType = ProtocolType.TCP): Result<Unit>
    suspend fun disconnect()
    suspend fun sendCommand(command: RemoteCommand): Result<Unit>
    suspend fun sendRawData(payload: String): Result<Unit>
}
