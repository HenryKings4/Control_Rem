package com.example.control_rem.wear.data.repository

import com.example.control_rem.wear.data.datalayer.WearDataLayerClient
import com.example.control_rem.wear.data.model.WearConnectionMode
import com.example.control_rem.wear.data.model.WearConnectionStatus
import com.example.control_rem.wear.data.socket.WearDirectSocketClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class WearRemoteRepository(
    private val dataLayerClient: WearDataLayerClient,
    private val directSocketClient: WearDirectSocketClient = WearDirectSocketClient()
) {
    private val _connectionStatus = MutableStateFlow(WearConnectionStatus())
    val connectionStatus: StateFlow<WearConnectionStatus> = _connectionStatus.asStateFlow()

    suspend fun refreshConnectionStatus() {
        val phoneConnected = dataLayerClient.isPhoneConnected()
        val nodeCount = if (phoneConnected) dataLayerClient.getConnectedNodeCount() else 0

        _connectionStatus.update { current ->
            if (phoneConnected) {
                current.copy(
                    isConnected = true,
                    mode = WearConnectionMode.DATA_LAYER,
                    connectedNodesCount = nodeCount,
                    statusText = if (nodeCount == 1) "Connected to Phone" else "Connected to $nodeCount Phones"
                )
            } else if (current.isDirectWifiEnabled && current.directIpAddress.isNotBlank()) {
                current.copy(
                    isConnected = true,
                    mode = WearConnectionMode.DIRECT_SOCKET,
                    connectedNodesCount = 0,
                    statusText = "Wi-Fi Direct (${current.directIpAddress}:${current.directPort})"
                )
            } else {
                current.copy(
                    isConnected = false,
                    mode = WearConnectionMode.DISCONNECTED,
                    connectedNodesCount = 0,
                    statusText = "Disconnected"
                )
            }
        }
    }

    fun updateDirectSocketConfig(ipAddress: String, port: Int, enabled: Boolean) {
        _connectionStatus.update { current ->
            current.copy(
                directIpAddress = ipAddress.trim(),
                directPort = port,
                isDirectWifiEnabled = enabled
            )
        }
    }

    suspend fun sendCommand(action: String): Result<Unit> {
        val current = _connectionStatus.value
        val isPhoneConnected = dataLayerClient.isPhoneConnected()

        // 1. Primary path: Phone Wearable Data Layer
        if (isPhoneConnected) {
            val result = dataLayerClient.sendPresentationCommand(action)
            if (result.isSuccess) {
                _connectionStatus.update {
                    it.copy(
                        isConnected = true,
                        mode = WearConnectionMode.DATA_LAYER,
                        statusText = "Connected to Phone"
                    )
                }
                return result
            }
        }

        // 2. Fallback path: Direct Standalone Wi-Fi Socket
        if (current.isDirectWifiEnabled && current.directIpAddress.isNotBlank()) {
            val result = directSocketClient.sendCommand(
                ipAddress = current.directIpAddress,
                port = current.directPort,
                action = action
            )
            if (result.isSuccess) {
                _connectionStatus.update {
                    it.copy(
                        isConnected = true,
                        mode = WearConnectionMode.DIRECT_SOCKET,
                        statusText = "Wi-Fi Direct (${current.directIpAddress}:${current.directPort})"
                    )
                }
                return result
            } else {
                _connectionStatus.update {
                    it.copy(
                        isConnected = false,
                        mode = WearConnectionMode.DISCONNECTED,
                        statusText = "Wi-Fi Direct Failed"
                    )
                }
                return result
            }
        }

        // Neither path is available
        val errMessage = "No active connection available (Phone disconnected & Wi-Fi Direct disabled)"
        _connectionStatus.update {
            it.copy(
                isConnected = false,
                mode = WearConnectionMode.DISCONNECTED,
                statusText = "Disconnected"
            )
        }
        return Result.failure(IllegalStateException(errMessage))
    }
}
