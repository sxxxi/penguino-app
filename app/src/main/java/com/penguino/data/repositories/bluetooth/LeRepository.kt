package com.penguino.data.repositories.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresPermission
import com.penguino.data.bluetooth.PenguinoGattService
import com.penguino.data.bluetooth.contracts.LeService
import com.penguino.data.bluetooth.receiver.GattConnectionStatusReceiver
import com.penguino.data.local.models.DeviceInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LeRepository @Inject constructor(
	private val context: Context,
	private val blAdapter: BluetoothAdapter,
) : DeviceConnectionRepository, DeviceDiscoveryRepository {
	private var bleService: LeService? = null
	private val statusReceiver = GattConnectionStatusReceiver()
	override val connectionState = statusReceiver.connectionStatus
	private val scanner = blAdapter.bluetoothLeScanner
	private var latestScanId: Long = -1L

	private var _scanning = MutableStateFlow(false)
	override var scanning: StateFlow<Boolean> = _scanning

	private var _btEnabled = MutableStateFlow(true)
	override var btEnabled: StateFlow<Boolean> = _btEnabled

	// Devices found during the scanning process. (Needs a little more tweak)
	private val mutDeviceSet: MutableSet<DeviceInfo> = mutableSetOf()
	private val _deviceList = MutableStateFlow<List<DeviceInfo>>(listOf())
	override val deviceList: StateFlow<List<DeviceInfo>> = _deviceList

	// Callbacks for binding BluetoothLeService
	private val penguinoGattServiceConn = object : ServiceConnection {
		override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
			Log.i(TAG, "Registering broadcast receiver")
			context.registerReceiver(statusReceiver, IntentFilter().apply {
				addAction(PenguinoGattService.ACTION_GATT_CONNECTING)
				addAction(PenguinoGattService.ACTION_GATT_CONNECTED)
				addAction(PenguinoGattService.ACTION_GATT_DISCONNECTING)
				addAction(PenguinoGattService.ACTION_GATT_DISCONNECTED)
			}, Context.RECEIVER_NOT_EXPORTED)

			try {
				bleService = (service as PenguinoGattService.BtServiceBinder)
					.getService(blAdapter)
			} catch (e: IllegalStateException) {
				Log.e(TAG, "Unable to initialize service")
			}
		}

		// TODO: LORD, WHY IS THIS NOT GETTING CALLED!?
		override fun onServiceDisconnected(name: ComponentName?) {
			Log.i(TAG, "Unbinding status receiver")
			context.unregisterReceiver(statusReceiver)
			Log.i(TAG, "Service Unbound")
		}
	}

	override fun bindService() {
		Intent(context, PenguinoGattService::class.java).also { intent ->
			val bindSuccess: Boolean = context.bindService(
				intent,
				penguinoGattServiceConn,
				Context.BIND_AUTO_CREATE
			)
			if (!bindSuccess) {
				unbindService()
			}
		}
	}

	override fun unbindService() {
		Intent(context, PenguinoGattService::class.java).also {
			context.stopService(it)
			context.unbindService(penguinoGattServiceConn)
		}
	}

	@RequiresPermission(value = Manifest.permission.BLUETOOTH_CONNECT)
	override suspend fun sendMessage(message: String): Unit = withContext(Dispatchers.IO) {
		bleService?.write(message)
	}

	@RequiresPermission(value = Manifest.permission.BLUETOOTH_CONNECT)
	override fun connect(address: String): Boolean {
		bleService?.connect(address)
		return true
	}

	@RequiresPermission(value = Manifest.permission.BLUETOOTH_CONNECT)
	override fun disconnect() {
		bleService?.disconnect()
	}

	override fun btEnabled(): Boolean {
		return blAdapter.isEnabled
	}


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

					Log.d("DeviceDiscovery", deviceList.value.toString())
				}
			}
		}

		override fun onBatchScanResults(results: MutableList<ScanResult>?) {
			super.onBatchScanResults(results)
			Log.d(TAG, results.toString())
		}
	}


	@RequiresPermission(value = Manifest.permission.BLUETOOTH_SCAN)
	override suspend fun scanDevices(durationMillis: Long) = withContext(Dispatchers.Default) {
		Log.d("Scanner", "${scanner == null}")
		if (scanning.value) return@withContext
		val latestId = System.currentTimeMillis()

		mutDeviceSet.clear()
		_deviceList.value = mutDeviceSet.toList()

		_scanning.value = true
		latestScanId = latestId
		scanner.startScan(scanCallback)

		// Don't stop Scanning if there's a new scan id (Feels like mutexes...)
		launch {
			delay(durationMillis)
			if (_scanning.value && latestId >= latestScanId) {
				stopScan()
			}
		}
	}

	@RequiresPermission(value = Manifest.permission.BLUETOOTH_SCAN)
	override fun stopScan() {
		scanner.stopScan(scanCallback)
		_scanning.value = false
	}

	companion object {
		private const val TAG = "LeRepository"
	}
}