package com.example.control_rem.wear.data.datalayer

import android.content.Context
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.tasks.await

data class WearNodeInfo(
    val id: String,
    val displayName: String
)

interface WearMessageSender {
    suspend fun sendMessage(nodeId: String, path: String, data: ByteArray): Result<Unit>
}

interface WearNodeProvider {
    suspend fun getConnectedNodes(): List<WearNodeInfo>
}

class PlayServicesWearMessageSender(context: Context) : WearMessageSender {
    private val messageClient = Wearable.getMessageClient(context)

    override suspend fun sendMessage(nodeId: String, path: String, data: ByteArray): Result<Unit> {
        return try {
            messageClient.sendMessage(nodeId, path, data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class PlayServicesWearNodeProvider(context: Context) : WearNodeProvider {
    private val nodeClient = Wearable.getNodeClient(context)

    override suspend fun getConnectedNodes(): List<WearNodeInfo> {
        return try {
            nodeClient.connectedNodes.await().map { node ->
                WearNodeInfo(id = node.id, displayName = node.displayName)
            }
        } catch (_: Exception) {
            emptyList()
        }
    }
}

/**
 * Implementation of [WearDataLayerClient] using Google Play Services Wearable Data Layer API.
 * Sends presentation control commands (e.g. "/remote/command" with action payload "SLIDE_NEXT")
 * to the connected Android phone app.
 */
class WearDataLayerClient(
    private val messageSender: WearMessageSender,
    private val nodeProvider: WearNodeProvider
) {
    constructor(context: Context) : this(
        PlayServicesWearMessageSender(context),
        PlayServicesWearNodeProvider(context)
    )

    companion object {
        const val REMOTE_COMMAND_PATH = "/remote/command"
    }

    /**
     * Sends presentation control commands via Wearable Data Layer to connected phone node(s).
     * @param action Presentation action payload, e.g. "SLIDE_NEXT", "SLIDE_PREV", "SLIDE_START", "SLIDE_STOP"
     */
    suspend fun sendPresentationCommand(action: String): Result<Unit> {
        return try {
            val nodes = nodeProvider.getConnectedNodes()
            if (nodes.isEmpty()) {
                return Result.failure(IllegalStateException("No phone nodes connected via Wear Data Layer"))
            }

            val payload = action.toByteArray(Charsets.UTF_8)
            var sentCount = 0
            for (node in nodes) {
                val result = messageSender.sendMessage(node.id, REMOTE_COMMAND_PATH, payload)
                if (result.isSuccess) {
                    sentCount++
                }
            }

            if (sentCount > 0) {
                Result.success(Unit)
            } else {
                Result.failure(IllegalStateException("Failed to send command to connected nodes"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Checks how many phone nodes are currently connected via Wear Data Layer.
     */
    suspend fun getConnectedNodeCount(): Int {
        return nodeProvider.getConnectedNodes().size
    }

    /**
     * Returns true if at least one phone node is connected.
     */
    suspend fun isPhoneConnected(): Boolean {
        return getConnectedNodeCount() > 0
    }
}
