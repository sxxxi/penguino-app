package com.penguino.prestentation.rc

import android.bluetooth.BluetoothProfile
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.penguino.data.bluetooth.PenguinoGattService
import com.penguino.data.utils.ObserveLifecycle
import com.penguino.domain.models.PetInformation
import com.penguino.prestentation.components.Loader
import com.penguino.prestentation.rc.RemoteControlViewModel.RemoteControlUiState
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
	chatFunc: (String) -> Unit = {},
) {
	/**
	 * Use this variable to tell the screen when not to disconnect to the device when an
	 * action requires to pause. Check the body of ON_RESUME and ON_PAUSE inside the
	 * ObserveLifecycle composable
	 */
	var intentionalPause by rememberSaveable {
		mutableStateOf(false)
	}

	ObserveLifecycle(lifecycleOwner = LocalLifecycleOwner.current,
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

	val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
		.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
		.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")

	val speechLaunch = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.StartActivityForResult(),
		onResult = {
			it.data?.extras?.getStringArrayList(RecognizerIntent.EXTRA_RESULTS)?.first()
				?.let { input ->
					chatFunc(input)
				}
		})

	when (btConnectionState) {
		PenguinoGattService.STATE_GATT_CONNECTING -> {
			Column(
				Modifier.fillMaxSize(),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Loader(text = "Connecting")
			}
		}

		PenguinoGattService.STATE_GATT_CONNECTED -> {
			Column(modifier.fillMaxSize()) {
				Column(
					modifier = Modifier
						.fillMaxWidth()
						.weight(1f),
					verticalArrangement = Arrangement.Center,
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					uiState.latestResponse?.let {
						Text(text = it.content, color = MaterialTheme.colorScheme.onBackground)
					}
				}

				Column(
					modifier = Modifier
						.fillMaxWidth()
						.padding(8.dp),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
						Button(onClick = { btMessageSend("ON") }) {
							Text(text = "On")
						}
						Button(onClick = { btMessageSend("OFF") }) {
							Text(text = "Off")
						}
					}
					IconButton(onClick = {
						intentionalPause = true
						speechLaunch.launch(speechRecognizerIntent)
					}) {
						Icon(
							imageVector = Icons.Default.Star,
							contentDescription = "Mic",
							tint = MaterialTheme.colorScheme.onBackground
						)
					}
				}
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
		RemoteControlScreen()
	}
}

