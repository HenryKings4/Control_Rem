package com.example.control_rem.wear.data.socket

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

/**
 * Direct socket client for standalone Wi-Fi mode on Wear OS.
 * Enables smartwatches (like Moto 360 Gen 1, Galaxy Watch, Pixel Watch) to directly control
 * presentation slides on the PC server when disconnected from the phone.
 */
class WearDirectSocketClient {

    /**
     * Sends a command directly to target IP and port over TCP or UDP socket.
     */
    suspend fun sendCommand(
        ipAddress: String,
        port: Int,
        action: String,
        useUdp: Boolean = false,
        timeoutMs: Int = 3000
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (ipAddress.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("IP address cannot be empty"))
            }

            val formattedPayload = if (action.endsWith("\n")) action else "$action\n"
            val bytes = formattedPayload.toByteArray(Charsets.UTF_8)

            if (useUdp) {
                sendUdp(ipAddress, port, bytes)
            } else {
                sendTcp(ipAddress, port, bytes, timeoutMs)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun sendTcp(ipAddress: String, port: Int, bytes: ByteArray, timeoutMs: Int) {
        Socket().use { socket ->
            socket.connect(InetSocketAddress(ipAddress, port), timeoutMs)
            socket.soTimeout = timeoutMs
            val outputStream: OutputStream = socket.getOutputStream()
            outputStream.write(bytes)
            outputStream.flush()
        }
    }

    private fun sendUdp(ipAddress: String, port: Int, bytes: ByteArray) {
        DatagramSocket().use { socket ->
            val address = InetAddress.getByName(ipAddress)
            val packet = DatagramPacket(bytes, bytes.size, address, port)
            socket.send(packet)
        }
    }
}
