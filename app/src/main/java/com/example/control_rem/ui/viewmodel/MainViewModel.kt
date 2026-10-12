package com.example.control_rem.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.control_rem.data.model.ConnectionState
import com.example.control_rem.data.repository.RemoteControlRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class NavScreen(val title: String) {
    CONNECTION("Conectar"),
    REMOTE("Presentación"),
    TOUCHPAD("Touchpad")
}

class MainViewModel(
    val repository: RemoteControlRepository
) : ViewModel() {

    private val _currentScreen = MutableStateFlow(NavScreen.CONNECTION)
    val currentScreen: StateFlow<NavScreen> = _currentScreen.asStateFlow()

    val connectionState: StateFlow<ConnectionState> = repository.connectionState

    fun selectScreen(screen: NavScreen) {
        _currentScreen.value = screen
    }
}
