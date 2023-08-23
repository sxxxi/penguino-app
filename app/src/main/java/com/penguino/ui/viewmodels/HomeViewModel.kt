package com.penguino.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguino.data.models.PetInformation
import com.penguino.data.repositories.registration.RegistrationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
	private val registrationRepo: RegistrationRepository,
) : ViewModel() {
	private val _uiState = MutableStateFlow(HomeUiState())
	val uiState: StateFlow<HomeUiState> = _uiState

	init {
		viewModelScope.launch {
			registrationRepo
				.getSaved().collectLatest { saved ->
					_uiState.update { state ->
						state.copy(savedDevices = saved)
					}
				}
		}
	}

	fun forgetDevice(id: String) {
		viewModelScope.launch {
			// clear focused pet before deleting
			uiState.value.focusedPet?.let { pet ->
				if (pet.address == id) {
					setFocusedPet(null)
				}
			}

			registrationRepo.delete(id)
		}
	}

	fun setFocusedPet(pet: PetInformation?) {
		_uiState.update { it.copy(focusedPet = pet) }
	}

	data class HomeUiState(
		val savedDevices: List<PetInformation> = listOf(),
		val focusedPet: PetInformation? = null
	)
}