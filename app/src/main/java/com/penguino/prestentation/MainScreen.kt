package com.penguino.prestentation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.penguino.ui.navigation.Screen
import com.penguino.ui.navigation.homeScreen
import com.penguino.ui.navigation.navigateToHome
import com.penguino.ui.navigation.navigateToRegistration
import com.penguino.ui.navigation.navigateToRemoteControl
import com.penguino.ui.navigation.navigateToScan
import com.penguino.ui.navigation.registrationScreen
import com.penguino.ui.navigation.remoteControlScreen
import com.penguino.ui.navigation.scanScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
	modifier: Modifier = Modifier,
	navController: NavHostController = rememberNavController()
) {
	val screenTransitionDuration = remember { 500 }
	Scaffold {
		NavHost(
			navController = navController,
			startDestination = Screen.HomeScreen.route,
			modifier = modifier.padding(it),
			enterTransition = {
				slideInHorizontally(tween(screenTransitionDuration)) +
						fadeIn(tween(screenTransitionDuration))
			},
			exitTransition = {
				slideOutHorizontally(tween(screenTransitionDuration)) +
						fadeOut(tween(screenTransitionDuration))
			}
		) {
			homeScreen(
				onPetAdd = navController::navigateToScan,
				onNavigateToRemoteControl = navController::navigateToRemoteControl
			)
			scanScreen(onNavigateToRegistration = navController::navigateToRegistration)
			remoteControlScreen()
			registrationScreen(
				onNavigateToHome = navController::navigateToHome,
				onBackPressed = navController::popBackStack
			)
		}
	}
}