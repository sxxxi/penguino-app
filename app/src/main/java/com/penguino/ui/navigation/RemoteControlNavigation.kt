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
import com.penguino.prestentation.rc.pages.FeaturesScreen
import com.penguino.prestentation.rc.pages.FeedScreen
import com.penguino.prestentation.rc.pages.chat.ChatScreen
import com.penguino.prestentation.rc.pages.chat.ChatViewModel
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
			btConnectionState = connectionState,
			btServiceBind = remoteControlViewModel::bindService,
			btServiceUnbind = remoteControlViewModel::unbindService,
			btConnect = remoteControlViewModel::connect,
			btDisconnect = remoteControlViewModel::disconnect,
			btMessageSend = remoteControlViewModel::sendMessage,
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

// Child navigation nodes
/**
 * Nav node for the buttons page
 */
fun NavGraphBuilder.featuresScreen(
	onNavigateToFeed: () -> Unit,
	onNavigateToChat: () -> Unit,
) {
	composable(route = Screen.RemoteControlScreen.FeaturesScreen.route) {
		val vm = hiltViewModel<RemoteControlViewModel>()
		FeaturesScreen(
			btMessageSend = vm::sendMessage,
			onNavigateToFeed = onNavigateToFeed,
			onNavigateToChat = onNavigateToChat
		)
	}
}

fun NavGraphBuilder.feedScreen(
	btSend: (String) -> Unit
) {
	composable(route = Screen.RemoteControlScreen.FeedScreen.route) {
		FeedScreen(btSend)
	}
}

fun NavController.navigateToFeatures() {
	navigate(route = Screen.RemoteControlScreen.FeaturesScreen.route)
}

fun NavController.navigateToFeed() {
	navigate(route = Screen.RemoteControlScreen.FeedScreen.route)
}

fun NavGraphBuilder.chatScreen(
	intentionalPause: (Boolean) -> Unit,
	sendMessage: (String) -> Unit
) {
    composable(route = Screen.ChatScreen.route) {
        val chatViewModel = hiltViewModel<ChatViewModel>()
        val uiState by chatViewModel.uiState.collectAsStateWithLifecycle()
		ChatScreen(
			uiState = uiState,
			chat = chatViewModel::sendMessage,
			chat2 = chatViewModel::tts,
			intentionalPause = intentionalPause,
			sendMessage = sendMessage
		)
    }
}

fun NavController.navigateToChat() {
    navigate(Screen.ChatScreen.route)
}