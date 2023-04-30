package com.penguino.views

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.penguino.viewmodels.BluetoothVM
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import com.penguino.bluetooth.models.DeviceInfo
import com.penguino.viewmodels.ScanStatus

@Composable
fun DeviceScan(
    modifier: Modifier = Modifier,
    btVM: BluetoothVM = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToDeviceList: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        val scanStatus by btVM.scanning.collectAsState()
        Text(text = "$scanStatus")
    }
}


@Composable
fun ScannedDevice(
    modifier: Modifier = Modifier,
    devices: List<DeviceInfo>
)  {
    for (device in devices) {
        Text(text = device.name)
        Text(text = device.address)
    }
}