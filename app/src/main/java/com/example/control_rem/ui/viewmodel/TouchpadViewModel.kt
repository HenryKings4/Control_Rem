package com.example.control_rem.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.control_rem.data.model.ConnectionState
import com.example.control_rem.data.model.RemoteCommand
import com.example.control_rem.data.repository.RemoteControlRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TouchpadViewModel(
    private val repository: RemoteControlRepository
) : ViewModel() {

    val connectionState: StateFlow<ConnectionState> = repository.connectionState

    fun onMouseMove(dx: Float, dy: Float) {
        viewModelScope.launch {
            repository.sendCommand(RemoteCommand.MouseMove(dx, dy))
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

    fun onScroll(dy: Float) {
        viewModelScope.launch {
            repository.sendCommand(RemoteCommand.MouseScroll(dy))
        }
    }
}
