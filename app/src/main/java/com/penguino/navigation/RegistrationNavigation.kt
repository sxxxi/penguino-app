package com.penguino.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.penguino.models.DeviceInfo
import com.penguino.models.RegistrationInfo
import com.penguino.viewmodels.RegistrationViewModel
import com.penguino.ui.screens.RegistrationScreen

fun NavGraphBuilder.registrationScreen(
	onNavigateToRemoteControl: (RegistrationInfo) -> Unit,
	onBackPressed: () -> Unit = {}
) {
	composable(Screen.RegistrationScreen.route) {
		val regVm = hiltViewModel<RegistrationViewModel>()
		RegistrationScreen(
			regVM = regVm,
			uiState = regVm.uiState,
			onNavigateToRemoteControl = onNavigateToRemoteControl
		)
	}
}

fun NavController.navigateToRegistration() {
	navigate(Screen.RegistrationScreen.route)
}