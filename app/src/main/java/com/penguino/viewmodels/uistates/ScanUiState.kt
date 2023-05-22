package com.penguino.viewmodels.uistates

import com.penguino.models.DeviceInfo

data class ScanUiState (
	val bluetoothEnabled: Boolean = false,
	val scanning: Boolean = false,
	val devicesFound: List<DeviceInfo> = listOf()
)