package com.penguino.ui.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.penguino.prestentation.home.HomePage
import com.penguino.prestentation.home.HomeViewModel

fun NavGraphBuilder.homeScreen(
//	onSavedPetClicked: (PetInfo) -> Unit,
	onNavigateToRemoteControl: (String) -> Unit,
	onPetAdd: () -> Unit
) {
	composable(Screen.HomeScreen.route) {
		val homeViewModel = hiltViewModel<HomeViewModel>()
		val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
		HomePage(
			uiState = uiState,
//			onSavedPetClicked = onSavedPetClicked,
			onNavigateToScan = onPetAdd,
			onPetDelete = homeViewModel::forgetDevice,
			setViewablePet = homeViewModel::setFocusedPet,
			onNavigateToRemoteControl = onNavigateToRemoteControl
		)
	}
}

fun NavController.navigateToHome() {
	navigate(Screen.HomeScreen.route) {
		popUpTo(Screen.HomeScreen.route) {inclusive = true}
	}
}