package com.example.control_rem.wearable

/**
 * Singleton provider for accessing the application-wide [WearableMessageHandler] instance.
 */
object WearableMessageHandlerProvider {

    @Volatile
    private var instance: WearableMessageHandler? = null

    fun getHandler(): WearableMessageHandler {
        return instance ?: synchronized(this) {
            instance ?: WearableMessageHandlerImpl().also { instance = it }
        }
    }

    fun setHandler(handler: WearableMessageHandler) {
        synchronized(this) {
            instance = handler
        }
    }
}
