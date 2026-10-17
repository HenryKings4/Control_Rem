package com.example.control_rem.data.repository

import com.example.control_rem.data.model.ConnectionState
import com.example.control_rem.data.model.NetworkConfig
import com.example.control_rem.data.model.ProtocolType
import com.example.control_rem.data.model.RemoteCommand
import com.example.control_rem.data.network.NetworkSocketManager
import com.example.control_rem.data.network.SocketClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RemoteControlRepositoryImpl(
    private val socketClient: SocketClient = NetworkSocketManager(),
    initialConfig: NetworkConfig = NetworkConfig()
) : RemoteControlRepository {

    override val connectionState: StateFlow<ConnectionState> = socketClient.connectionState

    private val _currentConfig = MutableStateFlow(initialConfig)
    override val currentConfig: StateFlow<NetworkConfig> = _currentConfig.asStateFlow()

    private val _commandHistory = MutableStateFlow<List<RemoteCommand>>(emptyList())
    override val commandHistory: StateFlow<List<RemoteCommand>> = _commandHistory.asStateFlow()

    private val maxHistorySize = 50

    override suspend fun connect(ipAddress: String, port: Int, protocolType: ProtocolType): Result<Unit> {
        val newConfig = _currentConfig.value.copy(
            ipAddress = ipAddress,
            port = port,
            protocolType = protocolType
        )
        _currentConfig.value = newConfig
        return socketClient.connect(ipAddress, port, protocolType)
    }

    override suspend fun disconnect() {
        socketClient.disconnect()
    }

    override suspend fun sendCommand(command: RemoteCommand): Result<Unit> {
        val currentState = connectionState.value
        if (currentState is ConnectionState.Disconnected || currentState is ConnectionState.Error) {
            val config = _currentConfig.value
            if (config.ipAddress.isNotBlank() && config.port in 1..65535) {
                val connectResult = connect(config.ipAddress, config.port, config.protocolType)
                if (connectResult.isFailure) {
                    return connectResult
                }
            }
        }
        val result = socketClient.sendCommand(command)
        if (result.isSuccess) {
            recordCommand(command)
        }
        return result
    }

    override suspend fun sendRawPayload(payload: String): Result<Unit> {
        val currentState = connectionState.value
        if (currentState is ConnectionState.Disconnected || currentState is ConnectionState.Error) {
            val config = _currentConfig.value
            if (config.ipAddress.isNotBlank() && config.port in 1..65535) {
                val connectResult = connect(config.ipAddress, config.port, config.protocolType)
                if (connectResult.isFailure) {
                    return connectResult
                }
            }
        }
        return socketClient.sendRawData(payload)
    }

    override suspend fun updateConfig(config: NetworkConfig) {
        _currentConfig.value = config
    }

    override fun clearCommandHistory() {
        _commandHistory.value = emptyList()
    }

    private fun recordCommand(command: RemoteCommand) {
        val currentList = _commandHistory.value.toMutableList()
        currentList.add(0, command)
        if (currentList.size > maxHistorySize) {
            currentList.removeAt(currentList.lastIndex)
        }
        _commandHistory.value = currentList
    }
}
