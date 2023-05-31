package com.penguino.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun ConfirmationAlert(
	modifier: Modifier = Modifier,
	title: String? = null,
	text: String? = null,
	onConfirm: () -> Unit = {},
	onDismiss: () -> Unit = {}
) {
	AlertDialog(
		modifier = modifier,
		shape = RoundedCornerShape(5),
		title = {
			title?.let {
				Text(text = it)
			}
		},
		text = {
			text?.let {
				Text(text = it)
			}
		},
		onDismissRequest = onDismiss,
		confirmButton = {
			TextButton(onClick = onConfirm) {
				Text(text = "Yes")
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(text = "No")
			}
		}
	)
}

@Preview
@Composable
fun ConfirmationAlertPreview() {
	ConfirmationAlert(
		title = "Test",
		text = "南無阿弥陀仏"
	)
}