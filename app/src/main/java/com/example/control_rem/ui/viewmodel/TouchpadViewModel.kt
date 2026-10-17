package com.example.control_rem.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.control_rem.data.model.ConnectionState
import com.example.control_rem.data.model.RemoteCommand
import com.example.control_rem.data.repository.RemoteControlRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel managing the interactive trackpad/touchpad screen.
 * Implements high-performance move throttling (~60 FPS aggregation) to prevent
 * coroutine spam, GC thrashing, and network lag during fast finger dragging.
 */
class TouchpadViewModel(
    private val repository: RemoteControlRepository
) : ViewModel() {

    val connectionState: StateFlow<ConnectionState> = repository.connectionState

    // Mouse movement throttling state
    private val moveLock = Any()
    private var accumulatedDx = 0f
    private var accumulatedDy = 0f
    private var isMoveLoopRunning = false

    // Scroll throttling state
    private val scrollLock = Any()
    private var accumulatedScroll = 0f
    private var isScrollLoopRunning = false

    fun onMouseMove(dx: Float, dy: Float) {
        synchronized(moveLock) {
            accumulatedDx += dx
            accumulatedDy += dy
            if (!isMoveLoopRunning) {
                isMoveLoopRunning = true
                viewModelScope.launch {
                    runMoveLoop()
                }
            }
        }
    }

    private suspend fun runMoveLoop() {
        while (true) {
            delay(16L) // ~60 FPS rate limit
            var dxToSend = 0f
            var dyToSend = 0f
            synchronized(moveLock) {
                if (accumulatedDx == 0f && accumulatedDy == 0f) {
                    isMoveLoopRunning = false
                    return
                }
                dxToSend = accumulatedDx
                dyToSend = accumulatedDy
                accumulatedDx = 0f
                accumulatedDy = 0f
            }
            if (dxToSend != 0f || dyToSend != 0f) {
                repository.sendCommand(RemoteCommand.MouseMove(dxToSend, dyToSend))
            }
        }
    }

    fun onScroll(dy: Float) {
        synchronized(scrollLock) {
            accumulatedScroll += dy
            if (!isScrollLoopRunning) {
                isScrollLoopRunning = true
                viewModelScope.launch {
                    runScrollLoop()
                }
            }
        }
    }

    private suspend fun runScrollLoop() {
        while (true) {
            delay(20L) // 50 FPS rate limit
            var scrollToSend = 0f
            synchronized(scrollLock) {
                if (accumulatedScroll == 0f) {
                    isScrollLoopRunning = false
                    return
                }
                scrollToSend = accumulatedScroll
                accumulatedScroll = 0f
            }
            if (scrollToSend != 0f) {
                repository.sendCommand(RemoteCommand.MouseScroll(scrollToSend))
            }
        }
    }

    fun onLeftClick() {
        viewModelScope.launch {
            repository.sendCommand(RemoteCommand.MouseLeftClick)
        }
    }

    fun onRightClick() {
        viewModelScope.launch {
            repository.sendCommand(RemoteCommand.MouseRightClick)
        }
    }

    fun onDoubleClick() {
        viewModelScope.launch {
            repository.sendCommand(RemoteCommand.MouseDoubleClick)
        }
    }
}
