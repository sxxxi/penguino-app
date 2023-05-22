package com.penguino.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.penguino.navigation.RemoteControlArgs
import com.penguino.repositories.BleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RemoteControlViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	btRepository: BleRepository
): ViewModel() {
	private val args = RemoteControlArgs(savedStateHandle = savedStateHandle)
	val deviceInfo = args.rcDevice
}