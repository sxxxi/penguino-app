package com.penguino.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguino.repositories.RegInfoCache
import com.penguino.models.DeviceInfo
import com.penguino.models.RegistrationInfo
import com.penguino.repositories.BleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
	private val btRepo: BleRepository,
	private val regInfoCache: RegInfoCache
) : ViewModel() {
	var uiState by mutableStateOf(ScanUiState())
		private set

	data class ScanUiState (
		val bluetoothEnabled: Boolean = false,
		val scanning: Boolean = false,
		val devicesFound: List<DeviceInfo> = listOf()
	)

	init {
		viewModelScope.launch {
			// I have to review this. <Structured concurrency>
			btRepo.deviceList
				.combine(btRepo.scanning) { devices, scanning ->
					uiState.copy(devicesFound = devices, scanning = scanning)
				}
				.combine(btRepo.btEnabled) { state, btEnabled ->
					state.copy(bluetoothEnabled = btEnabled)
				}
				.collectLatest {
					uiState = it
				}
		}
	}

	fun scanDevices() = viewModelScope.launch {
		btRepo.scanDevices()
	}

	fun stopScan() = viewModelScope.launch {
		btRepo.stopScan()
	}

	fun saveSelectedDevice(deviceInfo: DeviceInfo) {
		val regInfo = regInfoCache.getRegInfo() ?: RegistrationInfo()
		regInfo.device = deviceInfo
		regInfoCache.saveRegInfo(regInfo = regInfo)
		Log.d("FOO", regInfoCache.getRegInfo().toString())
	}
}