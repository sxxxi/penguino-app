package com.penguino.ui.screens

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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.penguino.ui.theme.PenguinoTheme
import com.penguino.ui.viewmodels.RemoteControlViewModel
import com.penguino.ui.viewmodels.RemoteControlViewModel.RemoteControlUiState

@Composable
fun RemoteControlScreen(
    modifier: Modifier = Modifier,
    rcVm: RemoteControlViewModel,
    uiState: RemoteControlUiState,
    chatFunc: (String) -> Unit,
    onNavigateToHome: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when(event) {
                Lifecycle.Event.ON_CREATE -> {
                    rcVm.bindService()
                }

                Lifecycle.Event.ON_PAUSE -> {
                    rcVm.disconnect()
                }

                Lifecycle.Event.ON_RESUME -> {
                    rcVm.connect()
                }

                Lifecycle.Event.ON_DESTROY -> {
                    rcVm.disconnect()
                    rcVm.unbindService()
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val x = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        .putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")

    val speechLaunch = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            it.data?.extras?.getStringArrayList(RecognizerIntent.EXTRA_RESULTS)?.first()?.let { input ->
                chatFunc(input)
            }
        })

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
                Button(onClick = { rcVm.sendMessage("ON") }) {
                    Text(text = "On")
                }
                Button(onClick = { rcVm.sendMessage("OFF") }) {
                    Text(text = "Off")
                }
            }
            IconButton(onClick = { speechLaunch.launch(x) }) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Mic",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewRcScreen() {
    PenguinoTheme {
//        RemoteControlScreen(
//            uiState = RemoteControlUiState(deviceInfo = RegistrationInfo()),
//            onNavigateToHome = {})
    }
}

