package com.penguino.prestentation.rc

import android.bluetooth.BluetoothProfile
import android.content.Intent
import android.speech.RecognizerIntent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import com.penguino.prestentation.rc.RemoteControlViewModel.RemoteControlUiState
import com.penguino.prestentation.rc.feed.FeedScreen
import com.penguino.ui.navigation.Screen
import com.penguino.ui.theme.PenguinoTheme
import kotlin.math.ceil

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
//			ConnectedIteration1(uiState = uiState, btMessageSend = btMessageSend) {
//				intentionalPause = true
//				speechLaunch.launch(speechRecognizerIntent)
//			}

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
fun ConnectedIteration1(
	uiState: RemoteControlUiState,
	btMessageSend: (String) -> Unit,
	onIconButtonClicked: () -> Unit,
) {
	Column(Modifier.fillMaxSize()) {
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
			IconButton(onClick = onIconButtonClicked) {
				Icon(
					imageVector = Icons.Default.Star,
					contentDescription = "Mic",
					tint = MaterialTheme.colorScheme.onBackground
				)
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
	Column {
		Spacer(modifier = Modifier.weight(1f))
		VerticalGrid(columns = 2, items = testList)
		Spacer(modifier = Modifier.height(32.dp))
	}

}

@Composable
fun VerticalGrid(columns: Int, items: List<@Composable () -> Unit>) {
	LazyColumn {
		var current = 0
		val rows = ceil(items.size / columns.toFloat()).toInt()
		items(count = rows) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceEvenly,
				verticalAlignment = Alignment.CenterVertically
			) {
				repeat(columns) {
					if (current < items.size)
						items[current++]()
				}

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

