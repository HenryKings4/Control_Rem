package com.example.control_rem.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Mouse
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.Button
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.control_rem.data.model.ConnectionState
import com.example.control_rem.data.model.ProtocolType
import com.example.control_rem.ui.components.ConnectionStateBanner
import com.example.control_rem.ui.theme.Control_RemTheme
import com.example.control_rem.ui.viewmodel.TouchpadViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun TouchpadScreen(
    viewModel: TouchpadViewModel,
    modifier: Modifier = Modifier,
    onNavigateToConnection: () -> Unit = {}
) {
    val connectionState by viewModel.connectionState.collectAsState()

    TouchpadContent(
        connectionState = connectionState,
        onMouseMove = viewModel::onMouseMove,
        onLeftClick = viewModel::onLeftClick,
        onRightClick = viewModel::onRightClick,
        onDoubleClick = viewModel::onDoubleClick,
        onScroll = viewModel::onScroll,
        onNavigateToConnection = onNavigateToConnection,
        modifier = modifier
    )
}

@Composable
fun TouchpadContent(
    connectionState: ConnectionState,
    onMouseMove: (dx: Float, dy: Float) -> Unit,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
    onDoubleClick: () -> Unit,
    onScroll: (dy: Float) -> Unit,
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

        // Screen Header
        Text(
            text = "Trackpad Interactivo",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )

        // Main Touchpad Gesture Area
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(18.dp)
                    )
                    .pointerInput(Unit) {
                        coroutineScope {
                            launch {
                                detectTapGestures(
                                    onTap = { onLeftClick() },
                                    onDoubleTap = { onDoubleClick() },
                                    onLongPress = { onRightClick() }
                                )
                            }
                            launch {
                                awaitPointerEventScope {
                                    while (true) {
                                        val event = awaitPointerEvent()
                                        val pointers = event.changes.filter { it.pressed }
                                        if (pointers.size == 1) {
                                            val change = pointers[0]
                                            if (change.position != change.previousPosition) {
                                                val dx = change.position.x - change.previousPosition.x
                                                val dy = change.position.y - change.previousPosition.y
                                                onMouseMove(dx, dy)
                                                change.consume()
                                            }
                                        } else if (pointers.size == 2) {
                                            val p1 = pointers[0]
                                            val p2 = pointers[1]
                                            val dy1 = p1.position.y - p1.previousPosition.y
                                            val dy2 = p2.position.y - p2.previousPosition.y
                                            val avgDy = (dy1 + dy2) / 2f
                                            if (abs(avgDy) > 0.5f) {
                                                onScroll(avgDy)
                                                p1.consume()
                                                p2.consume()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.TouchApp,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Área de Touchpad",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• 1 dedo: Mover cursor\n• 1 toque: Clic Izquierdo\n• 2 toques: Doble Clic\n• Mantener: Clic Derecho\n• 2 dedos: Desplazamiento (Scroll)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            textAlign = TextAlign.Center
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Secondary Click & Scroll Controls
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Controles Secundarios de Mouse",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                // Dedicated Clic Izquierdo & Clic Derecho
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onLeftClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Mouse,
                            contentDescription = "Clic Izquierdo"
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Clic Izquierdo",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    FilledTonalButton(
                        onClick = onRightClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Clic Derecho",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Dedicated Scroll Up / Scroll Down
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { onScroll(-20f) }, // Scroll Up
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowUpward,
                            contentDescription = "Scroll Arriba"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Scroll Arriba")
                    }

                    OutlinedButton(
                        onClick = { onScroll(20f) }, // Scroll Down
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowDownward,
                            contentDescription = "Scroll Abajo"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Scroll Abajo")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TouchpadScreenPreview() {
    Control_RemTheme {
        TouchpadContent(
            connectionState = ConnectionState.Connected("192.168.1.100", 8080, ProtocolType.TCP),
            onMouseMove = { _, _ -> },
            onLeftClick = {},
            onRightClick = {},
            onDoubleClick = {},
            onScroll = {}
        )
    }
}
