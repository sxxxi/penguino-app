package com.penguino.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.penguino.data.local.models.RegistrationInfoEntity
import com.penguino.models.PetInfo
import com.penguino.ui.components.TextInput
import com.penguino.ui.components.TextInputWithSuggestion
import com.penguino.ui.viewmodels.RegistrationViewModel
import com.penguino.ui.viewmodels.RegistrationViewModel.RegistrationUiState

private const val TAG = "RegistrationPage"

@Composable
fun RegistrationScreen(
	modifier: Modifier = Modifier,
	regVM: RegistrationViewModel,
	uiState: RegistrationUiState,
	onNavigateToRemoteControl: (PetInfo) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
    ) {
        Text(
            modifier = modifier.padding(horizontal = 12.dp, vertical = 32.dp),
            text = "Register",
            style = MaterialTheme.typography.headlineLarge,
        )

        TextFields(
            modifier = modifier
                .align(Alignment.CenterHorizontally),
            updater = regVM::updateRegInfo,
            suggestions = uiState.suggestions,
            regInfo = uiState.regInfo
        )

        Button(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            onClick = {
                regVM.postRegInfo()
                onNavigateToRemoteControl(uiState.regInfo.toModel())
            }) {
            Text(text = "Let's go!")
        }
    }
}

@Composable
private fun TextFields(
	modifier: Modifier = Modifier,
	suggestions: List<String>,
	regInfo: RegistrationInfoEntity,
	updater: ((RegistrationInfoEntity) -> Unit) -> Unit
) {
    Column (
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Get suggested names and store in a mutable state here :)
        TextInputWithSuggestion(
            value = regInfo.petName,
            stateUpdater = updater,
            label = "Name",
            suggestions = suggestions
        ) { newValue, updatable ->
            updatable.petName = newValue
        }

        TextInput(
            value = regInfo.personality,
            updater = updater, 
            label = { Text("Personality") }
        ) { newVal, updatable ->
            updatable.personality = newVal
        }

        TextInput(
            value = regInfo.age.toString(),
            updater = updater,
            keyboardType = KeyboardType.Number,
            label = { Text("Age") }
        ) { newVal, updatable ->
            updatable.age = if (newVal.isNotEmpty() && newVal.isDigitsOnly())
                newVal.toInt()
            else 0
        }
    }
}

