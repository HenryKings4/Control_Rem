package com.example.control_rem.wear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.control_rem.wear.data.datalayer.WearDataLayerClient
import com.example.control_rem.wear.data.repository.WearRemoteRepository
import com.example.control_rem.wear.ui.screens.WearRemoteScreen
import com.example.control_rem.wear.ui.theme.WearTheme
import com.example.control_rem.wear.ui.viewmodel.WearRemoteViewModel

class WearMainActivity : ComponentActivity() {

    private lateinit var viewModel: WearRemoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataLayerClient = WearDataLayerClient(applicationContext)
        val repository = WearRemoteRepository(dataLayerClient)
        viewModel = WearRemoteViewModel(repository)

        setContent {
            WearTheme {
                val connectionStatus by viewModel.connectionStatus.collectAsState()
                val lastActionSent by viewModel.lastActionSent.collectAsState()
                val lastErrorMessage by viewModel.lastErrorMessage.collectAsState()

                WearRemoteScreen(
                    connectionStatus = connectionStatus,
                    lastActionSent = lastActionSent,
                    lastErrorMessage = lastErrorMessage,
                    onNextSlide = viewModel::onNextSlide,
                    onPrevSlide = viewModel::onPrevSlide,
                    onStartPresentation = viewModel::onStartPresentation,
                    onStopPresentation = viewModel::onStopPresentation,
                    onGoToSlide = viewModel::goToSlide,
                    onRefreshStatus = viewModel::refreshConnectionStatus,
                    onUpdateSocketConfig = viewModel::updateDirectSocketConfig
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::viewModel.isInitialized) {
            viewModel.refreshConnectionStatus()
        }
    }
}
