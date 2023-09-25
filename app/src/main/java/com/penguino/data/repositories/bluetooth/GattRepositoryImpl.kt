package com.penguino.data.repositories.bluetooth

import android.Manifest
import androidx.annotation.RequiresPermission
import com.penguino.data.bluetooth.contracts.GattServiceManager
import com.penguino.data.bluetooth.contracts.LeService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GattRepositoryImpl @Inject constructor(
	private val gattServiceManager: GattServiceManager,
) : GattRepository {

	private val scope = CoroutineScope(Dispatchers.Default)
	private var bleService: LeService? = null
	override val connectionState = gattServiceManager.connectionState

	init {
		scope.launch {
			gattServiceManager.leService.collectLatest {
				bleService = it
			}
		}
	}

	override fun bindService() = gattServiceManager.bind()

	override fun unbindService() = gattServiceManager.unbind()

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

	companion object {
		private const val TAG = "LeRepository"
	}
}