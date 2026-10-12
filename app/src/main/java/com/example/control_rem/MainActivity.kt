package com.example.control_rem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.control_rem.data.repository.RepositoryProvider
import com.example.control_rem.ui.screens.MainScreen
import com.example.control_rem.ui.theme.Control_RemTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = RepositoryProvider.repository
        setContent {
            Control_RemTheme {
                MainScreen(repository = repository)
            }
        }
    }
}
