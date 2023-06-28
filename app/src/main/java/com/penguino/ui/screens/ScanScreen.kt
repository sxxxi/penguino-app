package com.penguino.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.penguino.data.local.models.DeviceInfo
import com.penguino.ui.components.Loader
import com.penguino.ui.components.Mods
import com.penguino.ui.components.SimpleIconButton
import com.penguino.ui.components.SimpleTopBar
import com.penguino.ui.theme.PenguinoTheme
import com.penguino.ui.viewmodels.ScanViewModel
import com.penguino.ui.viewmodels.ScanViewModel.ScanUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "ScanPage"

// TODO: EDIT SCREEN

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen (
	modifier: Modifier = Modifier,
	uiState: ScanUiState = ScanUiState(),
	onDeviceSelected: (device: DeviceInfo) -> Boolean = { false },
	onScanButtonClicked: () -> Unit = {},
	onNavigateToRegistration: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val devicesFound = uiState.devicesFound
    val snackHost = remember { SnackbarHostState() }

    LaunchedEffect(key1 = uiState.isError) {
        if (uiState.isError) {
            scope.launch {
                snackHost.showSnackbar(
                    message = uiState.errorMessage,
                    duration = uiState.errorDuration
                )
            }
        }
    }

    // Only run once
    LaunchedEffect(key1 = Unit) {
        onScanButtonClicked()
    }

    Scaffold(
        topBar = {
            SimpleTopBar(
                title = "Select device",
                actions = {
                    SimpleIconButton(
                        iconVector = Icons.Default.Refresh,
                        description = "Rescan",
                        enabled = !uiState.scanning,
                        onClick = onScanButtonClicked
                    )
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackHost)
        }
    ) { pad ->
        Column(modifier.padding(pad)) {
            Crossfade(targetState = uiState) { uiState ->
                // Scanning state
                if (uiState.devicesFound.isEmpty() && uiState.scanning) {
                    Loader("Searching for devices")
                } else {
                    Column(
                        modifier = modifier
                            .fillMaxSize()
                    ) {
                        // No devices found
                        if (uiState.devicesFound.isEmpty()) {
                            Column(
                                modifier = modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "No compatible devices found")
                                TextButton(onClick = onScanButtonClicked) {
                                    Text(text = "Refresh")
                                }
                            }
                        } else {
                            DeviceList(
                                modifier = modifier.weight(1F),
                                devices = devicesFound,
                                onItemClick = {
                                    scope.launch(Dispatchers.IO) {
                                        if (onDeviceSelected(it)) {
                                            scope.launch(Dispatchers.Main) {
                                                onNavigateToRegistration()
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DeviceList(
	modifier: Modifier = Modifier,
	devices: List<DeviceInfo>,
	onItemClick: (device: DeviceInfo) -> Unit = { }
) {
    LazyColumn {
        this.items(devices) { deviceInfo ->
            DeviceListItem(
                modifier = modifier
                    .fillMaxWidth()
                    .animateItemPlacement(),
                onClick = { onItemClick(deviceInfo) },
                device = deviceInfo
            )
            Spacer(modifier = modifier.height(4.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeviceListItem(
	modifier: Modifier = Modifier,
	device: DeviceInfo,
	onClick: (DeviceInfo) -> Unit = {}
) {
//    val deviceSupported by remember { mutableStateOf(device.name.equals("PENGUINO", true)) }
    val deviceSupported = true

    Card(
        modifier = Mods.verticalListItem.then(modifier),
        enabled = deviceSupported,
        onClick = { onClick(device) }

    ) {
        Column(
            modifier = Mods.verticalListItemContent
//                .padding(
//                    horizontal = 36.dp,
//                    vertical = 24.dp
//                )
        ) {
            Text(
                style = MaterialTheme.typography.titleMedium,
                text = device.deviceName
            )
            Text(
                text = device.address,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Preview
@Composable
fun ScanPagePreview() {
    PenguinoTheme {
        Surface {
            ScanPagePreview()
        }
    }
}