package com.example.control_rem.wear.data.model

enum class WearConnectionMode {
    DATA_LAYER,
    DIRECT_SOCKET,
    AUTO_FALLBACK,
    DISCONNECTED
}

data class WearConnectionStatus(
    val isConnected: Boolean = false,
    val mode: WearConnectionMode = WearConnectionMode.DISCONNECTED,
    val connectedNodesCount: Int = 0,
    val directIpAddress: String = "192.168.1.100",
    val directPort: Int = 8080,
    val isDirectWifiEnabled: Boolean = false,
    val statusText: String = "Disconnected"
)
