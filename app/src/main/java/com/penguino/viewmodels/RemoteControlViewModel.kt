package com.penguino.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.penguino.models.DeviceInfo
import com.penguino.navigation.RemoteControlArgs
import com.penguino.repositories.BleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RemoteControlViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val btRepository: BleRepository
): ViewModel() {
	private val args = RemoteControlArgs(savedStateHandle = savedStateHandle)
	val uiState by mutableStateOf(RemoteControlUiState(
		deviceInfo = args.rcDevice
	))

	data class RemoteControlUiState(
		val deviceInfo: DeviceInfo
	)

	/**
	 * I'm just doing one liners for now to make it readable
	 * since the business logic is not that complex
	 * yet, but as soon as it becomes complex, I'll convert this to use explicit blocks
	 */
	fun bindService() = btRepository.bindService()
	fun unbindService() = btRepository.unbindService()
	fun connect() = btRepository.connect(uiState.deviceInfo)
	fun disconnect() = btRepository.disconnect()
	fun sendMessage(msg: String) = btRepository.sendMessage(msg)

}