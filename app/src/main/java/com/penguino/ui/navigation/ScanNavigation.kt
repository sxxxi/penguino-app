package com.penguino.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.penguino.prestentation.scan.ScanScreen
import com.penguino.prestentation.scan.ScanViewModel

fun NavGraphBuilder.scanScreen(
	onNavigateToRegistration: () -> Unit,
) {
	composable(Screen.ScanScreen.route) {
		val scanVm: ScanViewModel = hiltViewModel()
		val uiState = scanVm.uiState.collectAsStateWithLifecycle()
		ScanScreen(
			uiState = uiState.value,
			onDeviceSelected = scanVm::saveSelectedDevice,
			onScanButtonClicked = scanVm::scanDevices,
			onScanStop = scanVm::stopScan,
			onNavigateToRegistration = onNavigateToRegistration,
		)
	}
}

fun NavController.navigateToScan() {
	navigate(Screen.ScanScreen.route)
}