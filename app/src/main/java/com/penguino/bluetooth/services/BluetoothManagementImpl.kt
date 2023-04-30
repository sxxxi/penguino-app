package com.penguino.bluetooth.services

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.penguino.bluetooth.models.DeviceInfo
import javax.inject.Inject

private const val DTAG = "BluetoothManagementImpl"

@SuppressLint("MissingPermission")
class BluetoothManagementImpl @Inject constructor(
    private val blAdapter: BluetoothAdapter
): BluetoothManagement {
    val scanning: MutableState<Boolean> = mutableStateOf(false)
    private val devicesFound = mutableStateListOf<DeviceInfo>()
    private val scanCallback: ScanCallback = object: ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.let { res ->
                val device = res.device
                val deviceInfo = DeviceInfo(device.name, device.address)
                devicesFound.add(deviceInfo)
            }
        }
    }

    override fun scanDevices() {
        if (scanning.value) return
        val scanner: BluetoothLeScanner = blAdapter.bluetoothLeScanner
        scanning.value = true
        scanner.startScan(scanCallback)
        Handler(Looper.getMainLooper()).postDelayed({ ->
            Log.d(DTAG, "Stopping scan")
            scanner.stopScan(scanCallback)
            scanning.value = false
        }, 5000)
    }
}