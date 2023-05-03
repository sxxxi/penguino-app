package com.penguino.bluetooth.services

import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.penguino.bluetooth.models.DeviceInfo
import com.penguino.viewmodels.ScanStatus
import javax.inject.Inject

private const val DTAG = "BluetoothManagementImpl"

@SuppressLint("MissingPermission")
class BluetoothManagementImpl @Inject constructor(
    private val context: Context,
    private val blAdapter: BluetoothAdapter,
//    override val scannedDevices: SnapshotStateList<DeviceInfo> = mutableStateListOf<DeviceInfo>()
): BluetoothManagement {
    private var bluetoothLeService: BluetoothLeService? = null
    override val scanning: MutableState<Boolean> = mutableStateOf(false)
    override val devicesFound = mutableStateListOf<DeviceInfo>()

    private val scanCallback: ScanCallback = object: ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.let { res ->
                val device = res.device
                val deviceInfo = DeviceInfo(device.name ?: "???", device.address)
                if (!devicesFound.contains(deviceInfo)) devicesFound.add(deviceInfo)
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
//                    context.finish()
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
        devicesFound.clear()
        scanning.value = true
        scanner.startScan(scanCallback)

        Handler(Looper.getMainLooper()).postDelayed({ ->
            Log.d(DTAG, "Stopping scan")
            stopScan()
        }, 5000)
    }

    override fun stopScan() {
        if (scanning.value) {
            blAdapter.bluetoothLeScanner.stopScan(scanCallback)
            scanning.value = false
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

}