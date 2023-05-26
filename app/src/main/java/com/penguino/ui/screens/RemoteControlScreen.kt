package com.penguino.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.penguino.viewmodels.RemoteControlViewModel
import com.penguino.viewmodels.RemoteControlViewModel.RemoteControlUiState

@Composable
fun RemoteControlScreen(
    modifier: Modifier = Modifier,
    rcVm: RemoteControlViewModel,
    uiState: RemoteControlUiState,
    onNavigateToHome: () -> Unit
) {
    val deviceInfo = uiState.deviceInfo
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when(event) {
                Lifecycle.Event.ON_CREATE -> {
                    rcVm.bindService()
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

    Column(modifier = modifier.fillMaxSize().padding(8.dp)) {
        Column {
            Text(
                text = deviceInfo.name,
                style = MaterialTheme.typography.headlineLarge,
            )

            Text(
                text = deviceInfo.address,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Column(
            modifier = modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { rcVm.sendMessage("ON") }) {
                Text(text = "LED ON")
            }

            Button(onClick = { rcVm.sendMessage("OFF") }) {
                Text(text = "LED OFF")
            }
        }

        Button(
            modifier = modifier.fillMaxWidth(),
            onClick = {
                rcVm.disconnect()
                onNavigateToHome()
            }
        ) {
            Text(text = "Disconnect")
        }
    }
}