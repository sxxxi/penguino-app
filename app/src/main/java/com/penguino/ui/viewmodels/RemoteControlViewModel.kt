package com.penguino.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguino.data.network.models.ChatMessage
import com.penguino.data.local.models.RegistrationInfoEntity
import com.penguino.ui.navigation.RemoteControlArgs
import com.penguino.data.repositories.bluetooth.BleRepository
import com.penguino.data.repositories.chat.ChatRepository
import com.penguino.models.PetInfo
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemoteControlViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	moshi: Moshi,
	private val btRepository: BleRepository,
	private val chatRepository: ChatRepository
): ViewModel() {
	private val args = RemoteControlArgs(savedStateHandle = savedStateHandle, moshi = moshi)
	var uiState by mutableStateOf(
		RemoteControlUiState(
			deviceInfo = args.regInfo ?: PetInfo()
		)
	)

	init {
		viewModelScope.launch {
			chatRepository.latestResponse.collectLatest {
				uiState = uiState.copy(latestResponse = it)
			}

		}

		viewModelScope.launch {
			chatRepository.setSystemMessage("You are a super helpful assistant. Your responses must be in the shortest form")
		}
	}

	data class RemoteControlUiState(
		val deviceInfo: PetInfo,
		val latestResponse: ChatMessage? = null
	)

	/**
	 * I'm just doing one liners for now to make it readable
	 * since the business logic is not that complex
	 * yet, but as soon as it becomes complex, I'll convert this to use explicit blocks
	 */
	fun bindService() = btRepository.bindService()
	fun unbindService() = btRepository.unbindService()
	fun connect() {
		viewModelScope.launch(Dispatchers.IO) {
			btRepository.connect(uiState.deviceInfo.address)
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
}