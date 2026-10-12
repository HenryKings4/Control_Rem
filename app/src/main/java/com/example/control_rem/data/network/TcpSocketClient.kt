package com.example.control_rem.data.network

import com.example.control_rem.data.model.ConnectionState
import com.example.control_rem.data.model.ProtocolType
import com.example.control_rem.data.model.RemoteCommand
import com.example.control_rem.data.protocol.CommandSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket

class TcpSocketClient(
    private val defaultTimeoutMs: Int = 3000
) : SocketClient {

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private var socket: Socket? = null
    private var outputStream: OutputStream? = null
    private var currentHost: String? = null
    private var currentPort: Int? = null

    override suspend fun connect(ipAddress: String, port: Int, protocolType: ProtocolType): Result<Unit> =
        withContext(Dispatchers.IO) {
            val activeSocket = socket
            if (_connectionState.value is ConnectionState.Connected &&
                currentHost == ipAddress &&
                currentPort == port &&
                activeSocket != null &&
                activeSocket.isConnected &&
                !activeSocket.isClosed
            ) {
                return@withContext Result.success(Unit)
            }

            disconnectInternal()
            _connectionState.value = ConnectionState.Connecting

            try {
                val newSocket = Socket()
                newSocket.tcpNoDelay = true // Disable Nagle's algorithm for minimum mouse latency
                newSocket.soTimeout = defaultTimeoutMs
                newSocket.connect(InetSocketAddress(ipAddress, port), defaultTimeoutMs)

                socket = newSocket
                outputStream = newSocket.getOutputStream()
                currentHost = ipAddress
                currentPort = port

                _connectionState.value = ConnectionState.Connected(ipAddress, port, ProtocolType.TCP)
                Result.success(Unit)
            } catch (e: Exception) {
                val errorMsg = e.localizedMessage ?: "Failed to connect to $ipAddress:$port"
                _connectionState.value = ConnectionState.Error(errorMsg, e)
                disconnectInternal()
                Result.failure(e)
            }
        }

    override suspend fun disconnect() {
        withContext(Dispatchers.IO) {
            disconnectInternal()
            _connectionState.value = ConnectionState.Disconnected
        }
    }

    private fun disconnectInternal() {
        try {
            outputStream?.close()
        } catch (_: Exception) {}
        try {
            socket?.close()
        } catch (_: Exception) {}

        outputStream = null
        socket = null
        currentHost = null
        currentPort = null
    }

    override suspend fun sendCommand(command: RemoteCommand): Result<Unit> {
        val payload = CommandSerializer.toTextProtocol(command, includeNewline = true)
        return sendRawData(payload)
    }

    override suspend fun sendRawData(payload: String): Result<Unit> = withContext(Dispatchers.IO) {
        val currentSocket = socket
        val currentOutput = outputStream

        if (currentSocket == null || currentOutput == null || !currentSocket.isConnected || currentSocket.isClosed) {
            val host = currentHost
            val port = currentPort
            if (host != null && port != null) {
                val reconnectResult = connect(host, port, ProtocolType.TCP)
                if (reconnectResult.isFailure) {
                    return@withContext reconnectResult
                }
            } else {
                val error = IllegalStateException("Not connected to any target device")
                _connectionState.value = ConnectionState.Error("Not connected", error)
                return@withContext Result.failure(error)
            }
        }

        try {
            val bytes = payload.toByteArray(Charsets.UTF_8)
            outputStream?.write(bytes)
            outputStream?.flush()
            Result.success(Unit)
        } catch (e: Exception) {
            val errorMsg = "Error sending data: ${e.localizedMessage}"
            _connectionState.value = ConnectionState.Error(errorMsg, e)
            disconnectInternal()
            Result.failure(e)
        }
    }
}
