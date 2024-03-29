package com.penguino.prestentation.scan

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguino.data.cache.DeviceInfoCache
import com.penguino.data.local.models.DeviceInfo
import com.penguino.data.repositories.bluetooth.DeviceDiscoveryRepository
import com.penguino.data.repositories.registration.RegistrationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
	private val btRepo: DeviceDiscoveryRepository,
	private val regRepo: RegistrationRepository,
	private val deviceInfoCache: DeviceInfoCache
) : ViewModel() {
	private var _uiState = MutableStateFlow(ScanUiState())
	val uiState: StateFlow<ScanUiState> = _uiState

	init {
		viewModelScope.launch {
			// <Structured concurrency>
			btRepo.deviceList
				.combine(btRepo.scanning) { devices, scanning ->
					_uiState.value.copy(devicesFound = devices, scanning = scanning)
				}
				.combine(btRepo.btEnabled) { state, btEnabled ->
					state.copy(bluetoothEnabled = btEnabled)
				}
				.collectLatest { us ->
					_uiState.update { us }
				}
		}
	}

	fun scanDevices() {
		viewModelScope.launch {
			btRepo.scanDevices(3000L)
		}
	}

	fun stopScan() {
		viewModelScope.launch(Dispatchers.IO) {
			btRepo.stopScan()
		}
	}

	private suspend fun makeError(msg: String, durationMillis: Long = 5000L) {
		_uiState.update {
			it.copy(isError = true, errorMessage = msg)
		}
		delay(durationMillis)
		_uiState.update {
			it.copy(isError = false, errorMessage = "")
		}
	}

	fun saveSelectedDevice(deviceInfo: DeviceInfo): Boolean {
		val exists = regRepo.deviceExists(deviceInfo.address)

		if (exists) {
			viewModelScope.launch(Dispatchers.IO) {
				makeError("Device already exists")
			}
			return false
		}

		viewModelScope.launch(Dispatchers.IO) {
			deviceInfoCache.saveSelectedDevice(deviceInfo)
			Log.d("FOO", deviceInfoCache.getSelectedDevice().toString())
		}
		return true
	}

	data class ScanUiState(
		val bluetoothEnabled: Boolean = false,
		val scanning: Boolean = false,
		val devicesFound: List<DeviceInfo> = listOf(),
		val isError: Boolean = false,
		val errorMessage: String = "",
		val errorDuration: SnackbarDuration = SnackbarDuration.Short
	)
}