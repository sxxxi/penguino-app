package com.penguino.ui.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.penguino.prestentation.rc.RemoteControlScreen
import com.penguino.prestentation.rc.RemoteControlViewModel
import com.squareup.moshi.Moshi

const val rcDeviceArg = "devId"
internal data class RemoteControlArgs(
	private val moshi: Moshi,
	private val savedStateHandle: SavedStateHandle,
) {
	val devId: String? = savedStateHandle.get<String>(rcDeviceArg)
}

fun NavGraphBuilder.remoteControlScreen() {
	composable(
		route = Screen.RemoteControlScreen.routeWithArgs,
		arguments = Screen.RemoteControlScreen.arguments
	) {
		val remoteControlViewModel = hiltViewModel<RemoteControlViewModel>()
		val uiState by remoteControlViewModel.uiState.collectAsStateWithLifecycle()
		val connectionState by remoteControlViewModel.connectionState.collectAsStateWithLifecycle()
		RemoteControlScreen(
			uiState = uiState,
			chatFunc = remoteControlViewModel::chat,
			btConnectionState = connectionState,
			btServiceBind = remoteControlViewModel::bindService,
			btServiceUnbind = remoteControlViewModel::unbindService,
			btConnect = remoteControlViewModel::connect,
			btDisconnect = remoteControlViewModel::disconnect,
			btMessageSend = remoteControlViewModel::sendMessage
		)
	}
}

fun NavController.navigateToRemoteControl(devId: String, popToHome: Boolean = false) {
	val routeWithArgs = "${Screen.RemoteControlScreen.route}/$devId"
	navigate(routeWithArgs) {
		if (popToHome) {
			popUpTo(Screen.HomeScreen.route)
		}
	}
}
