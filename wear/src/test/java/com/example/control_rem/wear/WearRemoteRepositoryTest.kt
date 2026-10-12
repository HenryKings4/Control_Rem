package com.example.control_rem.wear

import com.example.control_rem.wear.data.datalayer.WearDataLayerClient
import com.example.control_rem.wear.data.datalayer.WearMessageSender
import com.example.control_rem.wear.data.datalayer.WearNodeInfo
import com.example.control_rem.wear.data.datalayer.WearNodeProvider
import com.example.control_rem.wear.data.model.WearConnectionMode
import com.example.control_rem.wear.data.repository.WearRemoteRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WearRemoteRepositoryTest {

    private class FakeMessageSender : WearMessageSender {
        val sentMessages = mutableListOf<Pair<String, String>>()

        override suspend fun sendMessage(nodeId: String, path: String, data: ByteArray): Result<Unit> {
            sentMessages.add(Pair(path, String(data, Charsets.UTF_8)))
            return Result.success(Unit)
        }
    }

    private class FakeNodeProvider(private val nodes: List<WearNodeInfo>) : WearNodeProvider {
        override suspend fun getConnectedNodes(): List<WearNodeInfo> = nodes
    }

    @Test
    fun testRefreshConnectionStatusWithConnectedPhone() = runTest {
        val nodeProvider = FakeNodeProvider(listOf(WearNodeInfo("phone_1", "Pixel")))
        val messageSender = FakeMessageSender()
        val dataLayerClient = WearDataLayerClient(messageSender, nodeProvider)
        val repository = WearRemoteRepository(dataLayerClient)

        repository.refreshConnectionStatus()

        val status = repository.connectionStatus.value
        assertTrue(status.isConnected)
        assertEquals(WearConnectionMode.DATA_LAYER, status.mode)
        assertEquals(1, status.connectedNodesCount)
    }

    @Test
    fun testSendCommandDelegatesToDataLayerWhenPhoneConnected() = runTest {
        val nodeProvider = FakeNodeProvider(listOf(WearNodeInfo("phone_1", "Pixel")))
        val messageSender = FakeMessageSender()
        val dataLayerClient = WearDataLayerClient(messageSender, nodeProvider)
        val repository = WearRemoteRepository(dataLayerClient)

        val result = repository.sendCommand("SLIDE_START")
        assertTrue(result.isSuccess)

        assertEquals(1, messageSender.sentMessages.size)
        assertEquals("SLIDE_START", messageSender.sentMessages[0].second)
    }

    @Test
    fun testSendCommandFailsWhenNoConnectionAvailable() = runTest {
        val nodeProvider = FakeNodeProvider(emptyList())
        val messageSender = FakeMessageSender()
        val dataLayerClient = WearDataLayerClient(messageSender, nodeProvider)
        val repository = WearRemoteRepository(dataLayerClient)

        repository.updateDirectSocketConfig("", 8080, enabled = false)
        repository.refreshConnectionStatus()

        val result = repository.sendCommand("SLIDE_STOP")
        assertTrue(result.isFailure)
    }
}
