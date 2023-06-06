package com.penguino.ui.screens

import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.RecognizerIntent.EXTRA_RESULTS
import android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
import android.speech.RecognizerResultsIntent.EXTRA_VOICE_SEARCH_RESULT_STRINGS
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.penguino.R
import com.penguino.models.DeviceInfo
import com.penguino.models.RegistrationInfo
import com.penguino.ui.theme.PenguinoTheme
import com.penguino.viewmodels.HomeViewModel
import com.penguino.viewmodels.HomeViewModel.HomeUiState
import kotlin.reflect.typeOf

val deviceListItemModifier = Modifier
	.padding(20.dp)
	.fillMaxWidth()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
	modifier: Modifier = Modifier,
	uiState: HomeUiState = HomeUiState(),
	onSavedPetClicked: (RegistrationInfo) -> Unit = {},
	onNavigateToScan: () -> Unit = {},
) {


//	Scaffold(topBar = {
//		TopAppBar(title = { Text(text = "Home") })
//	}) {
		Column(
			modifier = modifier
				.fillMaxSize(),
		) {
			LazyColumn {
				items(uiState.savedDevices) { dev ->
					SavedDeviceListItem(
						modifier = deviceListItemModifier,
						petName = dev.petName,
						address = dev.device.address,
						onClick = { onSavedPetClicked(dev) }
					)
				}
			}

			Row(
				modifier = Modifier
					.clickable(onClick = onNavigateToScan)
					.then(deviceListItemModifier),
				horizontalArrangement = Arrangement.Center
			) {
				Icon(imageVector = Icons.Filled.Add, contentDescription = "Add pet")
				Text(text = "Add pet")
			}
		}
//	}

}

val defaultPfpImageMod = Modifier
	.width(60.dp)
	.clip(RoundedCornerShape(15.dp))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedDeviceListItem(
	modifier: Modifier = Modifier,
	imageBitmap: ImageBitmap? = null,
	petName: String = "",
	address: String = "",
	onClick: () -> Unit = {}
) {
	Box(modifier = Modifier
		.clickable(onClick = onClick)
		.then(modifier)
	) {
		Row(
			modifier = modifier,
		) {
			PetPfp(
				imageBitmap = imageBitmap,
			)
			Spacer(modifier = Modifier.padding(horizontal = 8.dp))
			Column {
				Text(
					petName,
					fontWeight = FontWeight.Bold,
					fontSize = MaterialTheme.typography.titleMedium.fontSize
				)
				Text(
					text = address,
					fontSize = MaterialTheme.typography.titleSmall.fontSize)
			}
		}
	}
}

@Composable
fun PetPfp(
	modifier: Modifier = Modifier,
	imageBitmap: ImageBitmap? = null,
	fallbackPainter: Painter = painterResource(id = R.drawable.pet_img_default),
	contentScale: ContentScale = ContentScale.FillWidth
) {
	imageBitmap?.let {
		Image(
			modifier = modifier.then(defaultPfpImageMod),
			bitmap = it,
			contentScale = contentScale,
			contentDescription = "Pet pfp"
		)
	} ?: Image(
		modifier = modifier.then(defaultPfpImageMod),
		contentScale = contentScale,
		painter = fallbackPainter,
		contentDescription = "Default"
	)

}


@Preview
@Composable
fun PreviewHomeScreen() {
	PenguinoTheme {
		HomePage(uiState = HomeUiState(
			savedDevices = listOf(
				RegistrationInfo(
					petName = "Gravy",
					device = DeviceInfo(address = "xx:xx:xx:xx")
				),
				RegistrationInfo(
					petName = "Ketchup",
					device = DeviceInfo(address = "xx:xx:xx:xx")
				)
			)
		)
		)
	}
}