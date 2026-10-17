package com.example.control_rem.wearable

import com.example.control_rem.data.repository.RepositoryProvider
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Service extending [WearableListenerService] from Google Play Services Wearable Data Layer API.
 * Listens for incoming messages sent from Wear OS smartwatches (e.g. Moto 360, Galaxy Watch)
 * and processes/forwards them seamlessly to the PC receiver server.
 */
class PhoneWearableListenerService : WearableListenerService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        RepositoryProvider.initialize(applicationContext)
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)
        val path = messageEvent.path
        val data = messageEvent.data

        val handler = WearableMessageHandlerProvider.getHandler()
        serviceScope.launch {
            handler.handleWearableMessage(path, data)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
