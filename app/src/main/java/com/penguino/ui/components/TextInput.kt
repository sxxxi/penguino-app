package com.penguino.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.penguino.data.local.models.RegistrationInfoEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInput(
	modifier: Modifier = Modifier,
	value: String,
	updater: ((RegistrationInfoEntity) -> Unit) -> Unit,
	label: (@Composable () -> Unit),
	keyboardType: KeyboardType = KeyboardType.Text,
	combined: (newVal: String, updatable: RegistrationInfoEntity) -> Unit
) {
    var tem by remember { mutableStateOf(value) }
	TextField(
		modifier = modifier
			.fillMaxWidth()
			.padding(all = 4.dp),
		label = label,
		value = tem,
		keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
		onValueChange = { newVal ->
			tem = newVal
			updater {
				combined(tem, it)
			}
		}
	)
}