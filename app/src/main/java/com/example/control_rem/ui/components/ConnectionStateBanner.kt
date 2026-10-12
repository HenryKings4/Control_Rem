package com.example.control_rem.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.control_rem.data.model.ConnectionState
import com.example.control_rem.data.model.ProtocolType
import com.example.control_rem.ui.theme.Control_RemTheme

@Composable
fun ConnectionStateBanner(
    connectionState: ConnectionState,
    modifier: Modifier = Modifier,
    onNavigateToConnection: () -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ConnectionStatusBadge(
                connectionState = connectionState,
                modifier = Modifier.weight(1f, fill = false)
            )

            if (connectionState !is ConnectionState.Connected) {
                TextButton(onClick = onNavigateToConnection) {
                    Text("Configurar")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConnectionStateBannerPreview() {
    Control_RemTheme {
        ConnectionStateBanner(
            connectionState = ConnectionState.Connected("192.168.1.100", 8080, ProtocolType.TCP),
            onNavigateToConnection = {}
        )
    }
}
