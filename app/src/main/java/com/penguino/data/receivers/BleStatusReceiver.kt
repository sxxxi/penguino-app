package com.penguino.data.receivers

import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.penguino.data.local.BleServiceDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BleStatusReceiver: BroadcastReceiver() {
	private val _connectionState = MutableStateFlow(BleServiceDataSource.STATE_CONNECTING)
	val connectionState: StateFlow<Int> = _connectionState

	override fun onReceive(context: Context?, intent: Intent?) {
		intent?.action.let { action ->
			Log.d("Receiver", action.toString())
			_connectionState.value = when(action) {
				BleServiceDataSource.ACTION_GATT_CONNECTING -> BleServiceDataSource.STATE_CONNECTING
				BleServiceDataSource.ACTION_GATT_CONNECTED -> BleServiceDataSource.STATE_CONNECTED
				BleServiceDataSource.ACTION_GATT_DISCONNECTING -> BleServiceDataSource.STATE_DISCONNECTING
				BleServiceDataSource.ACTION_GATT_DISCONNECTED -> BleServiceDataSource.STATE_DISCONNECTED
				else -> BluetoothProfile.STATE_DISCONNECTED
			}
		}
	}
}