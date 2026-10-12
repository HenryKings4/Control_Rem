package com.example.control_rem.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.NavigateBefore
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.control_rem.data.model.ConnectionState
import com.example.control_rem.data.model.ProtocolType
import com.example.control_rem.ui.components.ConnectionStateBanner
import com.example.control_rem.ui.theme.Control_RemTheme
import com.example.control_rem.ui.viewmodel.RemoteViewModel

@Composable
fun RemoteScreen(
    viewModel: RemoteViewModel,
    modifier: Modifier = Modifier,
    onNavigateToConnection: () -> Unit = {}
) {
    val connectionState by viewModel.connectionState.collectAsState()

    RemoteContent(
        connectionState = connectionState,
        onNextSlide = viewModel::sendNextSlide,
        onPrevSlide = viewModel::sendPrevSlide,
        onStartPresentation = viewModel::sendStartPresentation,
        onStopPresentation = viewModel::sendStopPresentation,
        onBlankScreen = viewModel::sendBlankScreen,
        onNavigateToConnection = onNavigateToConnection,
        modifier = modifier
    )
}

@Composable
fun RemoteContent(
    connectionState: ConnectionState,
    onNextSlide: () -> Unit,
    onPrevSlide: () -> Unit,
    onStartPresentation: () -> Unit,
    onStopPresentation: () -> Unit,
    onBlankScreen: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToConnection: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Connection State Banner
        ConnectionStateBanner(
            connectionState = connectionState,
            onNavigateToConnection = onNavigateToConnection
        )

        // Title Header
        Text(
            text = "Control de Presentación",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(vertical = 4.dp)
        )

        // Main Slide Navigation Area (Big Intuitive Buttons)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // NEXT SLIDE (Primary Big Button)
                Button(
                    onClick = onNextSlide,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.2f),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.NavigateNext,
                            contentDescription = "Siguiente Diapositiva",
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Siguiente",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        )
                    }
                }

                // PREVIOUS SLIDE (Secondary Big Button)
                FilledTonalButton(
                    onClick = onPrevSlide,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.NavigateBefore,
                            contentDescription = "Diapositiva Anterior",
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Anterior",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        )
                    }
                }
            }
        }

        // Auxiliary Presentation Controls (F5, Esc, Black Screen)
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Acciones de Presentación",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Start Presentation (F5)
                    Button(
                        onClick = onStartPresentation,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = "Iniciar Presentación (F5)"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Iniciar (F5)")
                    }

                    // Stop Presentation (Esc)
                    OutlinedButton(
                        onClick = onStopPresentation,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Stop,
                            contentDescription = "Detener Presentación (Esc)"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Detener (Esc)")
                    }
                }

                // Blank Screen (B)
                FilledTonalButton(
                    onClick = onBlankScreen,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.VisibilityOff,
                        contentDescription = "Pantalla en Blanco (B)"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pantalla en Blanco (B)")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RemoteScreenPreview() {
    Control_RemTheme {
        RemoteContent(
            connectionState = ConnectionState.Connected("192.168.1.100", 8080, ProtocolType.TCP),
            onNextSlide = {},
            onPrevSlide = {},
            onStartPresentation = {},
            onStopPresentation = {},
            onBlankScreen = {}
        )
    }
}
