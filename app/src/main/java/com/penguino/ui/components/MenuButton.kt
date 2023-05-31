package com.penguino.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun MenuButton(
	tint: Color = MaterialTheme.colorScheme.onPrimary,
	expanded: Boolean = false,
	onClick: () -> Unit = {},
	onDismiss: () -> Unit = {},
	menuButtons: @Composable ColumnScope.() -> Unit = {}
) {
	Column {
		IconButton(onClick = onClick) {
			Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More", tint = tint)
		}
		DropdownMenu(expanded = expanded, onDismissRequest = onDismiss, content = menuButtons)
	}
}