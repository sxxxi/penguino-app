package com.penguino.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.penguino.data.local.models.RegistrationInfoEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInputWithSuggestion(
	modifier: Modifier = Modifier,
	label: String,
	labelComposable: (@Composable () -> Unit) = { Text(text = label) },
	suggestions: List<String>,
	value: String,
	stateUpdater: ((updatable: RegistrationInfoEntity) -> Unit) -> Unit,
	sideEffect: (newValue: String, updatable: RegistrationInfoEntity) -> Unit
) {
    // Store a local state for the UI.
    var stateVal by remember { mutableStateOf(value) }

    /**
     * I couldn't update the shared mutable state from within
     * the suggestionList component therefore I decided to
     * create a value change handler shared by both components
     */
    fun onValueChange(newValue: String) {
        // Update local state (UI update)
        stateVal = newValue

        // Update ViewModel state
        stateUpdater { updatable ->
            /**
             * Inject/Expose the updatable to the sideEffect function.
             * Theoretically, we should be able to update the ViewModel state
             * by exposing a reference to it to lambda functions.
             *
             * Hope it works
             */
            sideEffect(newValue, updatable)

        }
    }

	TextField(
		modifier = modifier
			.fillMaxWidth()
			.padding(all = 4.dp),
		label = labelComposable,
		value = stateVal,
		onValueChange = ::onValueChange
	)

	SuggestionList(suggestions = suggestions, onClickHandler = ::onValueChange)
}

@Composable
private fun SuggestionList(
	modifier: Modifier = Modifier,
	suggestions: List<String>,
	onClickHandler: (String) -> Unit
) {
    val scrollState = rememberScrollState()
	Row(
		modifier = modifier
			.horizontalScroll(scrollState)
			.fillMaxWidth(),
		horizontalArrangement = Arrangement.End
	) {
		suggestions.forEach { s ->
			SuggestionItem(
				value = s,
				onClickHandler = onClickHandler
			)
		}
	}
}

@Composable
private fun SuggestionItem(
	modifier: Modifier = Modifier,
	value: String,
	onClickHandler: (String) -> Unit
) {
	Surface(
		modifier = modifier
			.shadow(2.dp)
			.padding(4.dp)
			.clickable {
				onClickHandler(value)
			}
	) {
		Text(
			modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp),
			text = value
		)
	}
}