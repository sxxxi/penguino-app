package com.penguino.data.bluetooth

import android.Manifest
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresPermission
import com.penguino.data.bluetooth.contracts.LeService
import java.util.UUID

class PenguinoGattService : Service(), LeService {
	var adapter: BluetoothAdapter? = null
		private set
	private var gatt: BluetoothGatt? = null
//		private set

	private val gattCallback = object : BluetoothGattCallback() {
		@RequiresPermission(value = Manifest.permission.BLUETOOTH_CONNECT)
		override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
			val action = when (newState) {
				STATE_GATT_CONNECTING -> ACTION_GATT_CONNECTING
				STATE_GATT_CONNECTED -> {
					gatt?.discoverServices()
					ACTION_GATT_CONNECTED
				}

				STATE_GATT_DISCONNECTING -> ACTION_GATT_DISCONNECTING
				STATE_GATT_DISCONNECTED -> ACTION_GATT_DISCONNECTED
				else -> ACTION_GATT_DISCONNECTED
			}
			Log.d(TAG, "$status $action")
			broadcastUpdate(action)
			super.onConnectionStateChange(gatt, status, newState)
		}

		override fun onCharacteristicWrite(
			gatt: BluetoothGatt?,
			characteristic: BluetoothGattCharacteristic?,
			status: Int
		) {
			super.onCharacteristicWrite(gatt, characteristic, status)
			Log.d(TAG, "Writing")
		}
	}

	override fun onBind(p0: Intent?): IBinder {
		return BtServiceBinder()
	}

	inner class BtServiceBinder : Binder() {
		fun getService(btAdapter: BluetoothAdapter): PenguinoGattService {
			return this@PenguinoGattService.apply {
				adapter = btAdapter
			}
		}
	}

	/**
	 * Connects device of address.
	 * Throws exception when remote address invalid or device
	 * not found
	 */
	@RequiresPermission(value = Manifest.permission.BLUETOOTH_CONNECT)
	override fun connect(address: String, autoConnect: Boolean) {
		adapter?.let { btAdapter ->
			broadcastUpdate(ACTION_GATT_CONNECTING)
			val device = btAdapter.getRemoteDevice(address)
			gatt = device.connectGatt(this, autoConnect, gattCallback)
		}
	}

	@RequiresPermission(value = Manifest.permission.BLUETOOTH_CONNECT)
	override fun disconnect() {
		broadcastUpdate(ACTION_GATT_DISCONNECTING)
		gatt?.disconnect()
	}

	@RequiresPermission(value = Manifest.permission.BLUETOOTH_CONNECT)
	override fun reconnect() {
		gatt?.connect()
	}

	/**
	 * Closes connection and set gatt to null
	 */
	@RequiresPermission(value = Manifest.permission.BLUETOOTH_CONNECT)
	override fun close() {
		gatt?.let { gut ->
			gut.close()
			gatt = null
		}
	}

	override fun read() {
		throw NotImplementedError("Under development :(")
	}

	/**
	 * TODO: Review this
	 */
	@RequiresPermission(value = Manifest.permission.BLUETOOTH_CONNECT)
	override fun write(message: String) {
		val uuidConst = "-0000-1000-8000-00805f9b34fb"
		val chars = gatt?.getService(UUID.fromString("0000aaa0$uuidConst"))
			?.getCharacteristic(UUID.fromString("0000aaaa$uuidConst"))
		Log.d(TAG, "Services: ${gatt?.services?.map { it.uuid.toString() }}")

		// Write characteristic here. wish me luck
		chars?.let {
			val packet = message.toByteArray(Charsets.UTF_8)
			gatt?.writeCharacteristic(it, packet, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
		}
	}

	private fun broadcastUpdate(state: String) {
		sendBroadcast(Intent(state))
	}

	companion object {
		private const val TAG = "PenguinoGattService"
		private const val prefix = "com.penguino.BtConnectionService"

		// Intent payload
		const val ACTION_GATT_CONNECTING = "$prefix.ACTION_GATT_CONNECTING"
		const val ACTION_GATT_DISCONNECTING = "$prefix.ACTION_GATT_DISCONNECTING"
		const val ACTION_GATT_CONNECTED = "$prefix.ACTION_GATT_CONNECTED"
		const val ACTION_GATT_DISCONNECTED = "$prefix.ACTION_GATT_DISCONNECTED"
		const val ACTION_SCANNING = "$prefix.ACTION_SCANNING"
		const val ACTION_NOT_SCANNING = "$prefix.ACTION_SCANNING"

		// States: Only 4 exist in BluetoothProfile :)
		const val STATE_GATT_CONNECTING = BluetoothProfile.STATE_CONNECTING
		const val STATE_GATT_CONNECTED = BluetoothProfile.STATE_CONNECTED
		const val STATE_GATT_DISCONNECTING = BluetoothProfile.STATE_DISCONNECTING
		const val STATE_GATT_DISCONNECTED = BluetoothProfile.STATE_DISCONNECTED
	}
}