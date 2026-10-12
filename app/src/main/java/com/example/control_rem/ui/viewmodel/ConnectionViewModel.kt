package com.example.control_rem.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.control_rem.data.model.ConnectionState
import com.example.control_rem.data.model.NetworkConfig
import com.example.control_rem.data.model.ProtocolType
import com.example.control_rem.data.model.RemoteCommand
import com.example.control_rem.data.repository.RemoteControlRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConnectionViewModel(
    private val repository: RemoteControlRepository
) : ViewModel() {

    val connectionState: StateFlow<ConnectionState> = repository.connectionState
    val currentConfig: StateFlow<NetworkConfig> = repository.currentConfig
    val commandHistory: StateFlow<List<RemoteCommand>> = repository.commandHistory

    private val _ipInput = MutableStateFlow(repository.currentConfig.value.ipAddress)
    val ipInput: StateFlow<String> = _ipInput.asStateFlow()

    private val _portInput = MutableStateFlow(repository.currentConfig.value.port.toString())
    val portInput: StateFlow<String> = _portInput.asStateFlow()

    private val _selectedProtocol = MutableStateFlow(repository.currentConfig.value.protocolType)
    val selectedProtocol: StateFlow<ProtocolType> = _selectedProtocol.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun onIpChanged(newIp: String) {
        _ipInput.value = newIp
        _errorMessage.value = null
    }

    fun onPortChanged(newPort: String) {
        if (newPort.all { it.isDigit() }) {
            _portInput.value = newPort
            _errorMessage.value = null
        }
    }

    fun onProtocolSelected(protocol: ProtocolType) {
        _selectedProtocol.value = protocol
    }

    fun connect() {
        val ip = _ipInput.value.trim()
        val portStr = _portInput.value.trim()

        if (ip.isBlank()) {
            _errorMessage.value = "Ingresa una dirección IP válida"
            return
        }

        val port = portStr.toIntOrNull()
        if (port == null || port !in 1..65535) {
            _errorMessage.value = "Ingresa un puerto válido (1-65535)"
            return
        }

        _errorMessage.value = null
        viewModelScope.launch {
            val result = repository.connect(ip, port, _selectedProtocol.value)
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.localizedMessage ?: "Error al conectar"
            }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            repository.disconnect()
        }
    }

    fun clearHistory() {
        repository.clearCommandHistory()
    }
}
