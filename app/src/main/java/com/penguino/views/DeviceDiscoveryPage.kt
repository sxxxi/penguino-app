package com.penguino.views

import androidx.compose.foundation.background
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.penguino.bluetooth.models.DeviceInfo
import com.penguino.ui.theme.PenguinoTheme
import com.penguino.viewmodels.BluetoothVM
import com.penguino.viewmodels.ScanStatus

@Composable
fun ScanPage (
    modifier: Modifier = Modifier,
    testVM: BluetoothVM = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToRegistration: () -> Unit
) {
    val scanState by testVM.scanning.collectAsState()
    val scannedDevices = remember { testVM.scannedDevices }

    Column(modifier = modifier
        .fillMaxSize()
    ) {

        DeviceList(
            modifier = modifier
                .weight(1F),
            devices = scannedDevices,
            onItemClick = {
                testVM.selectDevice(it)
                onNavigateToRegistration()
            }
        )

        Row (
            modifier = modifier
                .fillMaxWidth()
        ) {
            BackButton(
                modifier = modifier
                    .weight(1f)
            ) {
                testVM.stopScan()
                onNavigateToHome()
            }

            ScanButton(
                modifier = modifier
                    .weight(1f),
                scanStatus = scanState
            ) { testVM.scanDevices() }

        }


    }
}

@Composable
fun ScanButton(
    modifier: Modifier = Modifier,
    scanStatus: ScanStatus,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier
            .padding(8.dp),
        onClick = { onClick() },
        enabled = scanStatus == ScanStatus.Idle
    ) {
        Text(text = "Scan")
    }
}

@Composable
fun BackButton(
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

@JvmOverloads
@Composable
fun DeviceList(
    modifier: Modifier = Modifier,
    devices: SnapshotStateList<DeviceInfo>,
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
fun DeviceListItem(
    modifier: Modifier = Modifier,
    device: DeviceInfo,
    onClick: (DeviceInfo) -> Unit = {}
) {
    Surface(
        modifier = modifier
            .clickable { onClick(device) },
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
                text = device.name
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
            DeviceList(devices = testDevices, onItemClick = {})
        }
    }
}