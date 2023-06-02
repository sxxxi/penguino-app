package com.penguino.viewmodels

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.penguino.repositories.RegInfoCache
import com.penguino.models.DeviceInfo
import com.penguino.models.RegistrationInfo
import com.penguino.repositories.BleRepository
import com.penguino.repositories.RegistrationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@HiltViewModel
class ScanViewModel @Inject constructor(
	private val btRepo: BleRepository,
	private val regRepo: RegistrationRepository,
	private val regInfoCache: RegInfoCache
) : ViewModel() {
	private var _uiState = MutableStateFlow(ScanUiState())
	val uiState: StateFlow<ScanUiState> = _uiState

	data class ScanUiState (
		val bluetoothEnabled: Boolean = false,
		val scanning: Boolean = false,
		val devicesFound: List<DeviceInfo> = listOf(),
		val isError: Boolean = false,
		val errorMessage: String = "",
		val errorDuration: SnackbarDuration = SnackbarDuration.Short
	)

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
		scanDevices()
	}

	fun scanDevices() = viewModelScope.launch {
		_uiState.update { state ->
			state.copy(scanning = true)
		}
		btRepo.scanDevices()
	}

	fun stopScan() = viewModelScope.launch {
		btRepo.stopScan()
		_uiState.update { state ->
			state.copy(scanning = false)
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
			viewModelScope.launch(Dispatchers.Main) {
				makeError("Device already exists")
			}
			return false
		}

		viewModelScope.launch(Dispatchers.Main) {
			val regInfo = regInfoCache.getRegInfo() ?: RegistrationInfo()
			regInfo.device = deviceInfo
			regInfoCache.saveRegInfo(regInfo = regInfo)
			Log.d("FOO", regInfoCache.getRegInfo().toString())
		}
		return true
	}
}