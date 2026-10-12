package com.example.control_rem.wear.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.control_rem.wear.data.model.WearConnectionStatus
import com.example.control_rem.wear.data.repository.WearRemoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WearRemoteViewModel(
    private val repository: WearRemoteRepository
) : ViewModel() {

    val connectionStatus: StateFlow<WearConnectionStatus> = repository.connectionStatus

    private val _lastActionSent = MutableStateFlow<String?>(null)
    val lastActionSent: StateFlow<String?> = _lastActionSent.asStateFlow()

    private val _lastErrorMessage = MutableStateFlow<String?>(null)
    val lastErrorMessage: StateFlow<String?> = _lastErrorMessage.asStateFlow()

    init {
        refreshConnectionStatus()
    }

    fun refreshConnectionStatus() {
        viewModelScope.launch {
            repository.refreshConnectionStatus()
        }
    }

    fun onNextSlide() {
        sendCommand("SLIDE_NEXT")
    }

    fun onPrevSlide() {
        sendCommand("SLIDE_PREV")
    }

    fun onStartPresentation() {
        sendCommand("SLIDE_START")
    }

    fun onStopPresentation() {
        sendCommand("SLIDE_STOP")
    }

    fun goToSlide(slide: Int) {
        sendCommand("SLIDE_GOTO:$slide")
    }

    fun sendCommand(action: String) {
        viewModelScope.launch {
            _lastErrorMessage.value = null
            val result = repository.sendCommand(action)
            if (result.isSuccess) {
                _lastActionSent.value = action
            } else {
                _lastErrorMessage.value = result.exceptionOrNull()?.message ?: "Command failed"
            }
        }
    }

    fun updateDirectSocketConfig(ipAddress: String, port: Int, enabled: Boolean) {
        repository.updateDirectSocketConfig(ipAddress, port, enabled)
        refreshConnectionStatus()
    }

    fun clearError() {
        _lastErrorMessage.value = null
    }
}
