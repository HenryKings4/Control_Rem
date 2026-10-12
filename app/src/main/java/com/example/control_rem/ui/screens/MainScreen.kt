package com.example.control_rem.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mouse
import androidx.compose.material.icons.rounded.SettingsRemote
import androidx.compose.material.icons.rounded.Slideshow
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.control_rem.data.repository.RemoteControlRepository
import com.example.control_rem.ui.theme.Control_RemTheme
import com.example.control_rem.ui.viewmodel.ConnectionViewModel
import com.example.control_rem.ui.viewmodel.MainViewModel
import com.example.control_rem.ui.viewmodel.NavScreen
import com.example.control_rem.ui.viewmodel.RemoteViewModel
import com.example.control_rem.ui.viewmodel.TouchpadViewModel
import com.example.control_rem.ui.viewmodel.ViewModelFactory

@Composable
fun MainScreen(
    repository: RemoteControlRepository,
    modifier: Modifier = Modifier
) {
    val factory = ViewModelFactory(repository)
    val mainViewModel: MainViewModel = viewModel(factory = factory)
    val connectionViewModel: ConnectionViewModel = viewModel(factory = factory)
    val remoteViewModel: RemoteViewModel = viewModel(factory = factory)
    val touchpadViewModel: TouchpadViewModel = viewModel(factory = factory)

    val currentScreen by mainViewModel.currentScreen.collectAsState()

    MainScreenContent(
        currentScreen = currentScreen,
        onSelectScreen = mainViewModel::selectScreen,
        connectionViewModel = connectionViewModel,
        remoteViewModel = remoteViewModel,
        touchpadViewModel = touchpadViewModel,
        modifier = modifier
    )
}

@Composable
fun MainScreenContent(
    currentScreen: NavScreen,
    onSelectScreen: (NavScreen) -> Unit,
    connectionViewModel: ConnectionViewModel,
    remoteViewModel: RemoteViewModel,
    touchpadViewModel: TouchpadViewModel,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        NavItem(NavScreen.CONNECTION, "Conectar", Icons.Rounded.SettingsRemote),
        NavItem(NavScreen.REMOTE, "Presentación", Icons.Rounded.Slideshow),
        NavItem(NavScreen.TOUCHPAD, "Touchpad", Icons.Rounded.Mouse)
    )

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth >= 600.dp

        if (isWide) {
            // Tablet / Landscape Layout with Navigation Rail
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    NavigationRail {
                        navItems.forEach { item ->
                            NavigationRailItem(
                                selected = currentScreen == item.screen,
                                onClick = { onSelectScreen(item.screen) },
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) }
                            )
                        }
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        ScreenContainer(
                            currentScreen = currentScreen,
                            connectionViewModel = connectionViewModel,
                            remoteViewModel = remoteViewModel,
                            touchpadViewModel = touchpadViewModel,
                            onNavigateToConnection = { onSelectScreen(NavScreen.CONNECTION) }
                        )
                    }
                }
            }
        } else {
            // Smartphone Vertical Layout with Bottom Navigation Bar
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    NavigationBar {
                        navItems.forEach { item ->
                            NavigationBarItem(
                                selected = currentScreen == item.screen,
                                onClick = { onSelectScreen(item.screen) },
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    ScreenContainer(
                        currentScreen = currentScreen,
                        connectionViewModel = connectionViewModel,
                        remoteViewModel = remoteViewModel,
                        touchpadViewModel = touchpadViewModel,
                        onNavigateToConnection = { onSelectScreen(NavScreen.CONNECTION) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ScreenContainer(
    currentScreen: NavScreen,
    connectionViewModel: ConnectionViewModel,
    remoteViewModel: RemoteViewModel,
    touchpadViewModel: TouchpadViewModel,
    onNavigateToConnection: () -> Unit
) {
    when (currentScreen) {
        NavScreen.CONNECTION -> ConnectionScreen(viewModel = connectionViewModel)
        NavScreen.REMOTE -> RemoteScreen(
            viewModel = remoteViewModel,
            onNavigateToConnection = onNavigateToConnection
        )
        NavScreen.TOUCHPAD -> TouchpadScreen(
            viewModel = touchpadViewModel,
            onNavigateToConnection = onNavigateToConnection
        )
    }
}

private data class NavItem(
    val screen: NavScreen,
    val label: String,
    val icon: ImageVector
)

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    Control_RemTheme {
        // Preview placeholder
    }
}
