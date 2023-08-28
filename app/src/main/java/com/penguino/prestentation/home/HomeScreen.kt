package com.penguino.prestentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.penguino.R
import com.penguino.data.utils.EpochFactory
import com.penguino.domain.models.PetInformation
import com.penguino.prestentation.home.HomeViewModel.HomeUiState
import com.penguino.prestentation.components.ConfirmationAlert
import com.penguino.prestentation.components.CustomCard
import com.penguino.prestentation.components.ListComponent
import com.penguino.prestentation.components.ListComponentHeader
import com.penguino.prestentation.components.PetPic
import com.penguino.ui.theme.PenguinoTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Month
import java.util.Calendar

// TODO: TOP BAR!!!
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
	uiState: HomeUiState = HomeUiState(),
	onNavigateToScan: () -> Unit = {},
	onPetDelete: (String) -> Unit = {},
	setViewablePet: (PetInformation?) -> Unit = {},
	onNavigateToRemoteControl: (String) -> Unit = {}
) {
	var profileVisibility by rememberSaveable {
		mutableStateOf(false)
	}
	val profileVisible = rememberSaveable(uiState.focusedPet) {
		uiState.focusedPet != null
	}
	val scope = rememberCoroutineScope()

	fun showProfile(pet: PetInformation?) {
		scope.launch {
			setViewablePet(pet)
			delay(100)
			profileVisibility = true
		}
	}

	fun hideProfile() {
		scope.launch {
			profileVisibility = false
			delay(100)
			setViewablePet(null)
		}
	}

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
				onPetClicked = { showProfile(it) }
			)
		}

		ProfileOverlay(
			visible = profileVisibility,
			onDismiss = { hideProfile() }
		) {
			uiState.focusedPet?.let { pet ->
				AnimatedVisibility(
					visible = profileVisibility,
					enter = scaleIn(),
					exit = scaleOut()
//					enter = slideInHorizontally(),
//					exit = slideOutHorizontally()
				) {
					ProfileCard(
						modifier = Modifier.background(Color.Black)
							.pointerInput(Unit) {
								detectTapGestures {
								}
							},
						selectedPet = pet,
						onDismiss = ::hideProfile,
						onPlayClicked = { onNavigateToRemoteControl(pet.address) }
					)
				}
			}
		}
	}
}

@Composable
fun ProfileCard(
	modifier: Modifier = Modifier,
	selectedPet: PetInformation,
	onDismiss: () -> Unit,
	onPlayClicked: () -> Unit = {},
	onChatClicked: () -> Unit = {}
) {
	Box(
		modifier = Modifier
			.fillMaxWidth(0.8f)
			.heightIn(min = 200.dp, max = 500.dp)
			.clip(RoundedCornerShape(15.dp))
			.then(modifier)
	) {
		Column(
			modifier = Modifier
				.background(MaterialTheme.colorScheme.background)
				.padding(16.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			Spacer(modifier = Modifier.height(16.dp))
			Row(
				horizontalArrangement = Arrangement.spacedBy(16.dp)
			) {
				PetPic(
					modifier = Modifier.size(88.dp),
					image = selectedPet.pfp
				)
				Column {
					Text(
						text = selectedPet.name,
						style = MaterialTheme.typography.headlineLarge +
								TextStyle(
									color = MaterialTheme.colorScheme.onBackground,
									fontWeight = FontWeight.Bold
								)
					)
					Text(
						text = "Dev: ${selectedPet.address}",
						style = MaterialTheme.typography.titleSmall +
								TextStyle(
									color = MaterialTheme.colorScheme.onBackground
								)
					)

					val cal = remember { EpochFactory.epochToCalendar(selectedPet.birthDay) }

					Text(
						text = "Added: ${cal.get(Calendar.MONTH)}/${cal.get(Calendar.MONTH)}/${
							cal.get(
								Calendar.YEAR
							)
						}",
						style = MaterialTheme.typography.titleSmall +
								TextStyle(
									color = MaterialTheme.colorScheme.onBackground
								)
					)
				}
			}

			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(top = 16.dp, bottom = 8.dp),
				horizontalArrangement = Arrangement.Center
			) {
				Button(onClick = {
					onPlayClicked()
					onDismiss()
				}) {
					Text(text = "Play")
				}
				Spacer(modifier = Modifier.width(16.dp))
				Button(onClick = {
					onChatClicked()
					onDismiss()
				}) {
					Text(text = "Chat")
				}
			}
		}
	}
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BuddyList(
	buddies: List<PetInformation>,
	onPetDelete: (String) -> Unit,
	onPetAdd: () -> Unit,
	onPetClicked: (PetInformation?) -> Unit,
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
			onClick = { onPetClicked(info) }
		)
	}
}

@Composable
private fun BuddyCard(
	modifier: Modifier = Modifier,
	buddy: PetInformation,
	onPetDelete: (String) -> Unit,
	onClick: () -> Unit,
) {
	val cornerRadius = remember {
		RoundedCornerShape(15.dp)
	}
	CustomCard(
		modifier = Modifier
			.then(modifier)
			.clip(cornerRadius),
		onClick = { onClick() }
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
				image = buddy.pfp,
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
					val calendar = remember { EpochFactory.epochToCalendar(buddy.birthDay) }
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
	buddy: PetInformation,
	onPetDelete: (String) -> Unit
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
					onPetDelete(buddy.address)
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
private fun ProfileOverlay(
	visible: Boolean,
	onDismiss: () -> Unit = {},
	dim: Float = 0.8f,
	content: @Composable () -> Unit = {}
) {
	Box {
		AnimatedVisibility(
			visible = visible,
			enter = fadeIn(tween(500)),
			exit = fadeOut(tween(500))
		) {
			Box(
				modifier = Modifier
					.fillMaxSize()
					.background(Color(0f, 0f, 0f, dim))
					.pointerInput(Unit) {
						detectTapGestures {
							onDismiss()
						}
					},
			)
		}
		Column(
			modifier = Modifier
				.fillMaxSize(),
//				.background(Color.Black)
//				.alpha(dim)
//				.pointerInput(Unit) {
//					detectTapGestures {
//						onDismiss()
//					}
//				},
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			content()
		}

	}
}


@Preview
@Composable
fun PreviewHomeScreen() {
	PenguinoTheme {
		HomePage(
			uiState = HomeUiState(
				savedDevices = listOf(
					PetInformation(
						name = "Ketchup",
						address = "I can smell your bones",
					),
					PetInformation(
						name = "Mayonaise",
						address = "I can smell your bones"
					)
				)
			)
		)
	}
}