package com.example.control_rem.data.model

enum class ProtocolType {
    TCP,
    UDP
}

/**
 * Configuration data class holding IP, Port, and network protocol preferences.
 */
data class NetworkConfig(
    val ipAddress: String = "192.168.1.100",
    val port: Int = 8080,
    val protocolType: ProtocolType = ProtocolType.TCP,
    val connectionTimeoutMs: Int = 3000
)
