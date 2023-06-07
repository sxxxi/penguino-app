package com.penguino.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguino.models.chat.ChatMessage
import com.penguino.models.RegistrationInfo
import com.penguino.navigation.RemoteControlArgs
import com.penguino.repositories.bluetooth.BleRepository
import com.penguino.repositories.chat.ChatRepository
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
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

	var uiState by mutableStateOf(RemoteControlUiState(
		deviceInfo = args.regInfo ?: RegistrationInfo()
	))

	init {
		viewModelScope.launch {
			chatRepository.latestResponse.collectLatest {
				uiState = uiState.copy(latestResponse = it)
			}

		}

		viewModelScope.launch {
			Log.d("BOOBA", "Setting system message")
			chatRepository.setSystemMessage("You are a super helpful assistant. Your responses must be in the shortest form")
		}
	}

	data class RemoteControlUiState(
		val deviceInfo: RegistrationInfo,
		val latestResponse: ChatMessage? = null
	)

	/**
	 * I'm just doing one liners for now to make it readable
	 * since the business logic is not that complex
	 * yet, but as soon as it becomes complex, I'll convert this to use explicit blocks
	 */
	fun bindService() = btRepository.bindService()
	fun unbindService() = btRepository.unbindService()
	fun connect() = btRepository.connect(uiState.deviceInfo.device)
	fun disconnect() = btRepository.disconnect()
	fun sendMessage(msg: String) = btRepository.sendMessage(msg)

	fun chat(message: String) {
		chatRepository.chat(message = message)
	}
}