package com.example.control_rem.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents commands sent from the mobile app or Wear OS smartwatch to the PC receiver server.
 */
@Serializable
sealed class RemoteCommand {

    // Presentation Slide Commands
    @Serializable
    @SerialName("SLIDE_NEXT")
    data object SlideNext : RemoteCommand()

    @Serializable
    @SerialName("SLIDE_PREV")
    data object SlidePrev : RemoteCommand()

    @Serializable
    @SerialName("SLIDE_START")
    data object SlideStart : RemoteCommand()

    @Serializable
    @SerialName("SLIDE_STOP")
    data object SlideStop : RemoteCommand()

    @Serializable
    @SerialName("SLIDE_GOTO")
    data class SlideGoTo(val slideNumber: Int) : RemoteCommand()

    @Serializable
    @SerialName("BLANK_SCREEN")
    data object BlankScreen : RemoteCommand()

    // Mouse / Touchpad Commands
    @Serializable
    @SerialName("MOUSE_MOVE")
    data class MouseMove(val dx: Float, val dy: Float) : RemoteCommand()

    @Serializable
    @SerialName("MOUSE_LEFT_CLICK")
    data object MouseLeftClick : RemoteCommand()

    @Serializable
    @SerialName("MOUSE_RIGHT_CLICK")
    data object MouseRightClick : RemoteCommand()

    @Serializable
    @SerialName("MOUSE_DOUBLE_CLICK")
    data object MouseDoubleClick : RemoteCommand()

    @Serializable
    @SerialName("MOUSE_SCROLL")
    data class MouseScroll(val dy: Float) : RemoteCommand()

    // Utility Commands
    @Serializable
    @SerialName("PING")
    data object Ping : RemoteCommand()

    @Serializable
    @SerialName("CUSTOM")
    data class Custom(val rawCommand: String) : RemoteCommand()
}
