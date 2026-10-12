package com.example.control_rem.wear

import com.example.control_rem.wear.data.datalayer.WearDataLayerClient
import com.example.control_rem.wear.data.datalayer.WearMessageSender
import com.example.control_rem.wear.data.datalayer.WearNodeInfo
import com.example.control_rem.wear.data.datalayer.WearNodeProvider
import com.example.control_rem.wear.data.repository.WearRemoteRepository
import com.example.control_rem.wear.ui.viewmodel.WearRemoteViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WearRemoteViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeMessageSender : WearMessageSender {
        val sentActions = mutableListOf<String>()

        override suspend fun sendMessage(nodeId: String, path: String, data: ByteArray): Result<Unit> {
            sentActions.add(String(data, Charsets.UTF_8))
            return Result.success(Unit)
        }
    }

    private class FakeNodeProvider(private val connected: Boolean) : WearNodeProvider {
        override suspend fun getConnectedNodes(): List<WearNodeInfo> {
            return if (connected) listOf(WearNodeInfo("node_phone", "Phone")) else emptyList()
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testViewModelCommands() = runTest {
        val messageSender = FakeMessageSender()
        val nodeProvider = FakeNodeProvider(connected = true)
        val dataLayerClient = WearDataLayerClient(messageSender, nodeProvider)
        val repository = WearRemoteRepository(dataLayerClient)
        val viewModel = WearRemoteViewModel(repository)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onNextSlide()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("SLIDE_NEXT", viewModel.lastActionSent.value)

        viewModel.onPrevSlide()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("SLIDE_PREV", viewModel.lastActionSent.value)

        viewModel.onStartPresentation()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("SLIDE_START", viewModel.lastActionSent.value)

        viewModel.onStopPresentation()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("SLIDE_STOP", viewModel.lastActionSent.value)

        assertEquals(listOf("SLIDE_NEXT", "SLIDE_PREV", "SLIDE_START", "SLIDE_STOP"), messageSender.sentActions)
    }

    @Test
    fun testViewModelErrorHandlingWhenDisconnected() = runTest {
        val messageSender = FakeMessageSender()
        val nodeProvider = FakeNodeProvider(connected = false)
        val dataLayerClient = WearDataLayerClient(messageSender, nodeProvider)
        val repository = WearRemoteRepository(dataLayerClient)
        repository.updateDirectSocketConfig("", 8080, enabled = false)

        val viewModel = WearRemoteViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onNextSlide()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.lastErrorMessage.value)

        viewModel.clearError()
        assertNull(viewModel.lastErrorMessage.value)
    }
}
