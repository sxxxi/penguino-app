package com.penguino.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.penguino.data.local.models.RegistrationInfo
import com.penguino.ui.screens.HomePage
import com.penguino.ui.viewmodels.HomeViewModel

fun NavGraphBuilder.homeScreen(
	onNavigateToScan: () -> Unit,
	onSavedPetClicked: (RegistrationInfo) -> Unit
) {
	composable(Screen.HomeScreen.route) {
		val homeViewModel: HomeViewModel = hiltViewModel()
//		homeViewModel.test() // TODO: REMOVE
		HomePage(
			uiState = homeViewModel.uiState,
			onNavigateToScan = onNavigateToScan,
			onSavedPetClicked = onSavedPetClicked,
		)
	}
}

fun NavController.navigateToHome() {
	navigate(Screen.HomeScreen.route)
}