package com.example.control_rem.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.control_rem.data.model.ConnectionState
import com.example.control_rem.data.model.RemoteCommand
import com.example.control_rem.data.repository.RemoteControlRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RemoteViewModel(
    private val repository: RemoteControlRepository
) : ViewModel() {

    val connectionState: StateFlow<ConnectionState> = repository.connectionState

    fun sendNextSlide() {
        viewModelScope.launch {
            repository.sendCommand(RemoteCommand.SlideNext)
        }
    }

    fun sendPrevSlide() {
        viewModelScope.launch {
            repository.sendCommand(RemoteCommand.SlidePrev)
        }
    }

    fun sendStartPresentation() {
        viewModelScope.launch {
            repository.sendCommand(RemoteCommand.SlideStart)
        }
    }

    fun sendStopPresentation() {
        viewModelScope.launch {
            repository.sendCommand(RemoteCommand.SlideStop)
        }
    }

    fun sendBlankScreen() {
        viewModelScope.launch {
            repository.sendCommand(RemoteCommand.BlankScreen)
        }
    }
}
