package com.example.control_rem.ui

import com.example.control_rem.data.model.ConnectionState
import com.example.control_rem.data.model.NetworkConfig
import com.example.control_rem.data.model.ProtocolType
import com.example.control_rem.data.model.RemoteCommand
import com.example.control_rem.data.repository.RemoteControlRepository
import com.example.control_rem.ui.viewmodel.ConnectionViewModel
import com.example.control_rem.ui.viewmodel.MainViewModel
import com.example.control_rem.ui.viewmodel.NavScreen
import com.example.control_rem.ui.viewmodel.RemoteViewModel
import com.example.control_rem.ui.viewmodel.TouchpadViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelsTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeRemoteControlRepository : RemoteControlRepository {
        private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
        override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

        private val _currentConfig = MutableStateFlow(NetworkConfig("192.168.1.100", 8080, ProtocolType.TCP))
        override val currentConfig: StateFlow<NetworkConfig> = _currentConfig.asStateFlow()

        private val _commandHistory = MutableStateFlow<List<RemoteCommand>>(emptyList())
        override val commandHistory: StateFlow<List<RemoteCommand>> = _commandHistory.asStateFlow()

        val sentCommands = mutableListOf<RemoteCommand>()

        override suspend fun connect(ipAddress: String, port: Int, protocolType: ProtocolType): Result<Unit> {
            _currentConfig.value = NetworkConfig(ipAddress, port, protocolType)
            _connectionState.value = ConnectionState.Connected(ipAddress, port, protocolType)
            return Result.success(Unit)
        }

        override suspend fun disconnect() {
            _connectionState.value = ConnectionState.Disconnected
        }

        override suspend fun sendCommand(command: RemoteCommand): Result<Unit> {
            sentCommands.add(command)
            _commandHistory.value = listOf(command) + _commandHistory.value
            return Result.success(Unit)
        }

        override suspend fun sendRawPayload(payload: String): Result<Unit> {
            return Result.success(Unit)
        }

        override suspend fun updateConfig(config: NetworkConfig) {
            _currentConfig.value = config
        }

        override fun clearCommandHistory() {
            _commandHistory.value = emptyList()
        }
    }

    private lateinit var fakeRepository: FakeRemoteControlRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeRemoteControlRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testMainViewModelScreenSelection() {
        val viewModel = MainViewModel(fakeRepository)
        assertEquals(NavScreen.CONNECTION, viewModel.currentScreen.value)

        viewModel.selectScreen(NavScreen.REMOTE)
        assertEquals(NavScreen.REMOTE, viewModel.currentScreen.value)

        viewModel.selectScreen(NavScreen.TOUCHPAD)
        assertEquals(NavScreen.TOUCHPAD, viewModel.currentScreen.value)
    }

    @Test
    fun testConnectionViewModelConnectAndDisconnect() = runTest {
        val viewModel = ConnectionViewModel(fakeRepository)

        viewModel.onIpChanged("192.168.1.50")
        viewModel.onPortChanged("9000")
        viewModel.onProtocolSelected(ProtocolType.UDP)

        viewModel.connect()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(fakeRepository.connectionState.value is ConnectionState.Connected)
        assertEquals("192.168.1.50", fakeRepository.currentConfig.value.ipAddress)
        assertEquals(9000, fakeRepository.currentConfig.value.port)
        assertEquals(ProtocolType.UDP, fakeRepository.currentConfig.value.protocolType)

        viewModel.disconnect()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(fakeRepository.connectionState.value is ConnectionState.Disconnected)
    }

    @Test
    fun testConnectionViewModelInvalidInputValidation() {
        val viewModel = ConnectionViewModel(fakeRepository)

        viewModel.onIpChanged("")
        viewModel.connect()
        assertNotNull(viewModel.errorMessage.value)

        viewModel.onIpChanged("192.168.1.1")
        viewModel.onPortChanged("999999") // Invalid port
        viewModel.connect()
        assertNotNull(viewModel.errorMessage.value)
    }

    @Test
    fun testRemoteViewModelSlideCommands() = runTest {
        val viewModel = RemoteViewModel(fakeRepository)

        viewModel.sendNextSlide()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(RemoteCommand.SlideNext, fakeRepository.sentCommands.last())

        viewModel.sendPrevSlide()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(RemoteCommand.SlidePrev, fakeRepository.sentCommands.last())

        viewModel.sendStartPresentation()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(RemoteCommand.SlideStart, fakeRepository.sentCommands.last())

        viewModel.sendStopPresentation()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(RemoteCommand.SlideStop, fakeRepository.sentCommands.last())

        viewModel.sendBlankScreen()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(RemoteCommand.BlankScreen, fakeRepository.sentCommands.last())
    }

    @Test
    fun testTouchpadViewModelMouseCommands() = runTest {
        val viewModel = TouchpadViewModel(fakeRepository)

        viewModel.onMouseMove(15.5f, -10.0f)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(RemoteCommand.MouseMove(15.5f, -10.0f), fakeRepository.sentCommands.last())

        viewModel.onLeftClick()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(RemoteCommand.MouseLeftClick, fakeRepository.sentCommands.last())

        viewModel.onRightClick()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(RemoteCommand.MouseRightClick, fakeRepository.sentCommands.last())

        viewModel.onDoubleClick()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(RemoteCommand.MouseDoubleClick, fakeRepository.sentCommands.last())

        viewModel.onScroll(25f)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(RemoteCommand.MouseScroll(25f), fakeRepository.sentCommands.last())
    }
}
