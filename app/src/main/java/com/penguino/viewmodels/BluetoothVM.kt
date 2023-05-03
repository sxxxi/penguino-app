package com.penguino.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.penguino.bluetooth.models.DeviceInfo
import com.penguino.bluetooth.models.RegistrationInfo
import com.penguino.bluetooth.services.BluetoothManagement
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "BluetoothVM"
@SuppressLint("MissingPermission")
@HiltViewModel
class BluetoothVM @Inject constructor(
    private val bluetoothManager: BluetoothManagement,
): ViewModel() {
    var scanning = bluetoothManager.scanning
    val scannedDevices = bluetoothManager.devicesFound

    /**
     * SelectedDevice is initially null, therefore, classes depending on
     * its value should pass through the device scanning procedure to
     * update this property.
     */
    var selectedDevice = mutableStateOf<DeviceInfo?>(null)
        private set

//    private val scanCallback: ScanCallback = object: ScanCallback() {
//        override fun onScanResult(callbackType: Int, result: ScanResult?) {
//            super.onScanResult(callbackType, result)
//            result?.let { res ->
//                val device = res.device
//                val deviceInfo = DeviceInfo(device.name ?: "NO_NAME", device.address)
//                if (!scannedDevices.contains(deviceInfo)) scannedDevices.add(deviceInfo)
//            }
//        }
//    }

    fun scanDevices() {
        bluetoothManager.scanDevices()
    }

    fun stopScan() {
         bluetoothManager.stopScan()
    }

    fun bindService() {
        bluetoothManager.bindService()
    }

    fun unbindService() {
        bluetoothManager.unbindService()
    }

    fun selectDevice(device: DeviceInfo) {
        this.selectedDevice.value = device
    }

    fun connect() {
        selectedDevice.value?.let {
            Log.d("HELLO", bluetoothManager.connect(it).toString())
        }
    }

    fun sendMessage(msg: String) {
        bluetoothManager.sendMessage(msg)
    }

    fun disconnect() {
        bluetoothManager.disconnect()
    }

    // I might use for directly connecting from the home screen.
    fun deviceFromRegInfo(regInfo: RegistrationInfo? = null) {
        /*
        check saved reg infos and display if device is nearby
         */
        regInfo?.let {
            selectedDevice.value = regInfo.device
        }
    }
}