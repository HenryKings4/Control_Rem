package com.example.control_rem

import com.example.control_rem.data.model.ConnectionState
import com.example.control_rem.data.model.NetworkConfig
import com.example.control_rem.data.model.ProtocolType
import com.example.control_rem.data.model.RemoteCommand
import com.example.control_rem.data.network.SocketClient
import com.example.control_rem.data.repository.RemoteControlRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoteControlRepositoryTest {

    private class FakeSocketClient : SocketClient {
        private val _state = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
        override val connectionState: StateFlow<ConnectionState> = _state

        val sentCommands = mutableListOf<RemoteCommand>()
        val sentRawPayloads = mutableListOf<String>()

        override suspend fun connect(ipAddress: String, port: Int, protocolType: ProtocolType): Result<Unit> {
            _state.value = ConnectionState.Connected(ipAddress, port, protocolType)
            return Result.success(Unit)
        }

        override suspend fun disconnect() {
            _state.value = ConnectionState.Disconnected
        }

        override suspend fun sendCommand(command: RemoteCommand): Result<Unit> {
            sentCommands.add(command)
            return Result.success(Unit)
        }

        override suspend fun sendRawData(payload: String): Result<Unit> {
            sentRawPayloads.add(payload)
            return Result.success(Unit)
        }
    }

    @Test
    fun testConnectAndUpdateConfig() = runTest {
        val fakeSocket = FakeSocketClient()
        val repo = RemoteControlRepositoryImpl(
            socketClient = fakeSocket,
            initialConfig = NetworkConfig(ipAddress = "10.0.0.1", port = 9000)
        )

        assertEquals("10.0.0.1", repo.currentConfig.value.ipAddress)
        assertEquals(9000, repo.currentConfig.value.port)

        val connectResult = repo.connect("192.168.1.50", 8080, ProtocolType.TCP)
        assertTrue(connectResult.isSuccess)

        assertEquals("192.168.1.50", repo.currentConfig.value.ipAddress)
        assertEquals(8080, repo.currentConfig.value.port)
        assertTrue(repo.connectionState.value is ConnectionState.Connected)
    }

    @Test
    fun testSendCommandAndRecordHistory() = runTest {
        val fakeSocket = FakeSocketClient()
        val repo = RemoteControlRepositoryImpl(socketClient = fakeSocket)

        val cmd1 = RemoteCommand.SlideNext
        val cmd2 = RemoteCommand.MouseMove(10f, 20f)

        repo.sendCommand(cmd1)
        repo.sendCommand(cmd2)

        assertEquals(2, fakeSocket.sentCommands.size)
        assertEquals(cmd1, fakeSocket.sentCommands[0])
        assertEquals(cmd2, fakeSocket.sentCommands[1])

        val history = repo.commandHistory.value
        assertEquals(2, history.size)
        assertEquals(cmd2, history[0])
        assertEquals(cmd1, history[1])
    }
}
