package com.penguino.ui.screens

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.penguino.data.local.models.DeviceInfo
import com.penguino.ui.theme.PenguinoTheme
import com.penguino.ui.viewmodels.ScanViewModel.ScanUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "ScanPage"

// TODO: Make btEnabled reactive. Change value when bluetooth status is changed.
// TODO: EDIT SCREEN

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen (
	modifier: Modifier = Modifier,
	uiState: ScanUiState = ScanUiState(),
	onDeviceSelected: (device: DeviceInfo) -> Boolean = { false },
	onScanButtonClicked: () -> Unit = {},
	onNavigateToRegistration: () -> Unit = {},
	onBackPressed: () -> Unit = {}
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
    LaunchedEffect(key1 = Unit, block = {
        onScanButtonClicked()
    })

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Select Device")
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onScanButtonClicked() }, enabled = !uiState.scanning) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Rescan")
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackHost)
        }
    ) { pad ->
        Column(modifier.padding(pad)) {
            if (uiState.scanning) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(text = "Searching for devices")
                    }
                }

            } else {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                ) {
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

@Composable
private fun DeviceList(
	modifier: Modifier = Modifier,
	devices: List<DeviceInfo>,
	onItemClick: (device: DeviceInfo) -> Unit = { }
) {
    LazyColumn(
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(1)
            )
            .padding(8.dp)

    ) {
        this.items(devices) { deviceInfo ->
            DeviceListItem(
                modifier = modifier
                    .fillMaxWidth(),
                onClick = { onItemClick(deviceInfo) },
                device = deviceInfo
            )
            Spacer(modifier = modifier.height(4.dp))
        }
    }
}

@Composable
private fun DeviceListItem(
	modifier: Modifier = Modifier,
	device: DeviceInfo,
	onClick: (DeviceInfo) -> Unit = {}
) {
//    val deviceSupported by remember { mutableStateOf(device.name.equals("PENGUINO", true)) }
    val deviceSupported = true

    Surface(
        modifier = modifier
            .clickable(
                enabled = deviceSupported,
                onClick = { onClick(device) }
            ),
        shape = MaterialTheme.shapes.small,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = modifier
                .padding(
                    horizontal = 36.dp,
                    vertical = 24.dp
                )
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
    val testDevices = remember { mutableStateListOf<DeviceInfo>(
        DeviceInfo("Dev 1", "Addr 1"),
        DeviceInfo("Dev 2", "Addr 2"),
    ) }
    PenguinoTheme {
        Surface {
//            DeviceList(devices = testDevices, onItemClick = {})
        }
    }
}