package com.penguino.ui.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.penguino.data.models.PetInfo
import com.penguino.ui.screens.PetInfoScreen
import com.penguino.ui.viewmodels.PetInfoViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

const val petInfoSelectedDeviceArg = "selectedDevice"

val adapter = Moshi.Builder()
	.add(KotlinJsonAdapterFactory())
	.build()
	.adapter(PetInfo::class.java)

internal data class PetInfoArgs(
	val selectedDevice: PetInfo?
){
	constructor(stateHandle: SavedStateHandle) : this(
		selectedDevice = adapter.fromJson(checkNotNull(stateHandle[petInfoSelectedDeviceArg]) as String)
	)
}

fun NavGraphBuilder.petInfo(
	onNavigateToHome: () -> Unit,
	onNavigateToRc: (PetInfo) -> Unit,
	onBackPressed: () -> Unit
) {
	composable(route = Screen.PetInfoScreen.routeWithArgs, arguments = Screen.PetInfoScreen.arguments) {
		val viewModel: PetInfoViewModel = hiltViewModel()
		val uiState by viewModel.uiState.collectAsStateWithLifecycle()
		PetInfoScreen(
			uiState = uiState,
			onDeleteClicked = viewModel::deleteRegInfo,
			onNavigateToHome = onNavigateToHome,
			onNavigateToRc = onNavigateToRc,
			onBackPressed = onBackPressed
		)
	}
}

fun NavController.navigateToPetInfo(registrationInfoEntity: PetInfo) {
	val adapter = Moshi.Builder()
		.add(KotlinJsonAdapterFactory())
		.build()
		.adapter(PetInfo::class.java)
	navigate("${Screen.PetInfoScreen.route}/${adapter.toJson(registrationInfoEntity)}") {
//		popUpTo(Screen.HomeScreen.route) {inclusive = true}
	}

}
