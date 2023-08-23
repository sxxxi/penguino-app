package com.penguino.data.repositories.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.penguino.data.local.BleServiceDataSource
import com.penguino.data.receivers.BleStatusReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


@SuppressLint("MissingPermission")
class BleRepositoryImpl @Inject constructor(
	private val context: Context,
	private val blAdapter: BluetoothAdapter,
) : BleRepository {
	private var bleServiceDataSource: BleServiceDataSource? = null
	private val statusReceiver = BleStatusReceiver()
	override val connectionState = statusReceiver.connectionState

	// Callbacks for binding BluetoothLeService
	private val bluetoothServiceConn = object : ServiceConnection {
		override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
			Log.i(TAG, "Registering broadcast receiver")
			context.registerReceiver(statusReceiver, IntentFilter().apply {
				addAction(BleServiceDataSource.ACTION_GATT_CONNECTING)
				addAction(BleServiceDataSource.ACTION_GATT_CONNECTED)
				addAction(BleServiceDataSource.ACTION_GATT_DISCONNECTING)
				addAction(BleServiceDataSource.ACTION_GATT_DISCONNECTED)
			}, Context.RECEIVER_NOT_EXPORTED)

			bleServiceDataSource = (service as BleServiceDataSource.ServiceBinder).getService()
			bleServiceDataSource?.let { bluetooth ->
				// Connect here and do stuff here on service created
				if (!bluetooth.initialize()) {
					Log.e(TAG, "Unable to initialize service")
				} else {
					Log.i(TAG, "BLE service bound")
				}
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
		Intent(context, BleServiceDataSource::class.java).also { intent ->
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
		Intent(context, BleServiceDataSource::class.java).also {
			context.stopService(it)
			context.unbindService(bluetoothServiceConn)
		}
	}

	override suspend fun sendMessage(message: String): Unit = withContext(Dispatchers.IO) {
		bleServiceDataSource?.writeToPenguino(message)
	}

	override fun connect(address: String): Boolean {
		return bleServiceDataSource?.connect(address) ?: false
	}

	override fun disconnect() {
		bleServiceDataSource?.disconnect()
	}

	override fun btEnabled(): Boolean {
		return blAdapter.isEnabled
	}

	companion object {
		private const val TAG = "BleRepository"
	}
}