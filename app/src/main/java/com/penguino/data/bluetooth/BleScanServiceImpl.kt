package com.penguino.data.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.util.Log
import androidx.annotation.RequiresPermission
import com.penguino.data.bluetooth.contracts.BleScanService
import com.penguino.data.local.models.DeviceInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BleScanServiceImpl @Inject constructor(
	adapter: BluetoothAdapter
) : BleScanService {
	private val scanner = adapter.bluetoothLeScanner
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
		@RequiresPermission(value = Manifest.permission.BLUETOOTH_CONNECT)
		override fun onScanResult(callbackType: Int, result: ScanResult?) {
			super.onScanResult(callbackType, result)
			result?.device?.let { device ->
				device.name?.let { name ->
//                    if (name.lowercase() != "penguino") return

					val deviceInfo = DeviceInfo(deviceName = name, address = device.address)

					mutDeviceSet.add(deviceInfo)
					_deviceList.value = mutDeviceSet.toList()

					Log.d(TAG, deviceList.value.toString())
				}
			}
		}

		override fun onBatchScanResults(results: MutableList<ScanResult>?) {
			super.onBatchScanResults(results)
			Log.d(TAG, results.toString())
		}
	}

	@RequiresPermission(value = Manifest.permission.BLUETOOTH_SCAN)
	override suspend fun scanDevices(durationMillis: Long): Unit =
		withContext(Dispatchers.Default) {
			scanner?.let { leScanner ->
				if (scanning.value) return@withContext
				val latestId = System.currentTimeMillis()

				mutDeviceSet.clear()
				_deviceList.value = mutDeviceSet.toList()

				_scanning.value = true
				latestScanId = latestId
				leScanner.startScan(scanCallback)

				// Don't stop Scanning if there's a new scan id (Feels like mutexes...)
				launch {
					delay(durationMillis)
					if (_scanning.value && latestId >= latestScanId) {
						stopScan()
					}
				}
			}
		}

	@RequiresPermission(value = Manifest.permission.BLUETOOTH_SCAN)
	override fun stopScan() {
		scanner?.stopScan(scanCallback)
		_scanning.value = false
	}

	companion object {
		private const val TAG = "BleScanImpl"
	}
}