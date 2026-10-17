package com.example.control_rem.wear.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.FormatListNumbered
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Slideshow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.control_rem.wear.data.model.WearConnectionMode
import com.example.control_rem.wear.data.model.WearConnectionStatus
import com.example.control_rem.wear.ui.theme.AccentAmber
import com.example.control_rem.wear.ui.theme.ActionRed
import com.example.control_rem.wear.ui.theme.DarkBackground
import com.example.control_rem.wear.ui.theme.DarkCardSurface
import com.example.control_rem.wear.ui.theme.DarkSurface
import com.example.control_rem.wear.ui.theme.PrimaryCyan
import com.example.control_rem.wear.ui.theme.SecondaryGreen
import com.example.control_rem.wear.ui.theme.TextSecondary
import com.example.control_rem.wear.ui.theme.WearTheme

/**
 * Presentation remote control screen optimized for Moto 360 Gen 1 round display (320x290 with flat bottom chin).
 * Uses standard Compose Material 3 compatible with Android 6.0 Marshmallow (API 23+).
 */
@Composable
fun WearRemoteScreen(
    isAmbient: Boolean,
    connectionStatus: WearConnectionStatus,
    lastActionSent: String?,
    lastErrorMessage: String?,
    onNextSlide: () -> Unit,
    onPrevSlide: () -> Unit,
    onStartPresentation: () -> Unit,
    onStopPresentation: () -> Unit,
    onGoToSlide: (Int) -> Unit,
    onRefreshStatus: () -> Unit,
    onUpdateSocketConfig: (ip: String, port: Int, enabled: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showGoToSlideDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isAmbient) Color.Black else DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        if (isAmbient) {
            // UI Minimalista y de bajo consumo en modo ambiente
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Control Remoto",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                top = 28.dp,
                bottom = 28.dp,
                start = 12.dp,
                end = 12.dp
            )
        ) {
            // Header: Title
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(bottom = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Slideshow,
                        contentDescription = null,
                        tint = PrimaryCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Control Remoto",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            // Slide Navigation Area: PREV & NEXT Buttons (Taller)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Slide Button (Amber)
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onPrevSlide()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(76.dp)
                            .padding(end = 4.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentAmber,
                            contentColor = Color.Black
                        )
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Anterior",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "ANTERIOR",
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Next Slide Button (Electric Cyan)
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onNextSlide()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(76.dp)
                            .padding(start = 4.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryCyan,
                            contentColor = Color.Black
                        )
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                contentDescription = "Siguiente",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "SIGUIENTE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // Go to Slide Button (Full Width, right under Prev/Next)
            item {
                Surface(
                    onClick = { showGoToSlideDialog = true },
                    shape = RoundedCornerShape(14.dp),
                    color = DarkCardSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .padding(horizontal = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FormatListNumbered,
                            contentDescription = null,
                            tint = PrimaryCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Ir a Diapositiva",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Quick Actions: Stop (Left) / Start (Right) Presentation
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Stop Presentation (Red - Left)
                    Surface(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onStopPresentation()
                        },
                        shape = RoundedCornerShape(14.dp),
                        color = ActionRed.copy(alpha = 0.25f),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .padding(end = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Stop,
                                contentDescription = "Detener",
                                tint = ActionRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Detener",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Start Presentation (Green - Right)
                    Surface(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onStartPresentation()
                        },
                        shape = RoundedCornerShape(14.dp),
                        color = SecondaryGreen.copy(alpha = 0.25f),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .padding(start = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PlayArrow,
                                contentDescription = "Iniciar",
                                tint = SecondaryGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Iniciar",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Action Feedback / Error Display
            if (lastActionSent != null || lastErrorMessage != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (lastErrorMessage != null) ActionRed.copy(alpha = 0.2f) else DarkSurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (lastErrorMessage != null) Icons.Rounded.Warning else Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = if (lastErrorMessage != null) ActionRed else SecondaryGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = lastErrorMessage ?: "Enviado: $lastActionSent",
                                color = Color.White,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Wi-Fi Direct Settings Button (Full Width)
            item {
                Surface(
                    onClick = { showSettingsDialog = true },
                    shape = RoundedCornerShape(14.dp),
                    color = DarkCardSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .padding(horizontal = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Wifi,
                            contentDescription = null,
                            tint = AccentAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Wi-Fi Direct",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Connection Status Badge / Legend & IP (Very Bottom below all buttons)
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when {
                        connectionStatus.isConnected && connectionStatus.mode == WearConnectionMode.DATA_LAYER ->
                            SecondaryGreen.copy(alpha = 0.2f)
                        connectionStatus.isConnected && connectionStatus.mode == WearConnectionMode.DIRECT_SOCKET ->
                            PrimaryCyan.copy(alpha = 0.2f)
                        else -> ActionRed.copy(alpha = 0.2f)
                    },
                    modifier = Modifier.clickable { onRefreshStatus() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (connectionStatus.isConnected) SecondaryGreen else ActionRed
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = connectionStatus.statusText,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (connectionStatus.isConnected) Color.White else ActionRed,
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        } // Fin del bloque 'else' (fuera de modo ambiente)
    } // Fin del Box principal

    // Dialog: Go to Slide Number
    if (showGoToSlideDialog) {
        GoToSlideDialog(
            onDismiss = { showGoToSlideDialog = false },
            onConfirm = { slideNum ->
                onGoToSlide(slideNum)
                showGoToSlideDialog = false
            }
        )
    }

    // Dialog: Wi-Fi Direct Config
    if (showSettingsDialog) {
        SocketConfigDialog(
            initialIp = connectionStatus.directIpAddress,
            initialPort = connectionStatus.directPort,
            initialEnabled = connectionStatus.isDirectWifiEnabled,
            onDismiss = { showSettingsDialog = false },
            onSave = { ip, port, enabled ->
                onUpdateSocketConfig(ip, port, enabled)
                showSettingsDialog = false
            }
        )
    }
}

@Composable
private fun GoToSlideDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var slideInput by remember { mutableStateOf("1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(
                text = "Ir a Diapositiva",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(
                    value = slideInput,
                    onValueChange = { slideInput = it.filter { char -> char.isDigit() } },
                    label = { Text("Número", fontSize = 10.sp, color = TextSecondary) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryCyan,
                        unfocusedBorderColor = TextSecondary,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val slide = slideInput.toIntOrNull() ?: 1
                    onConfirm(slide)
                }
            ) {
                Text("IR", color = PrimaryCyan, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        }
    )
}

@Composable
private fun SocketConfigDialog(
    initialIp: String,
    initialPort: Int,
    initialEnabled: Boolean,
    onDismiss: () -> Unit,
    onSave: (ip: String, port: Int, enabled: Boolean) -> Unit
) {
    var ip by remember { mutableStateOf(initialIp.ifBlank { "192.168.1.100" }) }
    var portText by remember { mutableStateOf(initialPort.toString()) }
    var enabled by remember { mutableStateOf(initialEnabled) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(
                text = "Wi-Fi Direct",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Activar Direct Socket", color = Color.White, fontSize = 10.sp)
                    Switch(
                        checked = enabled,
                        onCheckedChange = { enabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PrimaryCyan,
                            checkedTrackColor = PrimaryCyan.copy(alpha = 0.5f)
                        )
                    )
                }

                if (enabled) {
                    OutlinedTextField(
                        value = ip,
                        onValueChange = { ip = it },
                        label = { Text("IP Servidor PC", fontSize = 9.sp, color = TextSecondary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryCyan,
                            unfocusedBorderColor = TextSecondary,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = portText,
                        onValueChange = { portText = it.filter { char -> char.isDigit() } },
                        label = { Text("Puerto", fontSize = 9.sp, color = TextSecondary) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryCyan,
                            unfocusedBorderColor = TextSecondary,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val port = portText.toIntOrNull() ?: 8080
                    onSave(ip, port, enabled)
                }
            ) {
                Text("Guardar", color = PrimaryCyan, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        }
    )
}

@Preview(widthDp = 320, heightDp = 290)
@Composable
fun WearRemoteScreenPreview() {
    WearTheme {
        WearRemoteScreen(
            isAmbient = false,
            connectionStatus = WearConnectionStatus(
                isConnected = true,
                statusText = "Conectado a Celular"
            ),
            lastActionSent = "SLIDE_NEXT",
            lastErrorMessage = null,
            onNextSlide = {},
            onPrevSlide = {},
            onStartPresentation = {},
            onStopPresentation = {},
            onGoToSlide = {},
            onRefreshStatus = {},
            onUpdateSocketConfig = { _, _, _ -> }
        )
    }
}
