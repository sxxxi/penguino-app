package com.penguino.data.repositories.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.penguino.data.local.BleServiceDataSource
import com.penguino.data.local.models.DeviceInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@SuppressLint("MissingPermission")
class BleRepositoryImpl @Inject constructor(
	private val context: Context,
	private val blAdapter: BluetoothAdapter,
): BleRepository {
    private var bleServiceDataSource: BleServiceDataSource? = null

    // Callbacks for binding BluetoothLeService
    private val bluetoothServiceConn = object : ServiceConnection {
        private val TAG = "BluetoothServiceConnection"
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
			Log.i(TAG, "BluetoothLeService Bound")
            bleServiceDataSource = (service as BleServiceDataSource.ServiceBinder).getService()
            bleServiceDataSource?.let { bluetooth ->
                // Connect here and do stuff here on service created
                if (!bluetooth.initialize()) {
					Log.e(TAG, "Unable to initialize service")
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
			Log.d(TAG, "Service Unbound")
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
        context.unbindService(bluetoothServiceConn)
    }

    override suspend fun sendMessage(message: String): Unit = withContext(Dispatchers.IO) {
        bleServiceDataSource?.writeToPengu(message)
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
        const val TAG = "BleRepository"
    }
}