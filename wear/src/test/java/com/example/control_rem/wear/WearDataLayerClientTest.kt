package com.example.control_rem.wear

import com.example.control_rem.wear.data.datalayer.WearDataLayerClient
import com.example.control_rem.wear.data.datalayer.WearMessageSender
import com.example.control_rem.wear.data.datalayer.WearNodeInfo
import com.example.control_rem.wear.data.datalayer.WearNodeProvider
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WearDataLayerClientTest {

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
    fun testSendPresentationCommandWithConnectedPhone() = runTest {
        val fakeNodes = listOf(WearNodeInfo("phone_123", "Pixel 8"))
        val fakeSender = FakeMessageSender()
        val fakeProvider = FakeNodeProvider(fakeNodes)

        val client = WearDataLayerClient(fakeSender, fakeProvider)

        assertTrue(client.isPhoneConnected())
        assertEquals(1, client.getConnectedNodeCount())

        val result = client.sendPresentationCommand("SLIDE_NEXT")
        assertTrue(result.isSuccess)

        assertEquals(1, fakeSender.sentMessages.size)
        val (path, action) = fakeSender.sentMessages[0]
        assertEquals("/remote/command", path)
        assertEquals("SLIDE_NEXT", action)
    }

    @Test
    fun testSendPresentationCommandWithNoConnectedNodesFails() = runTest {
        val fakeSender = FakeMessageSender()
        val fakeProvider = FakeNodeProvider(emptyList())

        val client = WearDataLayerClient(fakeSender, fakeProvider)

        assertFalse(client.isPhoneConnected())
        assertEquals(0, client.getConnectedNodeCount())

        val result = client.sendPresentationCommand("SLIDE_PREV")
        assertTrue(result.isFailure)
        assertEquals(0, fakeSender.sentMessages.size)
    }
}
