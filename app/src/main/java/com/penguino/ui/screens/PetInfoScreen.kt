package com.penguino.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.penguino.data.local.models.DeviceInfo
import com.penguino.data.local.models.RegistrationInfoEntity
import com.penguino.models.PetInfo
import com.penguino.ui.components.ConfirmationAlert
import com.penguino.ui.components.CustomTopBar
import com.penguino.ui.components.MenuButton
import com.penguino.ui.components.TitledText
import com.penguino.ui.theme.PenguinoTheme
import com.penguino.ui.viewmodels.PetInfoViewModel

@Composable
fun PetInfoScreen(
	modifier: Modifier = Modifier,
	uiState: PetInfoViewModel.PetInfoUiState,
	onDeleteClicked: () -> Unit = {},
	onNavigateToHome: () -> Unit = {},
	onNavigateToRc: (PetInfo) -> Unit = {},
	onBackPressed: (() -> Unit)? = {}
) {
	val petInfo by remember { mutableStateOf(uiState.selectedDevice) }
	var deleteRequest by remember { mutableStateOf(false) }

	Column(
		modifier = modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.background)
	) {
		PetInfoHeader(
//			image = null,
			petName = petInfo.name,
			onBackPressed = onBackPressed,
			headerButtons = {
				var expanded by remember {
					mutableStateOf(false)
				}
				MenuButton(
					expanded = expanded,
					onClick = { expanded = !expanded },
					onDismiss = { expanded = false },
					menuButtons = {
						DropdownMenuItem(
							text = { Text(text = "Edit") },
							onClick = {
								deleteRequest = true
								expanded = false
							}
						)
						DropdownMenuItem(
							text = { Text(text = "Delete") },
							onClick = {
								deleteRequest = true
								expanded = false
							}
						)
					}
				)
			}
		)

		PetInfoSection(
			modifier = Modifier
				.weight(1f)
				.fillMaxWidth(),
			petInfo = petInfo,
			onNavigateToRc = onNavigateToRc
		)

	}

	if (deleteRequest) {
		ConfirmationAlert(
			modifier = modifier.clip(RoundedCornerShape(5)),
			title = "Delete",
			text = "Are you sure you want to delete this profile?",
			onConfirm = {
				onDeleteClicked()
				onNavigateToHome()
				deleteRequest = !deleteRequest
			},
			onDismiss = {
				deleteRequest = !deleteRequest
			}
		)
	}
}

@Composable
private fun PetInfoHeader(
	image: ImageBitmap? = null,
	petName: String = "",
	headerButtons: @Composable RowScope.() -> Unit,
	onBackPressed: (() -> Unit)? = null
) {
	Column(
		modifier = Modifier
			.background(MaterialTheme.colorScheme.primary)
	) {
		CustomTopBar(
			navButton = {
				onBackPressed?.let {
					IconButton(onClick = it) {
						Icon(
							imageVector = Icons.Default.ArrowBack,
							contentDescription = "Back",
							tint = MaterialTheme.colorScheme.onPrimary
						)
					}
				}
			},
			headerButtons = headerButtons
		)
		Column(
			modifier = Modifier
				.padding(20.dp)
				.fillMaxWidth(),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			PetPfp(
				modifier = Modifier
					.width(200.dp)
					.clip(RoundedCornerShape(100))
					.border(2.dp, MaterialTheme.colorScheme.onPrimary, RoundedCornerShape(100)),
				imageBitmap = image
			)
			Spacer(modifier = Modifier.padding(10.dp))
			Text(
				text = petName,
				fontWeight = FontWeight.Bold,
				style = MaterialTheme.typography.displayLarge,
				color = MaterialTheme.colorScheme.onPrimary
			)
		}
	}

}


//private val InfoTextModifier = Modifier.fi
private val petInfoSectionTextModifier = Modifier.padding(vertical = 8.dp)

@Composable
private fun PetInfoSection(
	modifier: Modifier = Modifier,
	petInfo: PetInfo,
	onNavigateToRc: (PetInfo) -> Unit
) {
	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		TitledText(
			modifier = petInfoSectionTextModifier,
			title = "Address",
			text = petInfo.address
		)
		Row {
			TitledText(
				modifier = petInfoSectionTextModifier,
				title = "Personality",
				text = petInfo.personality
			)
			Spacer(modifier = Modifier.padding(4.dp))
			TitledText(
				modifier = petInfoSectionTextModifier,
				title = "Age",
				text = petInfo.age.toString()
			)

		}
	}
	Button(
		modifier = Modifier
			.fillMaxWidth()
			.padding(4.dp),
		shape = RoundedCornerShape(10),
		onClick = { onNavigateToRc(petInfo) }
	) {
		Text("Let's play!")
	}
}

@Preview
@Composable
fun PreviewPetInfoScreen() {
	PenguinoTheme {
		val uiState = PetInfoViewModel.PetInfoUiState(
			PetInfo(
				address = "xx:xx:xx:xx",
				name = "Ketchup",
				personality = "Cute",
				age = 5
			)
		)
		PetInfoScreen(uiState = uiState)
	}
}

