package com.penguino.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.penguino.models.RegistrationInfo
import com.penguino.ui.screens.PetInfoScreen
import com.penguino.viewmodels.PetInfoViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

const val petInfoSelectedDeviceArg = "selectedDevice"

val adapter = Moshi.Builder()
	.add(KotlinJsonAdapterFactory())
	.build()
	.adapter(RegistrationInfo::class.java)

internal data class PetInfoArgs(
	val selectedDevice: RegistrationInfo?
){
	constructor(stateHandle: SavedStateHandle) : this(
		selectedDevice = adapter.fromJson(checkNotNull(stateHandle[petInfoSelectedDeviceArg]) as String)
	)
}

fun NavGraphBuilder.petInfo(
	onNavigateToHome: () -> Unit,
	onNavigateToRc: (RegistrationInfo) -> Unit
) {
	composable(route = Screen.PetInfoScreen.routeWithArgs, arguments = Screen.PetInfoScreen.arguments) {
		val viewModel: PetInfoViewModel = hiltViewModel()
		PetInfoScreen(
			uiState = viewModel.uiState,
			onDeleteClicked = viewModel::deleteRegInfo,
			onNavigateToHome = onNavigateToHome,
			onNavigateToRc = onNavigateToRc
		)
	}
}

fun NavController.navigateToPetInfo(registrationInfo: RegistrationInfo) {
	val adapter = Moshi.Builder()
		.add(KotlinJsonAdapterFactory())
		.build()
		.adapter(RegistrationInfo::class.java)
	navigate("${Screen.PetInfoScreen.route}/${adapter.toJson(registrationInfo)}")

}
