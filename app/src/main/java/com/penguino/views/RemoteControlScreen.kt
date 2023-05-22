package com.penguino.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.penguino.models.DeviceInfo
import com.penguino.viewmodels.BluetoothViewModel

@Composable
fun RemoteControlScreen(
    modifier: Modifier = Modifier,
    deviceInfo: DeviceInfo,
    onNavigateToHome: () -> Unit
) {
//    val lifecycleOwner = LocalLifecycleOwner.current
//    DisposableEffect(key1 = lifecycleOwner) {
//        val observer = LifecycleEventObserver { _, event ->
//            when(event) {
//                Lifecycle.Event.ON_CREATE -> {
//                    btVM.bindService()
//                }
//
//                Lifecycle.Event.ON_RESUME -> {
//                    btVM.connect()
//                }
//
//                Lifecycle.Event.ON_DESTROY -> {
//                    btVM.disconnect()
//                    btVM.unbindService()
//                }
//
//                else -> {}
//            }
//        }
//
//        lifecycleOwner.lifecycle.addObserver(observer)
//
//        onDispose {
//            lifecycleOwner.lifecycle.removeObserver(observer)
//        }
//    }

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
            Button(onClick = { }) {
                Text(text = "LED ON")
            }

            Button(onClick = {  }) {
                Text(text = "LED OFF")
            }
        }

        Button(
            modifier = modifier.fillMaxWidth(),
            onClick = {
//            btVM.disconnect()
            onNavigateToHome()
            }
        ) {
            Text(text = "Disconnect")
        }
    }
}