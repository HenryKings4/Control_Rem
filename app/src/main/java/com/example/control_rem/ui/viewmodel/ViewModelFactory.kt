package com.example.control_rem.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.control_rem.data.repository.RemoteControlRepository

class ViewModelFactory(
    private val repository: RemoteControlRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ConnectionViewModel::class.java) -> {
                ConnectionViewModel(repository) as T
            }
            modelClass.isAssignableFrom(RemoteViewModel::class.java) -> {
                RemoteViewModel(repository) as T
            }
            modelClass.isAssignableFrom(TouchpadViewModel::class.java) -> {
                TouchpadViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
