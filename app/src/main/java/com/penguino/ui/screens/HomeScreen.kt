package com.penguino.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HomePage(
	modifier: Modifier = Modifier,
	onNavigateToScan: () -> Unit
) {
	Column(
		modifier = modifier
			.fillMaxSize(),
		verticalArrangement = Arrangement.Bottom
	) {
		Button(
			modifier = modifier.fillMaxWidth(),
			onClick = { onNavigateToScan() }
		) {
			Text(text = "Setup Product")
		}
	}

}