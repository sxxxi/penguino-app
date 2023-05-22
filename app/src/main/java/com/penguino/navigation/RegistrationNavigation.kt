package com.penguino.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.penguino.constants.Screen
import com.penguino.models.DeviceInfo
import com.penguino.viewmodels.RegistrationViewModel
import com.penguino.views.RegistrationScreen

fun NavGraphBuilder.registrationScreen(
	onNavigateToRemoteControl: (DeviceInfo) -> Unit
) {
	composable(Screen.RegistrationScreen.route) {
		val regVm = hiltViewModel<RegistrationViewModel>()
		RegistrationScreen(
			regVM = regVm,
			regInfo = regVm.regInfo,
			onNavigateToRemoteControl = onNavigateToRemoteControl
		)
	}
}

fun NavController.navigateToRegistration() {
	navigate(Screen.RegistrationScreen.route)
}