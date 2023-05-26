package com.penguino.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.penguino.ui.screens.HomePage
import com.penguino.constants.Screen

fun NavGraphBuilder.homeScreen(
	onNavigateToScan: () -> Unit
) {
	composable(Screen.HomeScreen.route) {
		HomePage(onNavigateToScan = onNavigateToScan)
	}
}

fun NavController.navigateToHome() {
	navigate(Screen.HomeScreen.route)
}