package com.penguino.ui.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.penguino.data.local.models.RegistrationInfoEntity
import com.penguino.data.models.PetInfo
import com.penguino.ui.screens.HomePage
import com.penguino.ui.viewmodels.HomeViewModel

fun NavGraphBuilder.homeScreen(onSavedPetClicked: (PetInfo) -> Unit) {
	composable(Screen.HomeScreen.route) {
		val homeViewModel = hiltViewModel<HomeViewModel>()
		val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
		HomePage(
			uiState = uiState,
			onSavedPetClicked = onSavedPetClicked,
			onScreenLaunched = homeViewModel::onScreenLaunch,
			onScreenExit = homeViewModel::onScreenExit
		)
	}
}

fun NavController.navigateToHome() {
	navigate(Screen.HomeScreen.route)
}