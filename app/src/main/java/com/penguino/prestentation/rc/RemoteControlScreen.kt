package com.penguino.prestentation.rc

import android.bluetooth.BluetoothProfile
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.penguino.data.bluetooth.GattService
import com.penguino.data.utils.ObserveLifecycle
import com.penguino.domain.models.PetInformation
import com.penguino.prestentation.components.Loader
import com.penguino.prestentation.components.VerticalGrid
import com.penguino.prestentation.rc.RemoteControlViewModel.RemoteControlUiState
import com.penguino.ui.navigation.Screen
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
	btMessageSend: (String) -> Unit = {}
) {
	/**
	 * Use this variable to tell the screen when not to disconnect to the device when an
	 * action requires to pause. Check the body of ON_RESUME and ON_PAUSE inside the
	 * ObserveLifecycle composable
	 */
	var intentionalPause by rememberSaveable {
		mutableStateOf(false)
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
			ConnectedIteration2(btMessageSend)
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

@Composable
fun ConnectedIteration2(btMessageSend: (String) -> Unit) {

	val testList = mutableListOf<@Composable () -> Unit>()
	val buttonTexts = listOf("Tickle", "Feed", "Action 3", "Action 4", "Action 5", "Action 6")

	repeat(6) { index ->
		testList.add {
			Button(
				modifier = Modifier
					.padding(vertical = 8.dp)
					.size(width = 160.dp, height = 50.dp),
				onClick = {
					Screen.FeedScreen
				}
			) {
				Text(text = buttonTexts[index])
			}
		}
	}

	Column {
		Spacer(modifier = Modifier.weight(1f))
		ButtonGrid()
		Spacer(modifier = Modifier.height(32.dp))
	}

	/*
	val testList = mutableListOf<@Composable () -> Unit>()
	var msg by remember {
		mutableStateOf("ON")
	}
	repeat(6) {
		testList.add {
			Button(
				modifier = Modifier
					.padding(vertical = 8.dp)
					.size(width = 160.dp, height = 50.dp),
				onClick = {

					btMessageSend(msg)
					msg = if (msg == "ON") "OFF" else "ON"
				}
			) {
				Text(text = "Tickle")
			}
		}
	}
	*/
}

@Composable
private fun ButtonGrid() {
	val buttons = remember {
		listOf<@Composable () -> Unit> {
			GridButton(label = "Test") {}
		}
	}
	VerticalGrid(columns = 2, items = buttons)
}

@Composable
private fun GridButton(label: String, onClick: () -> Unit) {
	Button(onClick = onClick) {
		Text(text = label)
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

