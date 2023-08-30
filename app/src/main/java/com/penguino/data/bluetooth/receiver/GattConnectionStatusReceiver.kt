package com.penguino.data.bluetooth.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.penguino.data.bluetooth.GattService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
// TODO: Create BleConnectionStatus and BleScanStatus
class GattConnectionStatusReceiver : BroadcastReceiver(), BleConnectStatusCapture {
	private var _bleStatus = MutableStateFlow(GattService.STATE_GATT_CONNECTING)
	override val connectionStatus = _bleStatus.asStateFlow()

	override fun onReceive(p0: Context?, p1: Intent?) {
		p1?.action?.let { action ->
			Log.i(TAG, "Status: $action")
			_bleStatus.update { captureBleStatus(action) }
		}
	}

	override fun captureBleStatus(action: String): Int {
		GattService.apply {
			return when (action) {
				ACTION_GATT_CONNECTING -> STATE_GATT_CONNECTING
				ACTION_GATT_CONNECTED -> STATE_GATT_CONNECTED
				ACTION_GATT_DISCONNECTING -> STATE_GATT_DISCONNECTING
				ACTION_GATT_DISCONNECTED -> STATE_GATT_DISCONNECTED
				else -> STATE_GATT_DISCONNECTED
			}
		}
	}

	companion object {
		const val TAG = "GattConnectionStatusReceiver"
	}
}