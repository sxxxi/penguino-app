package com.penguino.ui.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.penguino.prestentation.sample.SampleScreen
import com.penguino.prestentation.sample.SampleViewModel

fun NavGraphBuilder.sampleScreen() {
    composable(route = Screen.SampleScreen.route) {
        val vm = hiltViewModel<SampleViewModel>()
        val uiState by vm.uiState.collectAsStateWithLifecycle()
        SampleScreen(
            uiState = uiState,
            magicNumIncrementer = vm::incrementMagicNum
        )

    }
}