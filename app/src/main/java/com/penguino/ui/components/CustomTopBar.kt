package com.penguino.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CustomTopBar(
	modifier: Modifier = Modifier,
	navButton: @Composable () -> Unit = {},
	title: @Composable () -> Unit = {},
	headerButtons: @Composable RowScope.() -> Unit = {}
) {
	Row(modifier = modifier.fillMaxWidth()) {
		navButton()
		title()
		Row(
			modifier = Modifier.weight(1f),
			horizontalArrangement = Arrangement.End,
			content = headerButtons
		)
	}
}