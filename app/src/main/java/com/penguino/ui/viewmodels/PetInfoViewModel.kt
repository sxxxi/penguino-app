package com.penguino.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguino.data.local.models.RegistrationInfoEntity
import com.penguino.ui.navigation.PetInfoArgs
import com.penguino.data.repositories.registration.RegistrationRepository
import com.penguino.models.PetInfo
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
		PetInfoUiState(selectedDevice = args.selectedDevice ?: PetInfo())
	)
		private set

	data class PetInfoUiState(
		val selectedDevice: PetInfo = PetInfo()
	)

	fun deleteRegInfo() = viewModelScope.launch {
		registrationRepo.forgetDevice(uiState.selectedDevice)
	}
}