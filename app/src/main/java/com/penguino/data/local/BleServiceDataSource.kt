package com.penguino.data.local

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.*
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

private const val TAG = "BluetoothLeService"
@SuppressLint("MissingPermission")
class BleServiceDataSource : Service() {
    private var btAdapter: BluetoothAdapter? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private val gattCallback: BluetoothGattCallback = object: BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            Log.d("BAR", newState.toString())

            broadcastUpdate(
                when(newState) {
                    BluetoothProfile.STATE_CONNECTING -> ACTION_GATT_CONNECTING
                    BluetoothProfile.STATE_CONNECTED -> ACTION_GATT_CONNECTED
                    BluetoothProfile.STATE_DISCONNECTING -> ACTION_GATT_DISCONNECTING
                    BluetoothProfile.STATE_DISCONNECTED -> ACTION_GATT_DISCONNECTED
                    else -> ACTION_GATT_DISCONNECTED        // TODO: This might throw an error :/
                }
            )

            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    bluetoothGatt?.discoverServices()
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS)
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            Log.d(
                TAG,
                "Characteristic written: trying to read... [${
                    bluetoothGatt?.readCharacteristic(characteristic)
                }]"
            )
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, value, status)
            Log.d(TAG, "Result: ${value.toString(Charsets.UTF_8)} [$status]")
        }
    }

    private fun broadcastUpdate(action: String) {
        val intent = Intent(action)
        sendBroadcast(intent)
    }

    inner class ServiceBinder: Binder() {
        fun getService(): BleServiceDataSource {
            return this@BleServiceDataSource
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return ServiceBinder()
    }

    fun initialize(): Boolean {
        btAdapter = this.getSystemService(BluetoothManager::class.java).adapter
        if (btAdapter == null) {
            Log.e(TAG, "Unable to initialize adapter.")
            return false
        }
        Log.i(TAG, "Adapter initialized")
        return true
    }

    fun connect(address: String): Boolean {
        if (btAdapter == null) return false
        try {
            val device = btAdapter?.getRemoteDevice(address)
            bluetoothGatt = device?.connectGatt(this, false, gattCallback)
        } catch (e: java.lang.IllegalArgumentException) {
            Log.e(TAG, "Device with provided address not found")
            return false
        }
        return true
    }

    suspend fun writeToPenguino(control: String): Unit = withContext(Dispatchers.IO) {
        val uuidConst = "-0000-1000-8000-00805f9b34fb"
        val chars = bluetoothGatt?.getService(UUID.fromString("0000aaa0$uuidConst"))
            ?.getCharacteristic(UUID.fromString("0000aaaa$uuidConst"))

        // Write characteristic here. wish me luck
        chars?.let {
            val packet = control.toByteArray(Charsets.UTF_8)
            bluetoothGatt?.writeCharacteristic(it, packet, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
        }
    }

    fun disconnect() {
        bluetoothGatt?.let {
            it.close()
            bluetoothGatt = null
            broadcastUpdate(ACTION_GATT_DISCONNECTED)
        }
    }

    companion object {
        const val ACTION_GATT_CONNECTED =
            "com.penguino.BleServiceDataSource.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_CONNECTING =
            "com.penguino.BleServiceDataSource.ACTION_GATT_CONNECTING"
        const val ACTION_GATT_DISCONNECTING =
            "com.penguino.BleServiceDataSource.ACTION_GATT_DISCONNECTING"
        const val ACTION_GATT_DISCONNECTED =
            "com.penguino.BleServiceDataSource.ACTION_GATT_DISCONNECTED"
        const val ACTION_GATT_SERVICES_DISCOVERED =
            "com.penguino.BleServiceDataSource.ACTION_GATT_SERVICES_DISCOVERED"

        const val STATE_DISCONNECTED = BluetoothProfile.STATE_DISCONNECTED
        const val STATE_DISCONNECTING = BluetoothProfile.STATE_DISCONNECTING
        const val STATE_CONNECTING = BluetoothProfile.STATE_CONNECTING
        const val STATE_CONNECTED = BluetoothProfile.STATE_CONNECTED
    }
}