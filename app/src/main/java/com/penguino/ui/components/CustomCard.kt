package com.penguino.ui.components

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomCard(
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	onClick: () -> Unit = {},
	elevation: Dp = 4.dp,
	content: @Composable () -> Unit
) {
	Card(
		modifier = modifier,
		enabled = enabled,
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.surface,
		),
		elevation = CardDefaults.cardElevation(defaultElevation = elevation),
		onClick = onClick
	) {
		content()
	}
}