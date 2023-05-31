package com.penguino.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun TitledText(
	modifier: Modifier = Modifier,
	title: String = "",
	text: String = ""
) {
	Column(
		modifier = modifier,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			style = MaterialTheme.typography.labelSmall,
			text = title
		)
		Text(
			style = MaterialTheme.typography.titleLarge,
			text = text
		)
	}
}