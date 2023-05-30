package com.penguino.repositories

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.penguino.models.DeviceInfo
import com.penguino.services.BluetoothLeService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

private const val DTAG = "BluetoothManagementImpl"

@SuppressLint("MissingPermission")
class BleRepositoryImpl @Inject constructor(
    private val context: Context,
    private val blAdapter: BluetoothAdapter,
): BleRepository {
    private var bluetoothLeService: BluetoothLeService? = null

    private var _scanning = MutableStateFlow(false)
    override var scanning: StateFlow<Boolean> = _scanning

    private var _btEnabled = MutableStateFlow(true)
    override var btEnabled: StateFlow<Boolean> = _btEnabled

    // Devices found during the scanning process. (Needs a little more tweak)
    private val mutDeviceSet: MutableSet<DeviceInfo> = mutableSetOf()
    private val _deviceList = MutableStateFlow<List<DeviceInfo>>(listOf())
    override val deviceList: StateFlow<List<DeviceInfo>> = _deviceList

    private val scanCallback: ScanCallback = object: ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.device?.let { device ->
//                device.name?.let { name ->
//                    val deviceInfo = DeviceInfo(deviceName = name, address = device.address)
//
//                    mutDeviceSet.add(deviceInfo)
//                    _deviceList.value = mutDeviceSet.toList()
//
//                    Log.d(DTAG, deviceList.value.toString())
//                }

                val deviceInfo = DeviceInfo(deviceName = device.name ?: "", address = device.address)

                mutDeviceSet.add(deviceInfo)
                _deviceList.value = mutDeviceSet.toList()


            }
        }
    }

    // Callbacks for binding BluetoothLeService
    private val bluetoothServiceConn = object : ServiceConnection {
        private val TAG = "BluetoothServiceConnection"
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.i(TAG, "BluetoothLeService Bound")
            bluetoothLeService = (service as BluetoothLeService.ServiceBinder).getService()
            bluetoothLeService?.let { bluetooth ->
                // Connect here and do stuff here on service created
                if (!bluetooth.initialize()) {
                    Log.e(TAG, "Unable to initialize service")
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "Service Unbound")
        }
    }

    override fun scanDevices() {
        if (scanning.value) return
        val scanner: BluetoothLeScanner = blAdapter.bluetoothLeScanner

        mutDeviceSet.clear()
        _deviceList.value = mutDeviceSet.toList()

        _scanning.value = true
        scanner.startScan(scanCallback)

        Handler(Looper.getMainLooper()).postDelayed({ ->
            Log.d(DTAG, "Stopping scan")
            stopScan()
        }, 5000)
    }

    override fun stopScan() {
        if (scanning.value) {
            blAdapter.bluetoothLeScanner.stopScan(scanCallback)
            _scanning.value = false
        }
    }

    override fun bindService() {
        Intent(context, BluetoothLeService::class.java).also { intent ->
            val bindSuccess: Boolean = context.bindService(
                intent,
                bluetoothServiceConn,
                Context.BIND_AUTO_CREATE
            )

            if (!bindSuccess) {
                unbindService()
            }
        }

    }

    override fun unbindService() {
        context.unbindService(bluetoothServiceConn)
    }

    override fun sendMessage(message: String) {
        bluetoothLeService?.writeToPengu(message)
    }

    override fun connect(device: DeviceInfo): Boolean {
        return bluetoothLeService?.connect(device.address) ?: false
    }

    override fun disconnect() {
        bluetoothLeService?.disconnect()
    }

    override fun btEnabled(): Boolean {
        return blAdapter.isEnabled
    }

}