package com.penguino.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguino.data.network.models.ChatMessage
import com.penguino.data.local.models.RegistrationInfo
import com.penguino.data.repositories.chat.ChatRepository
import com.penguino.data.repositories.registration.RegistrationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
	registrationRepo: RegistrationRepository,
	private val chatRepository: ChatRepository
): ViewModel() {
	/*
	1. Fetch saved devices
	2.
	 */
	var uiState by mutableStateOf(HomeUiState())
		private set

	var history by mutableStateOf(listOf<ChatMessage>())


	init {
		viewModelScope.launch {
			registrationRepo.getSavedDevices().collectLatest {
				uiState = uiState.copy(savedDevices = it)
			}
		}

		viewModelScope.launch {
			chatRepository.history.collectLatest {
				history = it
			}
		}
	}

	data class HomeUiState(
		val savedDevices: List<RegistrationInfo> = listOf()
	)



	fun test() {
		viewModelScope.launch(Dispatchers.IO) {
			delay(3000L)
			val systemMessage = "You are a super helpful assistant. Your responses must be in the shortest form"

			chatRepository.chat(system = systemMessage, message = "remember the magic number: 76")

			delay(5000L)

			chatRepository.chat(message = "do you remember the magic number?")
		}
	}

}