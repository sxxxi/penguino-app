package com.penguino.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.penguino.constants.Screen
import com.penguino.viewmodels.ScanViewModel
import com.penguino.views.ScanScreen

fun NavGraphBuilder.scanScreen(
	onNavigateToHome: () -> Unit,
	onNavigateToRegistration: () -> Unit
) {
	composable(Screen.ScanScreen.route) {
		val scanVm: ScanViewModel = hiltViewModel()
		ScanScreen(
			uiState = scanVm.uiState,
			onDeviceSelected = scanVm::saveSelectedDevice,
			onScanButtonClicked = scanVm::scanDevices,
			onBackButtonClicked = scanVm::stopScan,
			onNavigateToHome = onNavigateToHome,
			onNavigateToRegistration = onNavigateToRegistration
		)
	}
}

fun NavController.navigateToScan() {
	navigate(Screen.ScanScreen.route)
}