package com.penguino.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.penguino.models.DeviceInfo
import com.penguino.ui.theme.PenguinoTheme
import com.penguino.viewmodels.ScanViewModel
import com.penguino.viewmodels.ScanViewModel.ScanUiState

private const val TAG = "ScanPage"

// TODO: Make btEnabled reactive. Change value when bluetooth status is changed.
// TODO: Research best practices for styling and where to store style property values.

@Composable
fun ScanScreen (
    modifier: Modifier = Modifier,
    viewmodel: ScanViewModel,
    uiState: ScanUiState,
    onDeviceSelected: (DeviceInfo) -> Unit = {},
    onScanButtonClicked: () -> Unit = {},
    onBackButtonClicked: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToRegistration: () -> Unit = {}
) {
    val devicesFound = uiState.devicesFound
    val scanning = uiState.scanning
    val btEnabled = uiState.bluetoothEnabled

    Column(modifier = modifier
        .fillMaxSize()
    ) {
        DeviceList(
            modifier = modifier.weight(1F),
            devices = devicesFound,
            onItemClick = {
                viewmodel.saveSelectedDevice(it)
                onNavigateToRegistration()
            }
        )

        ButtonRow(
            btEnabled = btEnabled,
            scanning = scanning,
            onScanButtonClicked = {
                viewmodel.scanDevices()
            },
            onBackButtonClicked = {
                viewmodel.stopScan()
                onNavigateToHome()
            }
        )
    }
}

@Composable
private fun DeviceList(
    modifier: Modifier = Modifier,
    devices: List<DeviceInfo>,
    onItemClick: (DeviceInfo) -> Unit = {}
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
        this.items(devices) {
            DeviceListItem(
                modifier = modifier
                    .fillMaxWidth(),
                onClick = onItemClick,
                device = it
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

@Composable
private fun ButtonRow(
    modifier: Modifier = Modifier,
    btEnabled: Boolean,
    scanning: Boolean,
    onScanButtonClicked: () -> Unit,
    onBackButtonClicked: () -> Unit
) {
    Row (modifier = modifier.fillMaxWidth()) {
        BackButton(
            modifier = modifier.weight(1f),
            onClick = onBackButtonClicked
        )

        // TODO: Change to Snack bar
        val ctx = LocalContext.current

        ScanButton(
            modifier = modifier.weight(1f),
            scanning = scanning
        ) {
//            if (!btEnabled) {
//                Toast.makeText(ctx, "Please enable Bluetooth", Toast.LENGTH_SHORT).show()
//            } else {
//                onScanButtonClicked()
//            }
            onScanButtonClicked()
        }

    }
}

@Composable
private fun ScanButton(
    modifier: Modifier = Modifier,
    scanning: Boolean,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier
            .padding(8.dp),
        onClick = { onClick() },
        enabled = !scanning
    ) {
        Text(text = "Scan")
    }
}

@Composable
private fun BackButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FilledTonalButton (
        modifier = modifier
            .padding(8.dp),
        onClick = { onClick() },
    ) {
        Text(text = "Back")
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
            DeviceList(devices = testDevices, onItemClick = {})
        }
    }
}