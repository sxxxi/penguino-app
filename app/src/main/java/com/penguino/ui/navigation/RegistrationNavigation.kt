package com.penguino.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.penguino.data.local.models.RegistrationInfoEntity
import com.penguino.models.PetInfo
import com.penguino.ui.viewmodels.RegistrationViewModel
import com.penguino.ui.screens.RegistrationScreen

fun NavGraphBuilder.registrationScreen(
	onNavigateToRemoteControl: (PetInfo) -> Unit,
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