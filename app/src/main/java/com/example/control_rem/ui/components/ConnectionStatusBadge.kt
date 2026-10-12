package com.example.control_rem.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.control_rem.data.model.ConnectionState
import com.example.control_rem.data.model.ProtocolType
import com.example.control_rem.ui.theme.Control_RemTheme

@Composable
fun ConnectionStatusBadge(
    connectionState: ConnectionState,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, contentColor, statusText, statusIcon) = when (connectionState) {
        is ConnectionState.Connected -> {
            val details = "${connectionState.ipAddress}:${connectionState.port} (${connectionState.protocolType.name})"
            Quadruple(
                Color(0xFFE8F5E9), // Light green container
                Color(0xFF2E7D32), // Dark green text/icon
                "Conectado a $details",
                Icons.Rounded.CheckCircle
            )
        }
        is ConnectionState.Connecting -> {
            Quadruple(
                Color(0xFFFFF8E1), // Light amber container
                Color(0xFFF57F17), // Dark amber text/icon
                "Conectando...",
                Icons.Rounded.Sync
            )
        }
        is ConnectionState.Error -> {
            Quadruple(
                Color(0xFFFFEBEE), // Light red container
                Color(0xFFC62828), // Dark red text/icon
                "Error de Conexión",
                Icons.Rounded.Error
            )
        }
        is ConnectionState.Disconnected -> {
            Quadruple(
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.onSurfaceVariant,
                "Desconectado",
                Icons.Rounded.WifiOff
            )
        }
    }

    val animatedBgColor by animateColorAsState(targetValue = backgroundColor, label = "badge_bg")

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = animatedBgColor,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicator Dot
            val dotColor = when (connectionState) {
                is ConnectionState.Connected -> Color(0xFF4CAF50)
                is ConnectionState.Connecting -> Color(0xFFFFB300)
                is ConnectionState.Error -> Color(0xFFF44336)
                is ConnectionState.Disconnected -> Color.Gray
            }

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(dotColor, CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            if (connectionState is ConnectionState.Connecting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(14.dp),
                    color = contentColor,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = contentColor
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = statusText,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                ),
                color = contentColor
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

@Preview(showBackground = true)
@Composable
fun ConnectionStatusBadgePreview() {
    Control_RemTheme {
        Row(modifier = Modifier.padding(16.dp)) {
            ConnectionStatusBadge(
                connectionState = ConnectionState.Connected("192.168.1.50", 8080, ProtocolType.TCP)
            )
        }
    }
}
