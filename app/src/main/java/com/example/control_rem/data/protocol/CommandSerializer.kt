package com.example.control_rem.data.protocol

import com.example.control_rem.data.model.RemoteCommand
import kotlinx.serialization.json.Json

/**
 * Protocol serializer and parser for converting [RemoteCommand] instances into
 * JSON strings, lightweight text protocol strings, and byte payloads.
 */
object CommandSerializer {

    private val jsonInstance = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        classDiscriminator = "action"
    }

    /**
     * Serializes a [RemoteCommand] to a JSON string payload.
     */
    fun toJson(command: RemoteCommand): String {
        return jsonInstance.encodeToString(RemoteCommand.serializer(), command)
    }

    /**
     * Deserializes a JSON string payload to a [RemoteCommand].
     */
    fun parseJson(jsonString: String): RemoteCommand? {
        return try {
            jsonInstance.decodeFromString(RemoteCommand.serializer(), jsonString.trim())
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Converts a [RemoteCommand] to a lightweight text protocol string.
     * Examples:
     * - "SLIDE_NEXT\n"
     * - "MOUSE_MOVE:10.5,-5.0\n"
     * - "MOUSE_SCROLL:2.0\n"
     */
    fun toTextProtocol(command: RemoteCommand, includeNewline: Boolean = true): String {
        val base = when (command) {
            is RemoteCommand.SlideNext -> "SLIDE_NEXT"
            is RemoteCommand.SlidePrev -> "SLIDE_PREV"
            is RemoteCommand.SlideStart -> "SLIDE_START"
            is RemoteCommand.SlideStop -> "SLIDE_STOP"
            is RemoteCommand.BlankScreen -> "BLANK_SCREEN"
            is RemoteCommand.MouseMove -> "MOUSE_MOVE:${command.dx},${command.dy}"
            is RemoteCommand.MouseLeftClick -> "MOUSE_LEFT_CLICK"
            is RemoteCommand.MouseRightClick -> "MOUSE_RIGHT_CLICK"
            is RemoteCommand.MouseDoubleClick -> "MOUSE_DOUBLE_CLICK"
            is RemoteCommand.MouseScroll -> "MOUSE_SCROLL:${command.dy}"
            is RemoteCommand.Ping -> "PING"
            is RemoteCommand.Custom -> command.rawCommand
        }
        return if (includeNewline) "$base\n" else base
    }

    /**
     * Parses a text command string or JSON string into a [RemoteCommand].
     */
    fun parseTextProtocol(text: String): RemoteCommand? {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return null

        // Attempt JSON deserialization if string starts with '{'
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            val jsonParsed = parseJson(trimmed)
            if (jsonParsed != null) return jsonParsed
        }

        return when {
            trimmed.equals("SLIDE_NEXT", ignoreCase = true) -> RemoteCommand.SlideNext
            trimmed.equals("SLIDE_PREV", ignoreCase = true) -> RemoteCommand.SlidePrev
            trimmed.equals("SLIDE_START", ignoreCase = true) -> RemoteCommand.SlideStart
            trimmed.equals("SLIDE_STOP", ignoreCase = true) -> RemoteCommand.SlideStop
            trimmed.equals("BLANK_SCREEN", ignoreCase = true) -> RemoteCommand.BlankScreen
            trimmed.equals("MOUSE_LEFT_CLICK", ignoreCase = true) -> RemoteCommand.MouseLeftClick
            trimmed.equals("MOUSE_RIGHT_CLICK", ignoreCase = true) -> RemoteCommand.MouseRightClick
            trimmed.equals("MOUSE_DOUBLE_CLICK", ignoreCase = true) -> RemoteCommand.MouseDoubleClick
            trimmed.equals("PING", ignoreCase = true) -> RemoteCommand.Ping

            trimmed.startsWith("MOUSE_MOVE", ignoreCase = true) -> {
                val coordsStr = trimmed.substringAfter("MOUSE_MOVE").trim(':', ' ', '=')
                parseCoordinates(coordsStr)?.let { (dx, dy) ->
                    RemoteCommand.MouseMove(dx, dy)
                }
            }

            trimmed.startsWith("MOUSE_SCROLL", ignoreCase = true) -> {
                val valueStr = trimmed.substringAfter("MOUSE_SCROLL").trim(':', ' ', '=')
                valueStr.toFloatOrNull()?.let { dy ->
                    RemoteCommand.MouseScroll(dy)
                }
            }

            else -> RemoteCommand.Custom(trimmed)
        }
    }

    private fun parseCoordinates(coordsString: String): Pair<Float, Float>? {
        val parts = coordsString.split(",", " ")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        if (parts.size >= 2) {
            val dx = parts[0].toFloatOrNull()
            val dy = parts[1].toFloatOrNull()
            if (dx != null && dy != null) {
                return Pair(dx, dy)
            }
        }
        return null
    }

    /**
     * Encodes a command to UTF-8 byte array for network or Wearable Data Layer transmission.
     */
    fun toBytes(command: RemoteCommand, useJson: Boolean = false): ByteArray {
        val str = if (useJson) toJson(command) else toTextProtocol(command, includeNewline = true)
        return str.toByteArray(Charsets.UTF_8)
    }

    /**
     * Parses byte array payload to [RemoteCommand].
     */
    fun fromBytes(bytes: ByteArray): RemoteCommand? {
        val text = String(bytes, Charsets.UTF_8)
        return parseTextProtocol(text)
    }
}
