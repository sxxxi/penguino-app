package com.penguino.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguino.models.RegistrationInfo
import com.penguino.navigation.PetInfoArgs
import com.penguino.repositories.bluetooth.BleRepository
import com.penguino.repositories.registration.RegistrationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PetInfoViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val registrationRepo: RegistrationRepository
): ViewModel() {
	private val args = PetInfoArgs(savedStateHandle)
	var uiState by mutableStateOf(
		PetInfoUiState(selectedDevice = args.selectedDevice ?: RegistrationInfo())
	)
		private set

	data class PetInfoUiState(
		val selectedDevice: RegistrationInfo = RegistrationInfo()
	)

	fun deleteRegInfo() = viewModelScope.launch {
		registrationRepo.forgetDevice(uiState.selectedDevice)
	}
}