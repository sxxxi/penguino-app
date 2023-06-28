package com.penguino.data.repositories.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.penguino.data.local.BleServiceDataSource
import com.penguino.data.local.models.DeviceInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@SuppressWarnings("MissingPermission")
class DeviceDiscoveryRepositoryImpl(
	btAdapter: BluetoothAdapter
) : DeviceDiscoveryRepository {
	private val scanner = btAdapter.bluetoothLeScanner
	private var latestScanId: Long = -1L

	private var _scanning = MutableStateFlow(false)
	override var scanning: StateFlow<Boolean> = _scanning

	private var _btEnabled = MutableStateFlow(true)
	override var btEnabled: StateFlow<Boolean> = _btEnabled

	// Devices found during the scanning process. (Needs a little more tweak)
	private val mutDeviceSet: MutableSet<DeviceInfo> = mutableSetOf()
	private val _deviceList = MutableStateFlow<List<DeviceInfo>>(listOf())
	override val deviceList: StateFlow<List<DeviceInfo>> = _deviceList

	private val scanCallback = object : ScanCallback() {
		override fun onScanResult(callbackType: Int, result: ScanResult?) {
			super.onScanResult(callbackType, result)
			result?.device?.let { device ->
				device.name?.let { name ->
//                    if (name.lowercase() != "penguino") return

					val deviceInfo = DeviceInfo(deviceName = name, address = device.address)

					mutDeviceSet.add(deviceInfo)
					_deviceList.value = mutDeviceSet.toList()

					Log.d("DeviceDiscovery", deviceList.value.toString())
				}
			}
		}
	}

	override fun scanDevices(durationMillis: Long) {
		if (scanning.value) return
		val latestId = System.currentTimeMillis()

		mutDeviceSet.clear()
		_deviceList.value = mutDeviceSet.toList()

		_scanning.value = true
		latestScanId = latestId
		scanner.startScan(scanCallback)

		// Don't stop Scanning if there's a new scan id (Feels like mutexes...)
		Handler(Looper.getMainLooper()).postDelayed({
			if (_scanning.value && latestId >= latestScanId) {
				stopScan()
			}
		}, durationMillis)
	}

	override fun stopScan() {
		scanner.stopScan(scanCallback)
		_scanning.value = false
	}
}