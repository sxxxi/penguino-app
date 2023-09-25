package com.penguino.prestentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp

@Composable
fun TextInputWithSuggestion(
	modifier: Modifier = Modifier,
	value: String,
	suggestions: List<String>,
	onValueChange: (String) -> Unit,
	label: (@Composable () -> Unit),
) {
	var suggestionsVisible by remember { mutableStateOf(false) }

	// Make suggestions visible when list is not empty, invisible otherwise
	LaunchedEffect(key1 = suggestions) {
		suggestionsVisible = suggestions.isNotEmpty()
	}

	Column {
		TextInput(modifier = modifier, value = value, onValueChange = onValueChange, label = label)
		AnimatedVisibility(visible = suggestionsVisible) {
			SuggestionList(suggestions = suggestions, onClickHandler = onValueChange)
		}
	}
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