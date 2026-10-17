package com.example.control_rem.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.control_rem.data.model.NetworkConfig
import com.example.control_rem.data.model.ProtocolType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.networkDataStore: DataStore<Preferences> by preferencesDataStore(name = "network_settings")

/**
 * Persists and loads network connection preferences (IP, port, protocol) using Jetpack DataStore.
 */
class NetworkPreferencesDataSource(private val context: Context) {

    private object PreferencesKeys {
        val IP_ADDRESS = stringPreferencesKey("ip_address")
        val PORT = intPreferencesKey("port")
        val PROTOCOL_TYPE = stringPreferencesKey("protocol_type")
        val TIMEOUT_MS = intPreferencesKey("timeout_ms")
    }

    val networkConfigFlow: Flow<NetworkConfig> = context.networkDataStore.data.map { preferences ->
        val ip = preferences[PreferencesKeys.IP_ADDRESS] ?: "192.168.1.100"
        val port = preferences[PreferencesKeys.PORT] ?: 8080
        val protocolName = preferences[PreferencesKeys.PROTOCOL_TYPE] ?: ProtocolType.TCP.name
        val protocol = try {
            ProtocolType.valueOf(protocolName)
        } catch (_: Exception) {
            ProtocolType.TCP
        }
        val timeoutMs = preferences[PreferencesKeys.TIMEOUT_MS] ?: 3000

        NetworkConfig(
            ipAddress = ip,
            port = port,
            protocolType = protocol,
            connectionTimeoutMs = timeoutMs
        )
    }

    suspend fun saveConfig(config: NetworkConfig) {
        context.networkDataStore.edit { preferences ->
            preferences[PreferencesKeys.IP_ADDRESS] = config.ipAddress
            preferences[PreferencesKeys.PORT] = config.port
            preferences[PreferencesKeys.PROTOCOL_TYPE] = config.protocolType.name
            preferences[PreferencesKeys.TIMEOUT_MS] = config.connectionTimeoutMs
        }
    }
}
