package com.example.control_rem.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ClearAll
import androidx.compose.material.icons.rounded.Computer
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Power
import androidx.compose.material.icons.rounded.PowerOff
import androidx.compose.material.icons.rounded.Router
import androidx.compose.material.icons.rounded.SettingsEthernet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.control_rem.data.model.ConnectionState
import com.example.control_rem.data.model.ProtocolType
import com.example.control_rem.data.model.RemoteCommand
import com.example.control_rem.ui.components.ConnectionStatusBadge
import com.example.control_rem.ui.theme.Control_RemTheme
import com.example.control_rem.ui.viewmodel.ConnectionViewModel

@Composable
fun ConnectionScreen(
    viewModel: ConnectionViewModel,
    modifier: Modifier = Modifier
) {
    val connectionState by viewModel.connectionState.collectAsState()
    val ipInput by viewModel.ipInput.collectAsState()
    val portInput by viewModel.portInput.collectAsState()
    val selectedProtocol by viewModel.selectedProtocol.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val commandHistory by viewModel.commandHistory.collectAsState()

    ConnectionContent(
        connectionState = connectionState,
        ipInput = ipInput,
        portInput = portInput,
        selectedProtocol = selectedProtocol,
        errorMessage = errorMessage,
        commandHistory = commandHistory,
        onIpChanged = viewModel::onIpChanged,
        onPortChanged = viewModel::onPortChanged,
        onProtocolSelected = viewModel::onProtocolSelected,
        onConnect = viewModel::connect,
        onDisconnect = viewModel::disconnect,
        onClearHistory = viewModel::clearHistory,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionContent(
    connectionState: ConnectionState,
    ipInput: String,
    portInput: String,
    selectedProtocol: ProtocolType,
    errorMessage: String?,
    commandHistory: List<RemoteCommand>,
    onIpChanged: (String) -> Unit,
    onPortChanged: (String) -> Unit,
    onProtocolSelected: (ProtocolType) -> Unit,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status Badge Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Computer,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Control Remoto PC",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ConnectionStatusBadge(connectionState = connectionState)
                }
            }
        }

        // Connection Configuration Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Configuración de Red",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    // Target IP Address Input
                    OutlinedTextField(
                        value = ipInput,
                        onValueChange = onIpChanged,
                        label = { Text("Dirección IP del Servidor PC") },
                        placeholder = { Text("Ej. 192.168.1.100") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Router,
                                contentDescription = null
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Target Port Input
                    OutlinedTextField(
                        value = portInput,
                        onValueChange = onPortChanged,
                        label = { Text("Puerto") },
                        placeholder = { Text("Ej. 8080") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.SettingsEthernet,
                                contentDescription = null
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Protocol Selector (TCP / UDP)
                    Column {
                        Text(
                            text = "Protocolo de Red",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ProtocolType.entries.forEachIndexed { index, protocol ->
                                SegmentedButton(
                                    selected = selectedProtocol == protocol,
                                    onClick = { onProtocolSelected(protocol) },
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = ProtocolType.entries.size
                                    )
                                ) {
                                    Text(
                                        text = when (protocol) {
                                            ProtocolType.TCP -> "TCP (Confiable)"
                                            ProtocolType.UDP -> "UDP (Rápido)"
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Error Message Banner
                    AnimatedVisibility(
                        visible = (errorMessage != null) || (connectionState is ConnectionState.Error),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        val displayErr = errorMessage ?: (connectionState as? ConnectionState.Error)?.message ?: "Error"
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ErrorOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = displayErr,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }

                    // Connect / Disconnect Action Button
                    val isConnected = connectionState is ConnectionState.Connected
                    val isConnecting = connectionState is ConnectionState.Connecting

                    if (isConnected) {
                        OutlinedButton(
                            onClick = onDisconnect,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PowerOff,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Desconectar",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Button(
                            onClick = onConnect,
                            enabled = !isConnecting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isConnecting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Conectando...")
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.Power,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Conectar",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Command History Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.History,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Historial de Comandos",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        if (commandHistory.isNotEmpty()) {
                            IconButton(onClick = onClearHistory) {
                                Icon(
                                    imageVector = Icons.Rounded.ClearAll,
                                    contentDescription = "Limpiar Historial"
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (commandHistory.isEmpty()) {
                        Text(
                            text = "No hay comandos enviados aún.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        commandHistory.take(5).forEach { cmd ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = cmd::class.simpleName ?: cmd.toString(),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontSize = 12.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConnectionScreenPreview() {
    Control_RemTheme {
        ConnectionContent(
            connectionState = ConnectionState.Disconnected,
            ipInput = "192.168.1.100",
            portInput = "8080",
            selectedProtocol = ProtocolType.TCP,
            errorMessage = null,
            commandHistory = listOf(RemoteCommand.SlideNext, RemoteCommand.MouseLeftClick),
            onIpChanged = {},
            onPortChanged = {},
            onProtocolSelected = {},
            onConnect = {},
            onDisconnect = {},
            onClearHistory = {}
        )
    }
}
