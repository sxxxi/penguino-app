package com.penguino.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.penguino.viewmodels.BluetoothVM
import com.penguino.viewmodels.ScanStatus
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.items

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    testVM: BluetoothVM = hiltViewModel(),
    onNavigateToScan: () -> Unit,
) {
    val scanState by testVM.scanning.collectAsState()
    val scannedDevices = remember { testVM.scannedDevices }
    
    Column(modifier = modifier.fillMaxSize()) {
        when (scanState) {
            ScanStatus.Idle -> {
                if (scannedDevices.size == 0) {
                    Button(onClick = {
                        testVM.scanDevices()
                    }) {
                        Text("Scan")
                    }
                } else {
                    LazyColumn {
                        items(scannedDevices) {
                            Row {
                                Text(it.name)
                                Text(it.address)
                            }
                        }
                    }
                }
            }

            ScanStatus.Scanning -> {
                Text("Scanning")
            }
        }

    }
}