package com.penguino.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.penguino.R
import com.penguino.data.models.PetInfo
import com.penguino.utils.ObserveLifecycle
import com.penguino.ui.components.Mods
import com.penguino.ui.components.SimpleTopBar
import com.penguino.ui.theme.PenguinoTheme
import com.penguino.ui.viewmodels.HomeViewModel.HomeUiState
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomePage(
	modifier: Modifier = Modifier,
	uiState: HomeUiState = HomeUiState(),
	onScreenLaunched: (CoroutineScope) -> Unit = {},
	onScreenExit: () -> Unit = {},
	onSavedPetClicked: (PetInfo) -> Unit = {},
) {
	ObserveLifecycle(lifecycleOwner = LocalLifecycleOwner.current, observer = { owner, event ->
		when(event) {
			Lifecycle.Event.ON_RESUME -> onScreenLaunched(owner.lifecycleScope)
			else -> onScreenExit()
		}
	})

	Column(
		modifier = modifier
			.fillMaxSize(),
	) {
		SimpleTopBar(title = "Saved devices")
		val visibleState = remember {
			MutableTransitionState(false).apply {
				this.isIdle
				targetState = true
			}
		}
		AnimatedVisibility(
			visibleState = visibleState,
			enter = fadeIn(tween(1000)),
			exit = fadeOut()
		) {
			LazyColumn {
				items(uiState.savedDevices, key = { it.address }) { dev ->
					SavedDeviceListItem(
						modifier = Modifier.animateItemPlacement(),
						petName = dev.name,
						address = dev.address,
						onClick = { onSavedPetClicked(dev) },
						isNearby = dev.isNearby
					)
				}
			}
		}
	}
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
	isNearby: Boolean = false,
	onClick: () -> Unit = {}
) {
	Card(
		modifier = Mods.verticalListItem.then(modifier),
		onClick = onClick
	) {
		Row(
			modifier = Mods.verticalListItemContent,
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
		HomePage(uiState = HomeUiState(
			savedDevices = listOf(
				PetInfo(
					name = "Ketchup",
					personality = "Cute",
					age = 3,
					address = "I can smell your bones",
					isNearby = true
				)
			)
		)
		)
	}
}