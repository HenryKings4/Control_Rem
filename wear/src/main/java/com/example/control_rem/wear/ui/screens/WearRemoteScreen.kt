package com.example.control_rem.wear.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberScalingLazyListState
import com.example.control_rem.wear.data.model.WearConnectionMode
import com.example.control_rem.wear.data.model.WearConnectionStatus
import com.example.control_rem.wear.ui.theme.AccentAmber
import com.example.control_rem.wear.ui.theme.ActionRed
import com.example.control_rem.wear.ui.theme.DarkBackground
import com.example.control_rem.wear.ui.theme.PrimaryCyan
import com.example.control_rem.wear.ui.theme.SecondaryGreen
import com.example.control_rem.wear.ui.theme.WearTheme

@Composable
fun WearRemoteScreen(
    connectionStatus: WearConnectionStatus,
    lastActionSent: String?,
    lastErrorMessage: String?,
    onNextSlide: () -> Unit,
    onPrevSlide: () -> Unit,
    onStartPresentation: () -> Unit,
    onStopPresentation: () -> Unit,
    onRefreshStatus: () -> Unit,
    onUpdateSocketConfig: (ip: String, port: Int, enabled: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberScalingLazyListState()
    var showSettingsDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            autoCentering = AutoCenteringParams(itemIndex = 1)
        ) {
            // Item 1: App Header
            item {
                Text(
                    text = "Control Remoto",
                    style = MaterialTheme.typography.caption1.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryCyan
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            // Item 2: Connection Status Badge
            item {
                ConnectionStatusChip(
                    status = connectionStatus,
                    onRefresh = onRefreshStatus
                )
            }

            // Item 3: Siguiente > (Next Slide) - Primary Action
            item {
                Chip(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .padding(vertical = 2.dp),
                    onClick = onNextSlide,
                    label = {
                        Text(
                            text = "Siguiente >",
                            style = MaterialTheme.typography.button.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = Color.Black
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    secondaryLabel = {
                        Text(
                            text = "Avanzar Diapositiva",
                            style = MaterialTheme.typography.caption2.copy(color = Color.DarkGray),
                            maxLines = 1
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = "Siguiente",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    colors = ChipDefaults.chipColors(
                        backgroundColor = PrimaryCyan,
                        contentColor = Color.Black
                    )
                )
            }

            // Item 4: < Anterior (Previous Slide)
            item {
                Chip(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(vertical = 2.dp),
                    onClick = onPrevSlide,
                    label = {
                        Text(
                            text = "< Anterior",
                            style = MaterialTheme.typography.button.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.Black
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    secondaryLabel = {
                        Text(
                            text = "Retroceder",
                            style = MaterialTheme.typography.caption2.copy(color = Color.DarkGray),
                            maxLines = 1
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Anterior",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    colors = ChipDefaults.chipColors(
                        backgroundColor = AccentAmber,
                        contentColor = Color.Black
                    )
                )
            }

            // Item 5: Iniciar Presentación (Start Presentation)
            item {
                Chip(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(vertical = 2.dp),
                    onClick = onStartPresentation,
                    label = {
                        Text(
                            text = "Iniciar Presentación",
                            style = MaterialTheme.typography.button.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = "Iniciar",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    colors = ChipDefaults.chipColors(
                        backgroundColor = SecondaryGreen,
                        contentColor = Color.Black
                    )
                )
            }

            // Item 6: Detener (Stop Presentation)
            item {
                Chip(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(vertical = 2.dp),
                    onClick = onStopPresentation,
                    label = {
                        Text(
                            text = "Detener",
                            style = MaterialTheme.typography.button.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Stop,
                            contentDescription = "Detener",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    colors = ChipDefaults.chipColors(
                        backgroundColor = ActionRed,
                        contentColor = Color.White
                    )
                )
            }

            // Item 7: Direct Wi-Fi Config / Options Toggle
            item {
                CompactChip(
                    onClick = { showSettingsDialog = !showSettingsDialog },
                    label = {
                        Text(
                            text = if (showSettingsDialog) "Ocultar Ajustes IP" else "Ajustes Wi-Fi Direct",
                            style = MaterialTheme.typography.caption2
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Configuración",
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = ChipDefaults.secondaryChipColors()
                )
            }

            // Item 8: Direct Wi-Fi Panel (if expanded)
            if (showSettingsDialog) {
                item {
                    DirectWifiSettingsPanel(
                        status = connectionStatus,
                        onUpdateSocketConfig = onUpdateSocketConfig
                    )
                }
            }

            // Item 9: Status feedback indicator / error feedback
            if (lastErrorMessage != null) {
                item {
                    Text(
                        text = "Error: $lastErrorMessage",
                        style = MaterialTheme.typography.caption2.copy(color = ActionRed),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            } else if (lastActionSent != null) {
                item {
                    Text(
                        text = "Enviado: $lastActionSent",
                        style = MaterialTheme.typography.caption2.copy(color = SecondaryGreen),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ConnectionStatusChip(
    status: WearConnectionStatus,
    onRefresh: () -> Unit
) {
    val statusBg = when {
        status.isConnected && status.mode == WearConnectionMode.DATA_LAYER -> SecondaryGreen
        status.isConnected && status.mode == WearConnectionMode.DIRECT_SOCKET -> PrimaryCyan
        else -> ActionRed
    }

    val statusText = when {
        status.isConnected && status.mode == WearConnectionMode.DATA_LAYER -> "Teléfono Conectado"
        status.isConnected && status.mode == WearConnectionMode.DIRECT_SOCKET -> "Wi-Fi Direct (${status.directIpAddress})"
        else -> "Desconectado"
    }

    val statusIcon = when {
        status.isConnected && status.mode == WearConnectionMode.DATA_LAYER -> Icons.Rounded.CheckCircle
        status.isConnected && status.mode == WearConnectionMode.DIRECT_SOCKET -> Icons.Rounded.Wifi
        else -> Icons.Rounded.Warning
    }

    Chip(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .padding(vertical = 2.dp),
        onClick = onRefresh,
        label = {
            Text(
                text = statusText,
                style = MaterialTheme.typography.caption2.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = if (status.isConnected) Color.Black else Color.White
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        icon = {
            Icon(
                imageVector = statusIcon,
                contentDescription = "Estado",
                tint = if (status.isConnected) Color.Black else Color.White,
                modifier = Modifier.size(16.dp)
            )
        },
        colors = ChipDefaults.chipColors(
            backgroundColor = statusBg,
            contentColor = if (status.isConnected) Color.Black else Color.White
        )
    )
}

@Composable
private fun DirectWifiSettingsPanel(
    status: WearConnectionStatus,
    onUpdateSocketConfig: (ip: String, port: Int, enabled: Boolean) -> Unit
) {
    var ipText by remember(status.directIpAddress) { mutableStateOf(status.directIpAddress) }
    var portText by remember(status.directPort) { mutableStateOf(status.directPort.toString()) }
    var directEnabled by remember(status.isDirectWifiEnabled) { mutableStateOf(status.isDirectWifiEnabled) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF2C2C3E))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Wi-Fi Fallback Target",
            style = MaterialTheme.typography.caption2.copy(fontWeight = FontWeight.Bold, color = PrimaryCyan)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "IP: ${status.directIpAddress}:${status.directPort}",
            style = MaterialTheme.typography.caption2,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CompactChip(
                onClick = {
                    val newEnabled = !directEnabled
                    directEnabled = newEnabled
                    onUpdateSocketConfig(ipText, portText.toIntOrNull() ?: 8080, newEnabled)
                },
                label = {
                    Text(
                        text = if (directEnabled) "Wi-Fi: On" else "Wi-Fi: Off",
                        style = MaterialTheme.typography.caption2
                    )
                },
                colors = ChipDefaults.chipColors(
                    backgroundColor = if (directEnabled) SecondaryGreen else Color.Gray,
                    contentColor = Color.Black
                )
            )
        }
    }
}

// ==========================================
// PREVIEWS FOR WEAR OS SCREENS (Requirement 5)
// ==========================================

@Preview(
    device = "id:wearos_large_round",
    showSystemUi = true,
    backgroundColor = 0xFF000000,
    showBackground = true,
    name = "Wear OS Round Watch (Moto 360)"
)
@Composable
fun WearRemoteScreenRoundPreview() {
    WearTheme {
        WearRemoteScreen(
            connectionStatus = WearConnectionStatus(
                isConnected = true,
                mode = WearConnectionMode.DATA_LAYER,
                connectedNodesCount = 1,
                statusText = "Teléfono Conectado"
            ),
            lastActionSent = "SLIDE_NEXT",
            lastErrorMessage = null,
            onNextSlide = {},
            onPrevSlide = {},
            onStartPresentation = {},
            onStopPresentation = {},
            onRefreshStatus = {},
            onUpdateSocketConfig = { _, _, _ -> }
        )
    }
}

@Preview(
    device = "id:wearos_square",
    showSystemUi = true,
    backgroundColor = 0xFF000000,
    showBackground = true,
    name = "Wear OS Square Watch"
)
@Composable
fun WearRemoteScreenSquarePreview() {
    WearTheme {
        WearRemoteScreen(
            connectionStatus = WearConnectionStatus(
                isConnected = true,
                mode = WearConnectionMode.DIRECT_SOCKET,
                directIpAddress = "192.168.1.100",
                directPort = 8080,
                statusText = "Wi-Fi Direct (192.168.1.100)"
            ),
            lastActionSent = null,
            lastErrorMessage = null,
            onNextSlide = {},
            onPrevSlide = {},
            onStartPresentation = {},
            onStopPresentation = {},
            onRefreshStatus = {},
            onUpdateSocketConfig = { _, _, _ -> }
        )
    }
}
