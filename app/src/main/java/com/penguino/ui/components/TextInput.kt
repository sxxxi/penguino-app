package com.penguino.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType

private val defaultTextInfoModifier = Modifier
	.fillMaxWidth()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInput(
	modifier: Modifier = Modifier,
	value: String,
	keyboardType: KeyboardType = KeyboardType.Text,
	onValueChange: (String) -> Unit,
	label: (@Composable () -> Unit)?,
) {
	var tem by remember { mutableStateOf(value) }
	TextField(
		modifier = defaultTextInfoModifier.then(modifier),
		shape = RoundedCornerShape(100),
		label = label,
		value = tem,
		keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
		onValueChange = { newVal ->
			tem = newVal
			onValueChange(tem)
		},
		colors = TextFieldDefaults.textFieldColors(
			focusedIndicatorColor = Color.Transparent,
			unfocusedIndicatorColor = Color.Transparent
		)
	)
}
