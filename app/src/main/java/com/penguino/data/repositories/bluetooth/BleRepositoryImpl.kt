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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@SuppressLint("MissingPermission")
class BleRepositoryImpl @Inject constructor(
	private val context: Context,
	private val blAdapter: BluetoothAdapter,
): BleRepository {
    private var bleServiceDataSource = MutableStateFlow<BleServiceDataSource?>(null)
    private val statusReceiver = BleStatusReceiver()
    override val connectionState = statusReceiver.connectionState

    // Callbacks for binding BluetoothLeService
    private val bluetoothServiceConn = object : ServiceConnection {
        private val TAG = "BluetoothServiceConnection"
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
			Log.i(TAG, "BluetoothLeService Bound")
            bleServiceDataSource.value = (service as BleServiceDataSource.ServiceBinder).getService()
            bleServiceDataSource.value?.let { bluetooth ->
                // Connect here and do stuff here on service created
                if (!bluetooth.initialize()) {
					Log.e(TAG, "Unable to initialize service")
                }
                Log.d("BAR", "SERVICE BOUND")
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
			Log.d(TAG, "Service Unbound")
        }
    }

    init {
    	context.registerReceiver(statusReceiver, IntentFilter().apply {
            addAction(BleServiceDataSource.ACTION_GATT_CONNECTING)
            addAction(BleServiceDataSource.ACTION_GATT_CONNECTED)
            addAction(BleServiceDataSource.ACTION_GATT_DISCONNECTING)
            addAction(BleServiceDataSource.ACTION_GATT_DISCONNECTED)
        })
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
        context.unbindService(bluetoothServiceConn)
    }

    override suspend fun sendMessage(message: String): Unit = withContext(Dispatchers.IO) {
        bleServiceDataSource.value?.writeToPenguino(message)
    }

    override fun connect(address: String): Boolean {
        return bleServiceDataSource.value?.connect(address) ?: false
    }

    override fun disconnect() {
        bleServiceDataSource.value?.disconnect()
    }

    override fun btEnabled(): Boolean {
        return blAdapter.isEnabled
    }

    companion object {
        const val TAG = "BleRepository"
    }
}