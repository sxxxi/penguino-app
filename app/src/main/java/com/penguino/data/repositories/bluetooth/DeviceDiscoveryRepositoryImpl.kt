package com.penguino.data.repositories.bluetooth

import com.penguino.data.bluetooth.contracts.BleScanService
import com.penguino.data.local.models.DeviceInfo
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class DeviceDiscoveryRepositoryImpl @Inject constructor(
	private val scanner: BleScanService
) : DeviceDiscoveryRepository {
	override val deviceList: StateFlow<List<DeviceInfo>> = scanner.deviceList
	override val btEnabled: StateFlow<Boolean> = scanner.btEnabled
	override val scanning: StateFlow<Boolean> = scanner.scanning

	override suspend fun scanDevices(durationMillis: Long) = scanner.scanDevices(durationMillis)
	override fun stopScan() = scanner.stopScan()

}