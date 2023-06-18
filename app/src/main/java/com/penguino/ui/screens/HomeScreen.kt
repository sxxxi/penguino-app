package com.penguino.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import com.penguino.R
import com.penguino.data.local.models.DeviceInfo
import com.penguino.data.local.models.RegistrationInfoEntity
import com.penguino.models.PetInfo
import com.penguino.ui.theme.PenguinoTheme
import com.penguino.ui.viewmodels.HomeViewModel
import com.penguino.ui.viewmodels.HomeViewModel.HomeUiState

val deviceListItemModifier = Modifier
	.padding(20.dp)
	.fillMaxWidth()

@Composable
fun HomePage(
	modifier: Modifier = Modifier,
	homeViewModel: HomeViewModel = hiltViewModel(),
	onSavedPetClicked: (PetInfo) -> Unit = {},
	onNavigateToScan: () -> Unit = {},
) {
	val uiState by homeViewModel.uiState.collectAsState()

	/**
	 * TODO: Modularize this later
	 */
	val lifecycleOwner = LocalLifecycleOwner.current
	DisposableEffect(key1 = lifecycleOwner) {
		val observer = LifecycleEventObserver { _, event ->
			when(event) {
				Lifecycle.Event.ON_RESUME -> homeViewModel.onScreenLaunch()
				else -> homeViewModel.onScreenExit()
			}
		}
		lifecycleOwner.lifecycle.addObserver(observer)
		onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
	}

	Column(
		modifier = modifier
			.fillMaxSize(),
	) {
		LazyColumn {
			items(uiState.savedDevices) { dev ->
				SavedDeviceListItem(
					modifier = deviceListItemModifier,
					petName = dev.name,
					address = dev.address,
					onClick = { onSavedPetClicked(dev) },
					isNearby = dev.isNearby
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

}

val defaultPfpImageMod = Modifier
	.width(60.dp)
	.clip(RoundedCornerShape(15.dp))

@Composable
fun SavedDeviceListItem(
	modifier: Modifier = Modifier,
	imageBitmap: ImageBitmap? = null,
	petName: String = "",
	address: String = "",
	isNearby: Boolean = false,
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
					fontSize = MaterialTheme.typography.titleSmall.fontSize
				)
			}
			Spacer(modifier = Modifier.weight(1f))
			Canvas(modifier = Modifier.width(8.dp)) {
				val clr = if (isNearby) Color.Green else Color.Gray
				drawCircle(
					color = clr,
					radius = 4.dp.toPx()
				)
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
		HomePage()
	}
}