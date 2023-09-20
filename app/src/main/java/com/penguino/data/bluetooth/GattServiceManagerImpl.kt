package com.penguino.data.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.penguino.data.bluetooth.contracts.GattServiceManager
import com.penguino.data.bluetooth.contracts.LeService
import com.penguino.data.bluetooth.receiver.GattConnectionStatusReceiver
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Responsible for managing and exposing the LeService and the connection status.
 * Initialize bleService by calling bind(), otherwise call unbind() to set bleService to null
 */
class GattServiceManagerImpl @Inject constructor(
	private val context: Context,
	private val blAdapter: BluetoothAdapter,
) : GattServiceManager {
	private val statusReceiver = GattConnectionStatusReceiver()
	private var mutBleService: LeService? = null
	override val bleService: LeService? = mutBleService
	override val connectionState: StateFlow<Int> = statusReceiver.connectionStatus
	// Callbacks for binding BluetoothLeService
	private val gattServiceConn = object : ServiceConnection {
		override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
			Log.i(TAG, "Registering broadcast receiver")
			context.registerReceiver(statusReceiver, IntentFilter().apply {
				addAction(GattService.ACTION_GATT_CONNECTING)
				addAction(GattService.ACTION_GATT_CONNECTED)
				addAction(GattService.ACTION_GATT_DISCONNECTING)
				addAction(GattService.ACTION_GATT_DISCONNECTED)
			}, Context.RECEIVER_NOT_EXPORTED)

			try {
				mutBleService = (service as GattService.BtServiceBinder)
					.getService(blAdapter)
				Log.i(TAG, mutBleService.toString())
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

	override fun bind() {
		Intent(context, GattService::class.java).also { intent ->
			val bindSuccess: Boolean = context.bindService(
				intent,
				gattServiceConn,
				Context.BIND_AUTO_CREATE
			)
			if (!bindSuccess) {
				unbind()
			}
		}
	}

	override fun unbind() {
		Intent(context, GattService::class.java).also {
			context.stopService(it)
			context.unbindService(gattServiceConn)
		}
	}

	companion object {
		private const val TAG = "GattServiceManagerImpl"
	}
}