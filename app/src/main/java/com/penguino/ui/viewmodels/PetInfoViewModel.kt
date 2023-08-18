package com.penguino.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguino.data.models.PetInfo
import com.penguino.data.repositories.registration.RegistrationRepository
import com.penguino.ui.navigation.PetInfoArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PetInfoViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val registrationRepo: RegistrationRepository
) : ViewModel() {
	private val args = PetInfoArgs(savedStateHandle)

	private val _uiState =
		MutableStateFlow(PetInfoUiState(selectedDevice = args.selectedDevice ?: PetInfo()))
	var uiState: StateFlow<PetInfoUiState> = _uiState

	fun deleteRegInfo() = viewModelScope.launch {
		registrationRepo.forgetDevice(uiState.value.selectedDevice)
	}

	data class PetInfoUiState(
		val selectedDevice: PetInfo = PetInfo()
	)
}