package com.penguino.prestentation.rc

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguino.data.repositories.bluetooth.GattRepository
import com.penguino.data.repositories.chat.ChatRepository
import com.penguino.data.repositories.registration.RegistrationRepository
import com.penguino.domain.models.ChatMessage
import com.penguino.domain.models.PetInformation
import com.penguino.ui.navigation.RemoteControlArgs
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemoteControlViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	moshi: Moshi,
	private val btRepository: GattRepository,
	private val chatRepository: ChatRepository,
	private val registrationRepo: RegistrationRepository
): ViewModel() {
	private val args = RemoteControlArgs(savedStateHandle = savedStateHandle, moshi = moshi)
	private val _uiState: MutableStateFlow<RemoteControlUiState> = MutableStateFlow(
		RemoteControlUiState(deviceInfo = PetInformation())
	)
	val uiState: StateFlow<RemoteControlUiState> = _uiState
	val connectionState: StateFlow<Int> = btRepository.connectionState

	init {
		// fetch device
		viewModelScope.launch {
			args.devId?.let {
				val pet = registrationRepo.getById(args.devId)
				_uiState.update { it.copy(deviceInfo = pet ?: PetInformation()) }
			}
		}

		viewModelScope.launch {
			chatRepository.latestResponse.collectLatest { response ->
				_uiState.update { uiState ->
					uiState.copy(latestResponse = response)
				}
			}

		}
		viewModelScope.launch {
			chatRepository.setSystemMessage("You are a super helpful assistant. Your responses " +
					"must be in the shortest form")
		}
	}

	fun bindService() {
		btRepository.bindService()
	}

	fun unbindService() = btRepository.unbindService()

	fun connect() {
		viewModelScope.launch(Dispatchers.IO) {
			btRepository.connect(uiState.value.deviceInfo.address)
		}
	}

	fun disconnect() = btRepository.disconnect()

	fun sendMessage(msg: String) {
		viewModelScope.launch {
			btRepository.sendMessage(msg)
		}
	}

	fun chat(message: String) {
		chatRepository.chat(message = message)
	}

	data class RemoteControlUiState (
		val deviceInfo: PetInformation,
		val latestResponse: ChatMessage? = null
	)
}