package com.penguino.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.penguino.data.local.models.RegistrationInfoEntity

private val defaultTextInfoModifier = Modifier
	.fillMaxWidth()
	.padding(vertical = 8.dp, horizontal = 16.dp)
	.border(BorderStroke(0.dp, Color.Transparent))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInput(
	modifier: Modifier = Modifier,
	value: String,
	keyboardType: KeyboardType = KeyboardType.Text,
	onValueChange: (String) -> Unit,
	label: @Composable () -> Unit,
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
