package com.example.control_rem.data.network

import android.content.Context
import android.net.wifi.WifiManager
import android.os.PowerManager
import android.util.Log

/**
 * Manages Wi-Fi Lock and WakeLock to prevent the device from suspending network sockets
 * or increasing Wi-Fi latency into power-saving mode when the screen is dimmed/locked.
 */
object NetworkLockManager {

    private const val TAG = "NetworkLockManager"
    private var wifiLock: WifiManager.WifiLock? = null
    private var wakeLock: PowerManager.WakeLock? = null

    fun initialize(context: Context) {
        val appContext = context.applicationContext
        try {
            val wifiManager = appContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            wifiLock = wifiManager?.createWifiLock(
                WifiManager.WIFI_MODE_FULL_HIGH_PERF,
                "ControlRem::HighPerfWifiLock"
            )?.apply {
                setReferenceCounted(false)
            }

            val powerManager = appContext.getSystemService(Context.POWER_SERVICE) as? PowerManager
            wakeLock = powerManager?.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "ControlRem::PartialWakeLock"
            )?.apply {
                setReferenceCounted(false)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to initialize locks: ${e.message}")
        }
    }

    fun acquireLocks() {
        try {
            if (wifiLock?.isHeld == false) {
                wifiLock?.acquire()
                Log.d(TAG, "Wi-Fi High Perf lock acquired")
            }
            if (wakeLock?.isHeld == false) {
                wakeLock?.acquire(30 * 60 * 1000L) // Safety timeout 30 minutes
                Log.d(TAG, "Partial WakeLock acquired")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to acquire locks: ${e.message}")
        }
    }

    fun releaseLocks() {
        try {
            if (wifiLock?.isHeld == true) {
                wifiLock?.release()
                Log.d(TAG, "Wi-Fi High Perf lock released")
            }
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
                Log.d(TAG, "Partial WakeLock released")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to release locks: ${e.message}")
        }
    }
}
