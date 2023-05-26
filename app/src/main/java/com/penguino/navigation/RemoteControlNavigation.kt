package com.penguino.navigation

import android.util.Log
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.penguino.constants.Screen
import com.penguino.models.DeviceInfo
import com.penguino.viewmodels.RemoteControlViewModel
import com.penguino.ui.screens.RemoteControlScreen
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

const val rcDeviceArg = "rcDevice"

internal class RemoteControlArgs(
	rcDeviceJson: String
) {
	private val adapter = Moshi.Builder()
		.add(KotlinJsonAdapterFactory())
		.build().adapter(DeviceInfo::class.java)
	val rcDevice: DeviceInfo = adapter.fromJson(rcDeviceJson)!!		// I promise you, this is not null.

	constructor(savedStateHandle: SavedStateHandle): this(
		rcDeviceJson = checkNotNull(savedStateHandle[rcDeviceArg]) as String
	) {
		Log.d("TESTING", checkNotNull(savedStateHandle[rcDeviceArg]))
	}
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
			rcVm = rcVm,
			uiState = rcVm.uiState,
			onNavigateToHome = onNavigateToHome
		)
	}
}

fun NavController.navigateToRemoteControl(rcDevice: DeviceInfo) {
	val adapter = Moshi.Builder()
		.add(KotlinJsonAdapterFactory())
		.build()
		.adapter(DeviceInfo::class.java)
	Log.d("TEST", adapter.toJson(rcDevice).toString())
	navigate("${Screen.RemoteControlScreen.route}/${adapter.toJson(rcDevice)}")
}