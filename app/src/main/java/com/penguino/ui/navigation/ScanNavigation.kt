package com.penguino.ui.navigation

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.penguino.ui.viewmodels.ScanViewModel
import com.penguino.ui.screens.ScanScreen

fun NavGraphBuilder.scanScreen(
	onNavigateToHome: () -> Unit = {},
	onNavigateToRegistration: () -> Unit = {},
	onBackPressed: () -> Unit = {}
) {
	composable(Screen.ScanScreen.route) {
		val scanVm: ScanViewModel = hiltViewModel()
		val uiState = scanVm.uiState.collectAsState()
		ScanScreen(
			uiState = uiState.value,
			onDeviceSelected = scanVm::saveSelectedDevice,
			onScanButtonClicked = scanVm::scanDevices,
			onNavigateToRegistration = onNavigateToRegistration,
			onBackPressed = onBackPressed
		)
	}
}

fun NavController.navigateToScan() {
	navigate(Screen.ScanScreen.route)
}