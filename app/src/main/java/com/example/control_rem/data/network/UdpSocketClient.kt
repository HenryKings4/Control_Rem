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
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class UdpSocketClient : SocketClient {

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private var socket: DatagramSocket? = null
    private var targetInetAddress: InetAddress? = null
    private var targetPort: Int? = null

    override suspend fun connect(ipAddress: String, port: Int, protocolType: ProtocolType): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                socket?.close()
                val newSocket = DatagramSocket()
                val address = InetAddress.getByName(ipAddress)

                socket = newSocket
                targetInetAddress = address
                targetPort = port

                _connectionState.value = ConnectionState.Connected(ipAddress, port, ProtocolType.UDP)
                Result.success(Unit)
            } catch (e: Exception) {
                val errorMsg = e.localizedMessage ?: "UDP Socket setup failed for $ipAddress:$port"
                _connectionState.value = ConnectionState.Error(errorMsg, e)
                Result.failure(e)
            }
        }

    override suspend fun disconnect() {
        withContext(Dispatchers.IO) {
            try {
                socket?.close()
            } catch (_: Exception) {}
            socket = null
            targetInetAddress = null
            targetPort = null
            _connectionState.value = ConnectionState.Disconnected
        }
    }

    override suspend fun sendCommand(command: RemoteCommand): Result<Unit> {
        val payload = CommandSerializer.toTextProtocol(command, includeNewline = true)
        return sendRawData(payload)
    }

    override suspend fun sendRawData(payload: String): Result<Unit> = withContext(Dispatchers.IO) {
        val currentSocket = socket
        val address = targetInetAddress
        val port = targetPort

        if (currentSocket == null || address == null || port == null || currentSocket.isClosed) {
            val error = IllegalStateException("UDP socket not connected")
            _connectionState.value = ConnectionState.Error("UDP socket not initialized", error)
            return@withContext Result.failure(error)
        }

        try {
            val bytes = payload.toByteArray(Charsets.UTF_8)
            val packet = DatagramPacket(bytes, bytes.size, address, port)
            currentSocket.send(packet)
            Result.success(Unit)
        } catch (e: Exception) {
            val errorMsg = "UDP send error: ${e.localizedMessage}"
            _connectionState.value = ConnectionState.Error(errorMsg, e)
            Result.failure(e)
        }
    }
}
