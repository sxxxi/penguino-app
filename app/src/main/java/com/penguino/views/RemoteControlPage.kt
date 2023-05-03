package com.penguino.views

import android.util.Log
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.penguino.viewmodels.BluetoothVM

@Composable
fun RemoteControl(
    modifier: Modifier = Modifier,
    btVM: BluetoothVM,
    onNavigateToHome: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when(event) {
                Lifecycle.Event.ON_CREATE -> {
                    Log.d("HELLO", btVM.selectedDevice.value.toString())
                    btVM.bindService()
                }

                Lifecycle.Event.ON_RESUME -> {
                    btVM.connect()
                }

                Lifecycle.Event.ON_DESTROY -> {
                    btVM.disconnect()
                    btVM.unbindService()
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
            val device by btVM.selectedDevice
            Text(
                text = device?.name ?: "",
                style = MaterialTheme.typography.headlineLarge,
            )

            Text(
                text = device?.address ?: "",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Column(
            modifier = modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { btVM.sendMessage("ON") }) {
                Text(text = "LED ON")
            }

            Button(onClick = { btVM.sendMessage("OFF") }) {
                Text(text = "LED OFF")
            }
        }

        Button(
            modifier = modifier.fillMaxWidth(),
            onClick = {
            btVM.disconnect()
            onNavigateToHome()
            }
        ) {
            Text(text = "Disconnect")
        }
    }
}