package com.penguino.data.repositories.bluetooth

import com.penguino.data.local.models.DeviceInfo
import kotlinx.coroutines.flow.StateFlow

interface DeviceDiscoveryRepository {
	val deviceList: StateFlow<List<DeviceInfo>>
	val btEnabled: StateFlow<Boolean>
	val scanning: StateFlow<Boolean>

	suspend fun scanDevices(durationMillis: Long)
	fun stopScan()
}