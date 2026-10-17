package com.example.control_rem.wearable

import com.example.control_rem.data.model.RemoteCommand
import com.example.control_rem.data.protocol.CommandSerializer
import com.example.control_rem.data.repository.RemoteControlRepository
import com.example.control_rem.data.repository.RepositoryProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class WearableMessageHandlerImpl : WearableMessageHandler {

    private val _receivedWearableCommands = MutableSharedFlow<RemoteCommand>(extraBufferCapacity = 64)
    override val receivedWearableCommands: SharedFlow<RemoteCommand> = _receivedWearableCommands.asSharedFlow()

    private var boundRepository: RemoteControlRepository? = null

    override fun bindRepository(repository: RemoteControlRepository) {
        this.boundRepository = repository
    }

    override suspend fun handleWearableMessage(path: String, data: ByteArray?): Result<RemoteCommand> {
        val command = parsePathAndData(path, data)
            ?: return Result.failure(IllegalArgumentException("Unable to parse Wear OS message from path: $path"))

        _receivedWearableCommands.emit(command)

        val repository = boundRepository ?: RepositoryProvider.repository
        val sendResult = repository.sendCommand(command)

        return if (sendResult.isSuccess) {
            Result.success(command)
        } else {
            Result.failure(sendResult.exceptionOrNull() ?: Exception("Failed to forward command to PC"))
        }
    }

    private fun parsePathAndData(path: String, data: ByteArray?): RemoteCommand? {
        val normalizedPath = path.trim().lowercase()

        // 1. Direct path mappings
        when {
            normalizedPath.endsWith("/slide/next") || normalizedPath.endsWith("/slide_next") -> return RemoteCommand.SlideNext
            normalizedPath.endsWith("/slide/prev") || normalizedPath.endsWith("/slide_prev") -> return RemoteCommand.SlidePrev
            normalizedPath.endsWith("/slide/start") || normalizedPath.endsWith("/slide_start") -> return RemoteCommand.SlideStart
            normalizedPath.endsWith("/slide/stop") || normalizedPath.endsWith("/slide_stop") -> return RemoteCommand.SlideStop
            normalizedPath.endsWith("/blank_screen") || normalizedPath.endsWith("/blank") -> return RemoteCommand.BlankScreen
            normalizedPath.endsWith("/mouse/left_click") || normalizedPath.endsWith("/left_click") -> return RemoteCommand.MouseLeftClick
            normalizedPath.endsWith("/mouse/right_click") || normalizedPath.endsWith("/right_click") -> return RemoteCommand.MouseRightClick
            normalizedPath.endsWith("/mouse/double_click") || normalizedPath.endsWith("/double_click") -> return RemoteCommand.MouseDoubleClick
        }

        // 2. Data payload parsing
        if (data != null && data.isNotEmpty()) {
            val payloadText = String(data, Charsets.UTF_8).trim()

            if (normalizedPath.endsWith("/mouse/move")) {
                val parsedMove = CommandSerializer.parseTextProtocol("MOUSE_MOVE:$payloadText")
                if (parsedMove is RemoteCommand.MouseMove) return parsedMove
            }

            if (normalizedPath.endsWith("/mouse/scroll")) {
                val parsedScroll = CommandSerializer.parseTextProtocol("MOUSE_SCROLL:$payloadText")
                if (parsedScroll is RemoteCommand.MouseScroll) return parsedScroll
            }

            // Fallback parsing via CommandSerializer
            val serializedCmd = CommandSerializer.fromBytes(data)
            if (serializedCmd != null) return serializedCmd
        }

        return null
    }
}
