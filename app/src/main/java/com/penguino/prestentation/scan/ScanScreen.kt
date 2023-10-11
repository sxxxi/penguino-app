package com.penguino.prestentation.scan

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.penguino.data.local.models.DeviceInfo
import com.penguino.data.utils.ObserveLifecycle
import com.penguino.prestentation.components.ListComponent
import com.penguino.prestentation.components.ListComponentHeader
import com.penguino.prestentation.scan.ScanViewModel.ScanUiState
import com.penguino.ui.theme.PenguinoTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
	uiState: ScanUiState = ScanUiState(),
	onDeviceSelected: (device: DeviceInfo) -> Boolean = { false },
	onScanButtonClicked: () -> Unit = {},
	onScanStop: () -> Unit = {},
	onNavigateToRegistration: () -> Unit = {},
) {
	val scope = rememberCoroutineScope()
	val snackHost = remember { SnackbarHostState() }
	val lifecycleOwner = LocalLifecycleOwner.current

	ObserveLifecycle(
		lifecycleOwner = lifecycleOwner,
		observer = { _, event ->
			when (event) {
				Lifecycle.Event.ON_START -> {
					onScanButtonClicked()
				}

				Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> {
					onScanStop()
				}

				else -> {}
			}
		}
	)

	LaunchedEffect(key1 = uiState.scanning) {
		Log.d("Scanning", "${uiState.scanning}")
	}

	LaunchedEffect(key1 = uiState.isError) {
		if (uiState.isError) {
			scope.launch {
				snackHost.showSnackbar(
					message = uiState.errorMessage,
					duration = uiState.errorDuration
				)
			}
		}
	}

	Scaffold(
		snackbarHost = {
			SnackbarHost(hostState = snackHost)
		}
	) { pad ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(pad),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			AnimatedContent(
				targetState = uiState.scanning, label = "",
				transitionSpec = { fadeIn(tween(500)) togetherWith fadeOut(tween(500)) }
			) { scanning ->
				if (scanning) {
					Text(
						text = "Scanning",
						style = MaterialTheme.typography.displaySmall +
								TextStyle(fontWeight = FontWeight.Bold)
					)
				} else {
					DevicesDiscovered(
						scope = scope,
						devicesFound = uiState.devicesFound,
						onRescan = onScanButtonClicked,
						onDeviceSelected = {
							scope.launch(Dispatchers.Default) {
								if (onDeviceSelected(it)) {
									launch(Dispatchers.Main) {
										onNavigateToRegistration()
									}
								}
							}
						}
					)
				}
			}
		}
	}
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DevicesDiscovered(
	scanning: Boolean = false,
	scope: CoroutineScope,
	devicesFound: List<DeviceInfo>,
	onDeviceSelected: (DeviceInfo) -> Unit,
	onRescan: () -> Unit
) {
	var listVisible by remember { mutableStateOf(false) }

	LaunchedEffect(key1 = scanning) {
		if (!scanning) {
			scope.launch {
				delay(800)
				listVisible = true
			}
		}
	}

	Column(
		modifier = Modifier
			.padding(16.dp),
	) {
		AnimatedVisibility(visible = listVisible) {
			ListComponentHeader(
				text = "Results",
				actions = {
					IconButton(
						enabled = !scanning,
						onClick = {
							scope.launch {
								listVisible = false
								delay(500)
								onRescan()
							}
						},
					) {
						Icon(imageVector = Icons.Default.Refresh, contentDescription = "Rescan")
					}
				}
			)
		}
		AnimatedVisibility(
			visible = listVisible,
			enter = slideInVertically(tween(500)) + fadeIn(tween(500)),
			exit = slideOutVertically(tween(500)) + fadeOut(tween(500))
		) {
			ListComponent(
				listItems = devicesFound,
				onListEmpty = {
					Text(
						text = "No devices found",
						style = MaterialTheme.typography.titleLarge + TextStyle(
							fontWeight = FontWeight.Bold,
							color = MaterialTheme.colorScheme.surfaceVariant
						)
					)
				}
			) { dev ->
				Surface(
					modifier = Modifier
						.animateItemPlacement()
						.padding(4.dp)
						.fillMaxWidth()
						.clip(RoundedCornerShape(15.dp)),
					onClick = { onDeviceSelected(dev) }
				) {
					Column(
						modifier = Modifier
							.padding(vertical = 24.dp, horizontal = 32.dp)
					) {
						Text(
							text = dev.deviceName,
							style = MaterialTheme.typography.titleLarge +
									TextStyle(fontWeight = FontWeight.Bold)
						)
						Text(text = dev.address)
					}
				}
			}
		}

	}
}

@Preview
@Composable
fun ScanPagePreview() {
	PenguinoTheme {
		Surface {
			ScanScreen(
				uiState = ScanUiState(
					scanning = false,
					devicesFound = listOf(
						DeviceInfo(
							deviceName = "Mayonaise",
							address = "I smell your bones"
						)
					)
				)
			)
		}
	}
}