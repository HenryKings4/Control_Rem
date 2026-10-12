package com.example.control_rem.data.model

/**
 * Represents the network connection lifecycle state to the PC receiver server.
 */
sealed class ConnectionState {
    data object Disconnected : ConnectionState()

    data object Connecting : ConnectionState()

    data class Connected(
        val ipAddress: String,
        val port: Int,
        val protocolType: ProtocolType = ProtocolType.TCP,
        val connectedAtTimestamp: Long = System.currentTimeMillis()
    ) : ConnectionState()

    data class Error(
        val message: String,
        val cause: Throwable? = null,
        val timestamp: Long = System.currentTimeMillis()
    ) : ConnectionState()

    val isConnected: Boolean
        get() = this is Connected

    val isConnecting: Boolean
        get() = this is Connecting
}
