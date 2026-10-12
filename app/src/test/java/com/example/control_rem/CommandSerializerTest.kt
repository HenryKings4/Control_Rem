package com.example.control_rem

import com.example.control_rem.data.model.RemoteCommand
import com.example.control_rem.data.protocol.CommandSerializer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CommandSerializerTest {

    @Test
    fun testSlideCommandsTextSerialization() {
        val nextText = CommandSerializer.toTextProtocol(RemoteCommand.SlideNext, includeNewline = false)
        assertEquals("SLIDE_NEXT", nextText)

        val parsedNext = CommandSerializer.parseTextProtocol("SLIDE_NEXT")
        assertEquals(RemoteCommand.SlideNext, parsedNext)

        val prevText = CommandSerializer.toTextProtocol(RemoteCommand.SlidePrev, includeNewline = false)
        assertEquals("SLIDE_PREV", prevText)

        val parsedPrev = CommandSerializer.parseTextProtocol("SLIDE_PREV")
        assertEquals(RemoteCommand.SlidePrev, parsedPrev)

        val startParsed = CommandSerializer.parseTextProtocol("SLIDE_START")
        assertEquals(RemoteCommand.SlideStart, startParsed)

        val stopParsed = CommandSerializer.parseTextProtocol("SLIDE_STOP")
        assertEquals(RemoteCommand.SlideStop, stopParsed)

        val blankParsed = CommandSerializer.parseTextProtocol("BLANK_SCREEN")
        assertEquals(RemoteCommand.BlankScreen, blankParsed)
    }

    @Test
    fun testMouseCommandsTextSerialization() {
        val move = RemoteCommand.MouseMove(dx = 12.5f, dy = -8.2f)
        val moveText = CommandSerializer.toTextProtocol(move, includeNewline = false)
        assertEquals("MOUSE_MOVE:12.5,-8.2", moveText)

        val parsedMove = CommandSerializer.parseTextProtocol("MOUSE_MOVE:12.5,-8.2")
        assertTrue(parsedMove is RemoteCommand.MouseMove)
        val moveResult = parsedMove as RemoteCommand.MouseMove
        assertEquals(12.5f, moveResult.dx, 0.001f)
        assertEquals(-8.2f, moveResult.dy, 0.001f)

        val scroll = RemoteCommand.MouseScroll(dy = 3.5f)
        val scrollText = CommandSerializer.toTextProtocol(scroll, includeNewline = false)
        assertEquals("MOUSE_SCROLL:3.5", scrollText)

        val parsedScroll = CommandSerializer.parseTextProtocol("MOUSE_SCROLL:3.5")
        assertTrue(parsedScroll is RemoteCommand.MouseScroll)
        assertEquals(3.5f, (parsedScroll as RemoteCommand.MouseScroll).dy, 0.001f)

        val leftClick = CommandSerializer.parseTextProtocol("MOUSE_LEFT_CLICK")
        assertEquals(RemoteCommand.MouseLeftClick, leftClick)

        val rightClick = CommandSerializer.parseTextProtocol("MOUSE_RIGHT_CLICK")
        assertEquals(RemoteCommand.MouseRightClick, rightClick)

        val doubleClick = CommandSerializer.parseTextProtocol("MOUSE_DOUBLE_CLICK")
        assertEquals(RemoteCommand.MouseDoubleClick, doubleClick)
    }

    @Test
    fun testJsonSerialization() {
        val moveCommand = RemoteCommand.MouseMove(dx = 5.0f, dy = 10.0f)
        val jsonStr = CommandSerializer.toJson(moveCommand)
        assertNotNull(jsonStr)

        val parsed = CommandSerializer.parseJson(jsonStr)
        assertNotNull(parsed)
        assertTrue(parsed is RemoteCommand.MouseMove)
        val moveParsed = parsed as RemoteCommand.MouseMove
        assertEquals(5.0f, moveParsed.dx, 0.001f)
        assertEquals(10.0f, moveParsed.dy, 0.001f)
    }

    @Test
    fun testBytesConversion() {
        val command = RemoteCommand.SlideNext
        val bytes = CommandSerializer.toBytes(command)
        val parsed = CommandSerializer.fromBytes(bytes)
        assertEquals(RemoteCommand.SlideNext, parsed)
    }
}
