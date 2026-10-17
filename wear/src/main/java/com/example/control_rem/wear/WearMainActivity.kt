package com.example.control_rem.wear

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.control_rem.wear.data.datalayer.WearDataLayerClient
import com.example.control_rem.wear.data.repository.WearRemoteRepository
import com.example.control_rem.wear.ui.screens.WearRemoteScreen
import com.example.control_rem.wear.ui.theme.WearTheme
import com.example.control_rem.wear.ui.viewmodel.WearRemoteViewModel
import android.view.KeyEvent
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import androidx.wear.ambient.AmbientModeSupport

class WearMainActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider {

    private lateinit var viewModel: WearRemoteViewModel
    private lateinit var ambientController: AmbientModeSupport.AmbientController
    
    // Configuración para el modo ambiente
    private val ambientCallback = object : AmbientModeSupport.AmbientCallback() {
        override fun onEnterAmbient(ambientDetails: Bundle?) {
            super.onEnterAmbient(ambientDetails)
            // Entra en modo ambiente (pantalla atenuada)
            viewModel.setIsAmbient(true)
        }

        override fun onExitAmbient() {
            super.onExitAmbient()
            // Sale del modo ambiente (pantalla encendida)
            viewModel.setIsAmbient(false)
        }

        override fun onUpdateAmbient() {
            super.onUpdateAmbient()
            // Actualización periódica en modo ambiente
        }
    }

    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback {
        return ambientCallback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Habilitar Always-on Display
        ambientController = AmbientModeSupport.attach(this)
        
        // Permitir que Wear OS maneje el modo ambiente, NO forzar que la pantalla
        // siempre esté 100% activa. Removemos window.addFlags(FLAG_KEEP_SCREEN_ON)
        
        // Para que la app se mantenga activa y no vuelva al watchface cuando se
        // duerme (modo ambiente), necesitamos ambientController.isAmbientEnabled = true
        // el cual ya está habilitado mediante el attach(this)


        val dataLayerClient = WearDataLayerClient(applicationContext)
        val repository = WearRemoteRepository(dataLayerClient)
        viewModel = WearRemoteViewModel(repository)

        setContent {
            WearTheme {
                val connectionStatus by viewModel.connectionStatus.collectAsState()
                val lastActionSent by viewModel.lastActionSent.collectAsState()
                val lastErrorMessage by viewModel.lastErrorMessage.collectAsState()
                val isAmbient by viewModel.isAmbient.collectAsState()

                WearRemoteScreen(
                    isAmbient = isAmbient,
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

    // Interceptar eventos de botones físicos (corona) o gestos de muñeca
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_NAVIGATE_NEXT -> {
                // Gesto de muñeca: "Flick wrist out" (Girar muñeca hacia afuera)
                // O girar corona hacia abajo
                viewModel.onNextSlide()
                true
            }
            KeyEvent.KEYCODE_NAVIGATE_PREVIOUS -> {
                // Gesto de muñeca: "Flick wrist in" (Girar muñeca hacia adentro)
                // O girar corona hacia arriba
                viewModel.onPrevSlide()
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }
}
