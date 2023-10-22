package com.penguino.prestentation.rc

import android.bluetooth.BluetoothProfile
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.penguino.data.bluetooth.GattService
import com.penguino.data.utils.ObserveLifecycle
import com.penguino.domain.models.PetInformation
import com.penguino.prestentation.components.Loader
import com.penguino.prestentation.rc.RemoteControlViewModel.RemoteControlUiState
import com.penguino.ui.navigation.Screen
import com.penguino.ui.navigation.chatScreen
import com.penguino.ui.navigation.featuresScreen
import com.penguino.ui.navigation.feedScreen
import com.penguino.ui.navigation.navigateToChat
import com.penguino.ui.navigation.navigateToFeed
import com.penguino.ui.theme.PenguinoTheme

@Composable
fun RemoteControlScreen(
	modifier: Modifier = Modifier,
	uiState: RemoteControlUiState = RemoteControlUiState(PetInformation()),
	btConnectionState: Int = BluetoothProfile.STATE_CONNECTED,
	btServiceBind: () -> Unit = {},
	btServiceUnbind: () -> Unit = {},
	btConnect: () -> Unit = {},
	btDisconnect: () -> Unit = {},
	btMessageSend: (String) -> Unit = {},
	navHostController: NavHostController = rememberNavController()
) {
	/**
	 * Use this variable to tell the screen when not to disconnect to the device when an
	 * action requires to pause. Check the body of ON_RESUME and ON_PAUSE inside the
	 * ObserveLifecycle composable
	 */
	var intentionalPause by rememberSaveable { mutableStateOf(false) }

	fun intentionalPauseSwitch(bool: Boolean) {
		intentionalPause = bool
	}

	ObserveLifecycle(
		lifecycleOwner = LocalLifecycleOwner.current,
		observer = { _, event ->
			when (event) {
				Lifecycle.Event.ON_CREATE -> btServiceBind()
				Lifecycle.Event.ON_RESUME -> {
					if (!intentionalPause) {
						btConnect()
					}
					intentionalPause = false
				}

				Lifecycle.Event.ON_PAUSE -> {
					if (!intentionalPause) {
						btDisconnect()
					}
				}

				else -> {}
			}
		},
		final = {
			btServiceUnbind()
		}
	)
//	NavHost(
//		modifier = Modifier.fillMaxSize(),
//		navController = navHostController,
//		startDestination = Screen.RemoteControlScreen.ChatScreen.route
//	) {
//		chatScreen(intentionalPause = ::intentionalPauseSwitch)
//	}
	when (btConnectionState) {
		GattService.STATE_GATT_CONNECTING -> {
			Column(
				Modifier.fillMaxSize(),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Loader(text = "Connecting")
			}
		}

		GattService.STATE_GATT_CONNECTED -> {
			NavHost(
				modifier = Modifier.fillMaxSize(),
				navController = navHostController,
				startDestination = Screen.RemoteControlScreen.FeaturesScreen.route
			) {
				featuresScreen(
					onNavigateToFeed = navHostController::navigateToFeed,
					onNavigateToChat = navHostController::navigateToChat
				)
				feedScreen(btMessageSend)
				chatScreen(intentionalPause = ::intentionalPauseSwitch, btMessageSend)
			}
		}

		else -> {
			Column(
				Modifier.fillMaxSize(),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text(text = "Cannot connect to device")
			}
		}
	}
}

@Preview
@Composable
fun PreviewRcScreen() {
	PenguinoTheme {
		Box(
			Modifier
				.fillMaxSize()
				.background(MaterialTheme.colorScheme.background)
		) {
			RemoteControlScreen()
		}
	}
}

