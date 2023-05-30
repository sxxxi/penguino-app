package com.penguino.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(
	val route: String,
	val routeWithArgs: String = route,
	val arguments: List<NamedNavArgument> = listOf()
) {
	object ScanScreen: Screen(route = "scan")
	object HomeScreen: Screen(route = "home")
	object RegistrationScreen: Screen(route = "register")
	object RemoteControlScreen: Screen(
		route = "rc",
		routeWithArgs = "rc/{$rcDeviceArg}",
		arguments = listOf(
			navArgument(rcDeviceArg) {type = NavType.StringType}
		)
	)
	object PetInfoScreen: Screen(
		route = "petInfo",
		routeWithArgs = "petInfo/{$petInfoSelectedDeviceArg}",
		arguments = listOf(
			navArgument(petInfoSelectedDeviceArg) {type = NavType.StringType}
		)
	)
}
