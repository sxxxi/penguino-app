package com.penguino.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguino.models.RegistrationInfo
import com.penguino.repositories.RegistrationRepository
import com.penguino.room.dao.DeviceDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
	registrationRepo: RegistrationRepository
): ViewModel() {
	/*
	1. Fetch saved devices
	2.
	 */
	var uiState by mutableStateOf(HomeUiState())
		private set

	init {
		viewModelScope.launch {
			registrationRepo.getSavedDevices().collectLatest {
				uiState = uiState.copy(savedDevices = it)
			}
		}
	}

	data class HomeUiState(
		val savedDevices: List<RegistrationInfo> = listOf()
	)

}