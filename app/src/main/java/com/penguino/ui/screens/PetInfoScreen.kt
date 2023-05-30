package com.penguino.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.penguino.models.DeviceInfo
import com.penguino.models.RegistrationInfo
import com.penguino.ui.theme.PenguinoTheme
import com.penguino.viewmodels.PetInfoViewModel

private fun Modifier.popUpABit() = layout { measurable, constraints ->
	val children = measurable.measure(constraints = constraints)
	val topPad = children.height/2

	layout(children.width, children.height - topPad) {
		children.place(0, -topPad)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetInfoScreen(
	modifier: Modifier = Modifier,
	uiState: PetInfoViewModel.PetInfoUiState,
	onDeleteClicked: () -> Unit = {},
	onNavigateToHome: () -> Unit = {},
	onNavigateToRc: (RegistrationInfo) -> Unit = {}
) {
	val petInfo by remember {
		mutableStateOf(uiState.selectedDevice)
	}

	Column(
		modifier = modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.background)
	) {
		PetInfoHeader(
			image = null,
			petName = petInfo.petName,
			address = petInfo.device.address
		)
		Column(
			modifier = Modifier
				.weight(1f)
				.padding(8.dp)
		) {
			Text(text = "Personality: ${petInfo.personality}")
			Text(text = "Age: ${petInfo.age}")
		}
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(4.dp)
		) {
			// TODO: Ask if user really wants to delete item
			IconButton(onClick = {
				onDeleteClicked()
				onNavigateToHome()
			}) {
				Icon(
					imageVector = Icons.Rounded.Delete,
					contentDescription = "",
					tint = MaterialTheme.colorScheme.primary
				)
			}
			Button(
				modifier = Modifier.weight(1f),
				shape = RoundedCornerShape(10),
				onClick = { onNavigateToRc(petInfo) }
			) {
				Text("Let's play!")
			}
		}
	}
}

@Composable
fun PetInfoHeader(
	image: ImageBitmap? = null,
	petName: String = "",
	address: String = ""
) {
	Column(
		modifier = Modifier
			.background(MaterialTheme.colorScheme.primary)
	) {
		Spacer(modifier = Modifier.padding(10.dp))
		Row(
			modifier = Modifier
				.fillMaxWidth(),
			horizontalArrangement = Arrangement.Center
		) {
			PetPfp(
				modifier = Modifier
					.width(200.dp)
					.clip(RoundedCornerShape(100))
					.border(2.dp, MaterialTheme.colorScheme.onPrimary, RoundedCornerShape(100)),
				imageBitmap = image
			)
		}
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(20.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(
				text = petName,
				fontSize = MaterialTheme.typography.displayMedium.fontSize,
				fontWeight = FontWeight.Bold,
				color = MaterialTheme.colorScheme.onPrimary
			)
			Text(
				text = address,
				color = MaterialTheme.colorScheme.onPrimary
			)
		}
	}
}

@Preview
@Composable
fun PreviewPetInfoScreen() {
	PenguinoTheme {
		val uiState = PetInfoViewModel.PetInfoUiState(
			RegistrationInfo(
				device = DeviceInfo(
					"xx:xx:xx:xx",
					"Penguno"
				),
				petName = "Ketchup",
				personality = "Cute",
				age = 5
			)
		)
		PetInfoScreen(uiState = uiState)
	}
}