package com.penguino.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguino.data.repositories.bluetooth.DeviceDiscoveryRepository
import com.penguino.data.repositories.registration.RegistrationRepository
import com.penguino.models.PetInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
	private val registrationRepo: RegistrationRepository,
	private val deviceDiscoveryRepo: DeviceDiscoveryRepository,
): ViewModel() {
	private var launchedJob: Job? = null
	private val _uiState = MutableStateFlow(HomeUiState())
	val uiState: StateFlow<HomeUiState> = _uiState

	init {
		viewModelScope.launch {
			registrationRepo
				.getSavedDevices()
				.collectLatest {
					_uiState.value = _uiState.value.copy(savedDevices = it)
			}
		}
	}

	data class HomeUiState(
		val savedDevices: List<PetInfo> = listOf()
	)

	/**
	 * Set private local job reference holder so that it can be
	 * cancelled by other functions (onScreenExit)
	 */
	fun onScreenLaunch() {
		Log.i("FOO", "STARTING STARTUP JOB")
		launchedJob = updateNearbyDevices()
	}

	/**
	 * Call to dispose onScreenLaunch
	 */
	fun onScreenExit() {
		viewModelScope.launch(context = Dispatchers.Default) {
			Log.d("FOO", "STOPPING STARTUP JOB $launchedJob")
			launchedJob?.cancelAndJoin()
			launchedJob = null
		}
	}

	private fun updateNearbyDevices() = viewModelScope.launch(Dispatchers.Default) {
		/**
		 * This coroutine will handle scanning nearby devices
		 * once every 10 seconds
		 *
		 * POSSIBLE OPTIMIZATIONS: Use work manager instead of
		 * using coroutines? (Still have to learn it)
		 */
		launch(Dispatchers.Default) {
			try {
				while (isActive) {
					deviceDiscoveryRepo.scanDevices(5000L)
					delay(15000L)
				}
			} finally {
				deviceDiscoveryRepo.stopScan()
			}
		}

		/**
		 * Listens to changes in both the list of saved devices in
		 * the database and the list of discovered devices, and
		 * changes the status of each of the saved devices accordingly
		 */
		_uiState
			.combine(deviceDiscoveryRepo.deviceList) { savedDevices, nearbyDevices ->
				savedDevices.savedDevices.map { saved ->
					val isNearby = nearbyDevices
						.find { nearby -> nearby.address == saved.address } != null
					// only update on value change
					if (saved.isNearby != isNearby) {
						saved.copy(isNearby = isNearby)
					} else {
						saved
					}
				}
			}
			.collectLatest {
				_uiState.value = _uiState.value.copy(savedDevices = it)
			}
	}
}