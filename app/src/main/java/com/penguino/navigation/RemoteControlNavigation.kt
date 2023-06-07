package com.penguino.navigation

import android.util.Log
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.penguino.models.DeviceInfo
import com.penguino.models.RegistrationInfo
import com.penguino.viewmodels.RemoteControlViewModel
import com.penguino.ui.screens.RemoteControlScreen
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

const val rcDeviceArg = "rcDevice"
internal data class RemoteControlArgs(
	private val moshi: Moshi,
	private val savedStateHandle: SavedStateHandle,
) {
	private val adapter by lazy { moshi.adapter(RegistrationInfo::class.java) }
	val regInfo: RegistrationInfo? = adapter.fromJson(checkNotNull(savedStateHandle[rcDeviceArg]) as String)
}

fun NavGraphBuilder.remoteControlScreen(
	onNavigateToHome: () -> Unit
) {
	composable(
		route = Screen.RemoteControlScreen.routeWithArgs,
		arguments = Screen.RemoteControlScreen.arguments
	) {
		val rcVm = hiltViewModel<RemoteControlViewModel>()
		RemoteControlScreen(
			uiState = rcVm.uiState,
			onNavigateToHome = onNavigateToHome,
			chatFunc = rcVm::chat
		)
	}
}

fun NavController.navigateToRemoteControl(rcDevice: RegistrationInfo, popToHome: Boolean = false) {
	val adapter = Moshi.Builder()
		.add(KotlinJsonAdapterFactory())
		.build()
		.adapter(RegistrationInfo::class.java)
	Log.d("TEST", adapter.toJson(rcDevice).toString())
	val routeWithArgs = "${Screen.RemoteControlScreen.route}/${adapter.toJson(rcDevice)}"
	navigate(routeWithArgs) {
		if (popToHome) {
			popUpTo(Screen.HomeScreen.route)
		}
	}
}
