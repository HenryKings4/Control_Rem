package com.example.control_rem

import com.example.control_rem.data.model.ConnectionState
import com.example.control_rem.data.model.NetworkConfig
import com.example.control_rem.data.model.ProtocolType
import com.example.control_rem.data.model.RemoteCommand
import com.example.control_rem.data.repository.RemoteControlRepository
import com.example.control_rem.wearable.WearableMessageHandlerImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WearableMessageHandlerTest {

    private class FakeRepository : RemoteControlRepository {
        override val connectionState: StateFlow<ConnectionState> = MutableStateFlow(ConnectionState.Disconnected)
        override val currentConfig: StateFlow<NetworkConfig> = MutableStateFlow(NetworkConfig())
        override val commandHistory: StateFlow<List<RemoteCommand>> = MutableStateFlow(emptyList())

        val forwardedCommands = mutableListOf<RemoteCommand>()

        override suspend fun connect(ipAddress: String, port: Int, protocolType: ProtocolType): Result<Unit> {
            return Result.success(Unit)
        }

        override suspend fun disconnect() {}

        override suspend fun sendCommand(command: RemoteCommand): Result<Unit> {
            forwardedCommands.add(command)
            return Result.success(Unit)
        }

        override suspend fun sendRawPayload(payload: String): Result<Unit> {
            return Result.success(Unit)
        }

        override suspend fun updateConfig(config: NetworkConfig) {}

        override fun clearCommandHistory() {}
    }

    @Test
    fun testParseAndForwardSlideNextMessage() = runTest {
        val handler = WearableMessageHandlerImpl()
        val repository = FakeRepository()
        handler.bindRepository(repository)

        val result = handler.handleWearableMessage("/remote/slide/next", null)
        assertTrue(result.isSuccess)
        assertEquals(RemoteCommand.SlideNext, result.getOrNull())

        assertEquals(1, repository.forwardedCommands.size)
        assertEquals(RemoteCommand.SlideNext, repository.forwardedCommands[0])
    }

    @Test
    fun testParseAndForwardRemoteCommandPath() = runTest {
        val handler = WearableMessageHandlerImpl()
        val repository = FakeRepository()
        handler.bindRepository(repository)

        val nextPayload = "SLIDE_NEXT".toByteArray(Charsets.UTF_8)
        val resultNext = handler.handleWearableMessage("/remote/command", nextPayload)
        assertTrue(resultNext.isSuccess)
        assertEquals(RemoteCommand.SlideNext, resultNext.getOrNull())

        val prevPayload = "SLIDE_PREV".toByteArray(Charsets.UTF_8)
        val resultPrev = handler.handleWearableMessage("/remote/command", prevPayload)
        assertTrue(resultPrev.isSuccess)
        assertEquals(RemoteCommand.SlidePrev, resultPrev.getOrNull())

        val startPayload = "SLIDE_START".toByteArray(Charsets.UTF_8)
        val resultStart = handler.handleWearableMessage("/remote/command", startPayload)
        assertTrue(resultStart.isSuccess)
        assertEquals(RemoteCommand.SlideStart, resultStart.getOrNull())

        val stopPayload = "SLIDE_STOP".toByteArray(Charsets.UTF_8)
        val resultStop = handler.handleWearableMessage("/remote/command", stopPayload)
        assertTrue(resultStop.isSuccess)
        assertEquals(RemoteCommand.SlideStop, resultStop.getOrNull())

        assertEquals(4, repository.forwardedCommands.size)
        assertEquals(RemoteCommand.SlideNext, repository.forwardedCommands[0])
        assertEquals(RemoteCommand.SlidePrev, repository.forwardedCommands[1])
        assertEquals(RemoteCommand.SlideStart, repository.forwardedCommands[2])
        assertEquals(RemoteCommand.SlideStop, repository.forwardedCommands[3])
    }

    @Test
    fun testParseAndForwardMouseMoveMessage() = runTest {
        val handler = WearableMessageHandlerImpl()
        val repository = FakeRepository()
        handler.bindRepository(repository)

        val payload = "15.0, -10.0".toByteArray(Charsets.UTF_8)
        val result = handler.handleWearableMessage("/remote/mouse/move", payload)

        assertTrue(result.isSuccess)
        val cmd = result.getOrNull()
        assertTrue(cmd is RemoteCommand.MouseMove)
        val move = cmd as RemoteCommand.MouseMove
        assertEquals(15.0f, move.dx, 0.001f)
        assertEquals(-10.0f, move.dy, 0.001f)

        assertEquals(1, repository.forwardedCommands.size)
        assertEquals(cmd, repository.forwardedCommands[0])
    }
}
