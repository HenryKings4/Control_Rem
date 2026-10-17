package com.example.control_rem.data.network

import com.example.control_rem.data.model.ConnectionState
import com.example.control_rem.data.model.ProtocolType
import com.example.control_rem.data.model.RemoteCommand
import com.example.control_rem.data.protocol.CommandSerializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket

class TcpSocketClient(
    private val defaultTimeoutMs: Int = 3000
) : SocketClient {

    private val clientScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val sendMutex = Mutex()

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private var socket: Socket? = null
    private var outputStream: OutputStream? = null
    private var inputStream: InputStream? = null
    private var readJob: Job? = null

    // Persist configured host and port so auto-reconnection works even if the socket closes
    private var configuredHost: String? = null
    private var configuredPort: Int? = null

    override suspend fun connect(ipAddress: String, port: Int, protocolType: ProtocolType): Result<Unit> =
        sendMutex.withLock {
            withContext(Dispatchers.IO) {
                configuredHost = ipAddress
                configuredPort = port

                val activeSocket = socket
                if (_connectionState.value is ConnectionState.Connected &&
                    activeSocket != null &&
                    activeSocket.isConnected &&
                    !activeSocket.isClosed
                ) {
                    return@withContext Result.success(Unit)
                }

                disconnectInternal(clearTargetConfig = false)
                _connectionState.value = ConnectionState.Connecting

                try {
                    val newSocket = Socket()
                    newSocket.tcpNoDelay = true // Disable Nagle's algorithm for minimum mouse latency
                    newSocket.connect(InetSocketAddress(ipAddress, port), defaultTimeoutMs)

                    socket = newSocket
                    outputStream = newSocket.getOutputStream()
                    inputStream = newSocket.getInputStream()

                    _connectionState.value = ConnectionState.Connected(ipAddress, port, ProtocolType.TCP)

                    // Start background listener loop to detect remote disconnection (EOF / half-open socket)
                    startReadLoop(newSocket)

                    Result.success(Unit)
                } catch (e: Exception) {
                    val errorMsg = e.localizedMessage ?: "Failed to connect to $ipAddress:$port"
                    _connectionState.value = ConnectionState.Error(errorMsg, e)
                    disconnectInternal(clearTargetConfig = false)
                    Result.failure(e)
                }
            }
        }

    override suspend fun disconnect() {
        sendMutex.withLock {
            withContext(Dispatchers.IO) {
                disconnectInternal(clearTargetConfig = true)
                _connectionState.value = ConnectionState.Disconnected
            }
        }
    }

    private fun disconnectInternal(clearTargetConfig: Boolean) {
        readJob?.cancel()
        readJob = null

        try {
            outputStream?.close()
        } catch (_: Exception) {}
        try {
            inputStream?.close()
        } catch (_: Exception) {}
        try {
            socket?.close()
        } catch (_: Exception) {}

        outputStream = null
        inputStream = null
        socket = null

        if (clearTargetConfig) {
            configuredHost = null
            configuredPort = null
        }
    }

    private fun startReadLoop(activeSocket: Socket) {
        readJob?.cancel()
        readJob = clientScope.launch {
            val input = activeSocket.getInputStream()
            val buffer = ByteArray(1024)
            try {
                while (isActive && !activeSocket.isClosed) {
                    val bytesRead = input.read(buffer)
                    if (bytesRead == -1) {
                        // Remote host closed the TCP connection cleanly
                        if (_connectionState.value is ConnectionState.Connected) {
                            _connectionState.value = ConnectionState.Disconnected
                        }
                        disconnectInternal(clearTargetConfig = false)
                        break
                    }
                }
            } catch (e: Exception) {
                if (isActive && _connectionState.value is ConnectionState.Connected) {
                    _connectionState.value = ConnectionState.Error("Conexión finalizada por el servidor", e)
                    disconnectInternal(clearTargetConfig = false)
                }
            }
        }
    }

    override suspend fun sendCommand(command: RemoteCommand): Result<Unit> {
        val payload = CommandSerializer.toTextProtocol(command, includeNewline = true)
        return sendRawData(payload)
    }

    override suspend fun sendRawData(payload: String): Result<Unit> = sendMutex.withLock {
        withContext(Dispatchers.IO) {
            var currentSocket = socket
            var currentOutput = outputStream

            if (currentSocket == null || currentOutput == null || !currentSocket.isConnected || currentSocket.isClosed) {
                val host = configuredHost
                val port = configuredPort
                if (host != null && port != null) {
                    // Attempt automatic reconnection with stored target
                    try {
                        disconnectInternal(clearTargetConfig = false)
                        val newSocket = Socket()
                        newSocket.tcpNoDelay = true
                        newSocket.connect(InetSocketAddress(host, port), defaultTimeoutMs)

                        socket = newSocket
                        outputStream = newSocket.getOutputStream()
                        inputStream = newSocket.getInputStream()
                        currentSocket = newSocket
                        currentOutput = newSocket.getOutputStream()

                        _connectionState.value = ConnectionState.Connected(host, port, ProtocolType.TCP)
                        startReadLoop(newSocket)
                    } catch (e: Exception) {
                        val error = IllegalStateException("Reconnection failed: ${e.message}", e)
                        _connectionState.value = ConnectionState.Error(error.message ?: "Reconnection error", e)
                        return@withContext Result.failure(error)
                    }
                } else {
                    val error = IllegalStateException("Not connected to any target device")
                    _connectionState.value = ConnectionState.Error("Not connected", error)
                    return@withContext Result.failure(error)
                }
            }

            try {
                val bytes = payload.toByteArray(Charsets.UTF_8)
                currentOutput.write(bytes)
                currentOutput.flush()
                Result.success(Unit)
            } catch (e: Exception) {
                val errorMsg = "Error sending data: ${e.localizedMessage}"
                _connectionState.value = ConnectionState.Error(errorMsg, e)
                disconnectInternal(clearTargetConfig = false)
                Result.failure(e)
            }
        }
    }
}
