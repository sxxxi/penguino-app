package com.penguino.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.penguino.data.local.models.RegistrationInfoEntity
import com.penguino.models.PetInfo
import com.penguino.ui.screens.HomePage
import com.penguino.ui.viewmodels.HomeViewModel

fun NavGraphBuilder.homeScreen(
	onNavigateToScan: () -> Unit,
	onSavedPetClicked: (PetInfo) -> Unit
) {
	composable(Screen.HomeScreen.route) {
		HomePage(
			onNavigateToScan = onNavigateToScan,
			onSavedPetClicked = onSavedPetClicked,
		)
	}
}

fun NavController.navigateToHome() {
	navigate(Screen.HomeScreen.route)
}