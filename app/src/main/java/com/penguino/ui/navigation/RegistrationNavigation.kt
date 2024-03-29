package com.penguino.ui.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.penguino.prestentation.registration.RegistrationScreen
import com.penguino.prestentation.registration.RegistrationViewModel

fun NavGraphBuilder.registrationScreen(
	onNavigateToHome: () -> Unit,
	onBackPressed: () -> Unit = {}
) {
	composable(Screen.RegistrationScreen.route) {
		val registrationViewModel = hiltViewModel<RegistrationViewModel>()
		val uiState by registrationViewModel.uiState.collectAsStateWithLifecycle()
		RegistrationScreen(
			uiState = uiState,
			onNavigateToHome = onNavigateToHome,
			onBack = onBackPressed,
			onInputChange = registrationViewModel::updateRegForm,
			onSubmit = registrationViewModel::onFormSubmit,
			onPfpChange = registrationViewModel::onPfpChange
		)
	}
}

fun NavController.navigateToRegistration() {
	navigate(Screen.RegistrationScreen.route)
}