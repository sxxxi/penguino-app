package com.penguino.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.penguino.data.local.models.RegistrationInfoEntity
import com.penguino.ui.components.SimpleTopBar
import com.penguino.ui.components.TextInput
import com.penguino.ui.components.TextInputWithSuggestion
import com.penguino.ui.theme.PenguinoTheme
import com.penguino.ui.viewmodels.RegistrationViewModel.RegistrationUiState

@Composable
fun RegistrationScreen(
	modifier: Modifier = Modifier,
	uiState: RegistrationUiState = RegistrationUiState(regInfo = RegistrationInfoEntity()),
	onInputChange: ((RegistrationInfoEntity) -> RegistrationInfoEntity) -> Unit = {},
	onRegInfoPost: () -> Unit = {},
	onNavigateToHome: () -> Unit = {},
	onBack: () -> Unit = {}
) {
	Column(
		modifier = modifier.fillMaxSize(),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		SimpleTopBar(title = "Enter info", onNavBack = onBack)
		TextFields(
			modifier = modifier
				.align(Alignment.CenterHorizontally),
			suggestions = uiState.suggestions,
			regInfo = uiState.regInfo,
			regInfoUpdate = onInputChange,
			onPostRequest = {
				onRegInfoPost()
				onNavigateToHome()
			}
		)
	}
}

@Composable
private fun TextFields(
	modifier: Modifier = Modifier,
	suggestions: List<String>,
	regInfo: RegistrationInfoEntity,
	regInfoUpdate: ((RegistrationInfoEntity) -> RegistrationInfoEntity) -> Unit,
	onPostRequest: () -> Unit = {}
) {
	Column(
		modifier = modifier.fillMaxSize(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		TextInputWithSuggestion(
			value = regInfo.petName,
			suggestions = suggestions,
			onValueChange = { name ->
				regInfoUpdate { regInfo ->
					regInfo.copy(petName = name)
				}
			}
		) { Text(text = "Name") }

		TextInput(
			value = regInfo.personality,
			onValueChange = { newVal ->
				regInfoUpdate { regInfo ->
					regInfo.copy(personality = newVal)
				}
			}
		) { Text(text = "Personality") }

		TextInput(
			value = regInfo.age.toString(),
			keyboardType = KeyboardType.Number,
			onValueChange = { newInt ->
				regInfoUpdate { regInfo ->
					newInt.toIntOrNull()?.let {
						regInfo.copy(age = it)
					} ?: regInfo
				}
			}
		) { Text(text = "Age") }

		Button(
			modifier = modifier.padding(vertical = 8.dp, horizontal = 16.dp),
			onClick = onPostRequest
		) {
			Text(text = "Register")
		}
	}
}

@Preview
@Composable
fun PreviewRegistrationScreen() {
	PenguinoTheme {
		Surface {
			RegistrationScreen()
		}
	}
}

