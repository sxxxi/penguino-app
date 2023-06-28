package com.penguino.ui.components

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopBar(
	title: String,
	elevation: Dp = 8.dp,
	bottomMargin: Dp = 8.dp,
	onNavBack: (() -> Unit)? = null,
	actions: @Composable RowScope.() -> Unit = {},
) {
	Column {
		TopAppBar(
			modifier = Modifier.shadow(elevation),
			title = {
				Text(text = title)
			},
			navigationIcon = {
				onNavBack?.let { onNavBack ->
					SimpleIconButton(
						iconVector = Icons.Default.ArrowBack,
						description = "Back",
						onClick = onNavBack
					)
				}
			},
			actions = actions,
		)
		Spacer(modifier = Modifier.height(bottomMargin))
	}
}

@Composable
fun SimpleIconButton(
	iconVector: ImageVector,
	description: String,
	enabled: Boolean = true,
	onClick: () -> Unit
) {
	IconButton(onClick = onClick, enabled = enabled) {
		Icon(imageVector = iconVector, contentDescription = description)
	}
}