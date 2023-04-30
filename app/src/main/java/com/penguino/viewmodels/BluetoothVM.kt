package com.penguino.viewmodels

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.penguino.bluetooth.models.DeviceInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

enum class ScanStatus {
    Scanning,
    Idle
}

@SuppressLint("MissingPermission")
@HiltViewModel
class BluetoothVM @Inject constructor(
    private val btAdapter: BluetoothAdapter,
): ViewModel() {
    private var _scanning = MutableStateFlow(ScanStatus.Idle)
    var scanning = _scanning.asStateFlow()

    val scannedDevices = mutableStateListOf<DeviceInfo>()

    private val scanCallback: ScanCallback = object: ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.let { res ->
                val device = res.device
                val deviceInfo = DeviceInfo(device.name ?: "NO_NAME", device.address)
                scannedDevices.add(deviceInfo)
            }
        }
    }

    fun scanDevices() {
        if (scanning.value == ScanStatus.Scanning) return
        val scanner: BluetoothLeScanner = btAdapter.bluetoothLeScanner
        Log.d("FOO", "Starting scan")

        _scanning.value = ScanStatus.Scanning

        Log.d("BAZ", "${scanning.value}")

        scanner.startScan(scanCallback)
        Handler(Looper.getMainLooper()).postDelayed({ ->
            scanner.stopScan(scanCallback)
            Log.d("FOO", "Stopping scan")

            _scanning.value = ScanStatus.Idle


            Log.d("BAZ", "${scanning.value}")

        }, 5000)
    }
}