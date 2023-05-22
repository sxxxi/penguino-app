package com.penguino.constants

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.penguino.navigation.rcDeviceArg

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
}
