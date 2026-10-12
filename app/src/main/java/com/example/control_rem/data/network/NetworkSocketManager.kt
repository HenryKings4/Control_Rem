package com.example.control_rem.data.network

import com.example.control_rem.data.model.ConnectionState
import com.example.control_rem.data.model.ProtocolType
import com.example.control_rem.data.model.RemoteCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NetworkSocketManager(
    private val tcpClient: TcpSocketClient = TcpSocketClient(),
    private val udpClient: UdpSocketClient = UdpSocketClient()
) : SocketClient {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private var activeProtocol: ProtocolType = ProtocolType.TCP

    init {
        scope.launch {
            tcpClient.connectionState.collect { state ->
                if (activeProtocol == ProtocolType.TCP) {
                    _connectionState.value = state
                }
            }
        }
        scope.launch {
            udpClient.connectionState.collect { state ->
                if (activeProtocol == ProtocolType.UDP) {
                    _connectionState.value = state
                }
            }
        }
    }

    override suspend fun connect(ipAddress: String, port: Int, protocolType: ProtocolType): Result<Unit> {
        activeProtocol = protocolType
        return when (protocolType) {
            ProtocolType.TCP -> {
                udpClient.disconnect()
                tcpClient.connect(ipAddress, port, protocolType)
            }
            ProtocolType.UDP -> {
                tcpClient.disconnect()
                udpClient.connect(ipAddress, port, protocolType)
            }
        }
    }

    override suspend fun disconnect() {
        tcpClient.disconnect()
        udpClient.disconnect()
        _connectionState.value = ConnectionState.Disconnected
    }

    override suspend fun sendCommand(command: RemoteCommand): Result<Unit> {
        return when (activeProtocol) {
            ProtocolType.TCP -> tcpClient.sendCommand(command)
            ProtocolType.UDP -> udpClient.sendCommand(command)
        }
    }

    override suspend fun sendRawData(payload: String): Result<Unit> {
        return when (activeProtocol) {
            ProtocolType.TCP -> tcpClient.sendRawData(payload)
            ProtocolType.UDP -> udpClient.sendRawData(payload)
        }
    }
}
