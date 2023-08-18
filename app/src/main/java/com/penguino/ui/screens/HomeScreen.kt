package com.penguino.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.penguino.R
import com.penguino.data.models.PetInfo
import com.penguino.data.utils.EpochFactory
import com.penguino.ui.components.ConfirmationAlert
import com.penguino.ui.components.CustomCard
import com.penguino.ui.components.ListComponent
import com.penguino.ui.components.ListComponentHeader
import com.penguino.ui.theme.PenguinoTheme
import com.penguino.ui.viewmodels.HomeViewModel.HomeUiState
import java.time.Month
import java.util.Calendar

// TODO: TOP BAR!!!
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
	uiState: HomeUiState = HomeUiState(),
	onSavedPetClicked: (PetInfo) -> Unit = {},
	onNavigateToScan: () -> Unit = {},
	onPetDelete: (PetInfo) -> Unit = {}
) {
	Scaffold(
		modifier = Modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.background),
	) { padding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(horizontal = 16.dp)
				.padding(padding),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			BuddyList(
				buddies = uiState.savedDevices,
				onPetDelete = onPetDelete,
				onPetAdd = onNavigateToScan,
				onPetClicked = onSavedPetClicked
			)
		}
	}
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BuddyList(
	buddies: List<PetInfo>,
	onPetDelete: (PetInfo) -> Unit,
	onPetAdd: () -> Unit,
	onPetClicked: (PetInfo) -> Unit,
) {
	ListComponentHeader(text = "Pets") {
		IconButton(onClick = onPetAdd) {
			Icon(
				modifier = Modifier.size(20.dp),
				imageVector = Icons.Default.Add,
				contentDescription = "Add buddy"
			)
		}
	}
	ListComponent(
		listItems = buddies,
		onListEmpty = {
			Text(
				modifier = Modifier.fillMaxWidth(),
				text = "No devices found",
				style = MaterialTheme.typography.titleLarge + TextStyle(
					fontWeight = FontWeight.Bold,
					color = MaterialTheme.colorScheme.surfaceVariant
				)
			)
		}
	) { info ->
		BuddyCard(
			modifier = Modifier
				.padding(4.dp)
				.animateItemPlacement(),
			buddy = info,
			onPetDelete = onPetDelete,
			onClick = onPetClicked
		)
	}
}

@Composable
private fun BuddyCard(
	modifier: Modifier = Modifier,
	buddy: PetInfo,
	onPetDelete: (PetInfo) -> Unit,
	onClick: (PetInfo) -> Unit,
) {
	val cornerRadius = remember {
		RoundedCornerShape(15.dp)
	}
	CustomCard(
		modifier = Modifier
			.then(modifier)
			.clip(cornerRadius),
		onClick = { onClick(buddy) }
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
		) {
			PetPic(
				modifier = Modifier
					.border(
						width = 2.dp,
						color = MaterialTheme.colorScheme.surfaceVariant,
						shape = RoundedCornerShape(100)
					)
					.size(76.dp),
				containerPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
			)

			Column(
				modifier = Modifier.padding(vertical = 16.dp)
			) {
				// Name
				Text(
					text = buddy.name,
					style = MaterialTheme.typography.headlineMedium +
							TextStyle(fontWeight = FontWeight.Bold)
				)

				// Birthday info
				Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
					val calendar = remember { EpochFactory.epochToCalendar(buddy.birthDate) }
					Icon(
						modifier = Modifier.size(16.dp),
						painter = painterResource(id = R.drawable.birthday_cake),
						contentDescription = "Cake"
					)
					Text(
						text = "${Month.of(calendar.get(Calendar.MONTH))} " +
								"${calendar.get(Calendar.DAY_OF_MONTH)}, ${calendar.get(Calendar.YEAR)}",
						style = MaterialTheme.typography.bodySmall
					)
				}
			}

			Spacer(modifier = Modifier.weight(1f))
			BuddyCardMenuButton(
				buddy = buddy,
				onPetDelete = onPetDelete
			)
		}
	}
}

@Composable
private fun BuddyCardMenuButton(
	buddy: PetInfo,
	onPetDelete: (PetInfo) -> Unit
) {
	Column {
		var menuExpanded by remember { mutableStateOf(false) }
		var deleteRequest by remember { mutableStateOf(false) }

		IconButton(onClick = { menuExpanded = true }) {
			Icon(imageVector = Icons.Default.MoreVert, contentDescription = "")
		}

		DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
			DropdownMenuItem(
				text = { Text(text = "Delete") },
				onClick = {
					menuExpanded = false
					deleteRequest = true
				})
		}

		if (deleteRequest) {
			ConfirmationAlert(
				modifier = Modifier.clip(RoundedCornerShape(5)),
				title = "Delete",
				text = "Are you sure you want to delete pet?",
				onConfirm = {
					onPetDelete(buddy)
					deleteRequest = !deleteRequest
				},
				onDismiss = {
					deleteRequest = !deleteRequest
				}
			)
		}
	}

}

@Composable
fun PetPic(
	modifier: Modifier = Modifier,
	contentScale: ContentScale = ContentScale.FillWidth,
	painter: Painter = painterResource(id = R.drawable.pet_img_default),
	containerPadding: PaddingValues = PaddingValues(0.dp)
) {
	Box(Modifier.padding(containerPadding)) {
		Image(
			modifier = Modifier
				.clip(RoundedCornerShape(100))
				.then(modifier),
			painter = painter,
			contentDescription = "Pet image",
			contentScale = contentScale
		)
	}
}


@Preview
@Composable
fun PreviewHomeScreen() {
	PenguinoTheme {
		HomePage(
			uiState = HomeUiState(
				savedDevices = listOf(
					PetInfo(
						name = "Ketchup",
						address = "I can smell your bones",
						isNearby = true
					),
					PetInfo(
						name = "Mayonaise",
						address = "I can smell your bones"
					)
				)
			)
		)
	}
}