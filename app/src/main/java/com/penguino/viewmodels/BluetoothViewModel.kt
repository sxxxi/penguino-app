package com.penguino.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguino.models.DeviceInfo
import com.penguino.models.RegistrationInfo
import com.penguino.repositories.BleRepository
import com.penguino.viewmodels.uistates.ScanUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "BluetoothVM"
@SuppressLint("MissingPermission")
@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothManager: BleRepository,
): ViewModel() {
    var uiState by mutableStateOf(ScanUiState())
        private set

//    var scanning: Boolean = bluetoothManager.scanning.value

    /**
     * SelectedDevice is initially null, therefore, classes depending on
     * its value should pass through the device scanning procedure to
     * update this property.
     */
    var selectedDevice = mutableStateOf<DeviceInfo?>(null)
        private set



    init {
        viewModelScope.launch {
            bluetoothManager.deviceList.collectLatest {
                uiState = uiState.copy(devicesFound = it.toList())
            }
        }
    }

    fun btEnabled(): Boolean {
        return bluetoothManager.btEnabled()
    }

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