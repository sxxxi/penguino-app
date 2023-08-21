package com.penguino.ui.screens

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.penguino.data.local.models.RegistrationInfoEntity
import com.penguino.data.models.Image
import com.penguino.data.models.forms.PetRegistrationForm
import com.penguino.ui.components.TextInput
import com.penguino.ui.theme.PenguinoTheme
import com.penguino.ui.viewmodels.RegistrationViewModel.RegistrationUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File


@Composable
fun RegistrationScreen(
	uiState: RegistrationUiState = RegistrationUiState(regInfo = RegistrationInfoEntity()),
	onInputChange: ((PetRegistrationForm) -> PetRegistrationForm) -> Unit = {},
	onPfpChange: (Image?) -> Unit = {},
	onSubmit: () -> Unit = {},
	onNavigateToHome: () -> Unit = {},
	onBack: () -> Unit = {},
) {
	ScreenHolder(
		screens = listOf(
			{
				NamePrompt(
					name = uiState.regInfo.petName,
					onInputChange = { newName ->
						onInputChange { state ->
							state.copy(name = newName)
						}
					}
				)
			},
			{
				ImagePrompt(
					registrationForm = uiState.regForm,
					onPfpChange = onPfpChange
				)
			},
		),
		loadingScreen = {
			Text(text = "Loading")
		},
		confirmationScreen = {
			ConfirmationScreen()
		},
		onNavigateToHome = onNavigateToHome,
		onNavigateToPrevious = onBack,
		onSubmit = { onSubmit() },
	)
}

@Composable
private fun ProgressIndicator(
	modifier: Modifier = Modifier,
	containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
	indicatorColor: Color = MaterialTheme.colorScheme.primary,
	progress: Float = 0.5f
) {
	val barHeight = remember { 8.dp }
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.then(modifier)
			.clip(RoundedCornerShape(100))
	) {
		Box(modifier = Modifier
			.height(barHeight)
			.background(containerColor)
			.fillMaxWidth()
			.drawBehind {
				drawRoundRect(
					color = indicatorColor,
					cornerRadius = CornerRadius(100f, 100f),
					size = this.size.copy(width = size.width * progress)
				)
			}
		)
	}
}

@Composable
private fun ScreenHolder(
	screens: List<@Composable () -> Unit>,
	loadingScreen: @Composable () -> Unit = {},
	confirmationScreen: @Composable () -> Unit = {},
	coroutineScope: CoroutineScope = rememberCoroutineScope(),
	onSubmit: () -> Unit = {},
	onNavigateToPrevious: () -> Unit,
	onNavigateToHome: () -> Unit,
) {
	if (screens.isNotEmpty()) {
		var isSubmitted by remember { mutableStateOf(false) }
		var isLoading by remember { mutableStateOf(false) }
		var currentIndex by remember { mutableIntStateOf(0) }
		val progress: Float by animateFloatAsState(
			targetValue = ((currentIndex + 1).toFloat() / screens.size.toFloat()),
			label = "Progress animation",
			animationSpec = spring(
				dampingRatio = Spring.DampingRatioLowBouncy,
				stiffness = Spring.StiffnessLow
			)
		)
		val currentScreen = remember(currentIndex, isSubmitted) {
			if (isSubmitted) {
				if (isLoading) {
					loadingScreen
				} else {
					confirmationScreen
				}
			} else {
				screens[currentIndex]
			}
		}

		BackHandler(enabled = !isSubmitted) {
			if (currentIndex == 0 || (isSubmitted && !isLoading)) {
				onNavigateToPrevious()
			} else {
				currentIndex -= 1
			}
		}

		Column(
			modifier = Modifier.fillMaxSize(),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			if (!isSubmitted) {
				ProgressIndicator(
					modifier = Modifier.padding(16.dp),
					progress = progress
				)
			}
			Column(
				Modifier.weight(1f)
			) {
				AnimatedContent(
					targetState = currentScreen,
					label = "Current screen",
					transitionSpec = { (fadeIn() + slideInHorizontally()) togetherWith (fadeOut() + slideOutHorizontally()) }
				) { screen ->
					Column(
						modifier = Modifier
							.fillMaxSize()
							.padding(horizontal = 16.dp),
						verticalArrangement = Arrangement.Center,
						horizontalAlignment = Alignment.CenterHorizontally
					) {
						screen()
					}
				}
			}
			Row(
				modifier = Modifier
					.padding(horizontal = 16.dp)
					.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween
			) {
				if (!isSubmitted) {
					TextButton(onClick = {
						if (currentIndex == 0) {
							onNavigateToPrevious()
						} else {
							currentIndex -= 1
						}
					}) {
						Text(text = "Back")
					}

					Spacer(Modifier.weight(1f))

					if (progress == 1f) {
						TextButton(onClick = {
							coroutineScope.launch {
								isLoading = true
								isSubmitted = true
								onSubmit()
							}.invokeOnCompletion {
								isLoading = false
							}
						}) {
							Text(text = "Submit")
						}
					} else {
						TextButton(onClick = {
							currentIndex += 1
						}) {
							Text(text = "Next")
						}
					}
				} else {
					Spacer(Modifier.weight(1f))
					TextButton(onClick = {
						onNavigateToPrevious()
						onNavigateToHome()
					}) {
						Text(text = "Done")
					}
				}
			}
		}
	}
}

@Composable
private fun NamePrompt(
	name: String,
	onInputChange: (String) -> Unit
) {
	Column(
		Modifier.fillMaxSize()
	) {
		Spacer(modifier = Modifier.fillMaxHeight(0.5f))
		Text(
			text = "What is your new \nfriend's name?",
			style = MaterialTheme.typography.displaySmall + TextStyle(fontWeight = FontWeight.Bold)
		)
		Spacer(modifier = Modifier.height(8.dp))
		TextInput(
			value = name,
			label = null,
			onValueChange = onInputChange
		)
	}
}

@Composable
fun ImagePrompt(
	registrationForm: PetRegistrationForm = PetRegistrationForm(),
	onPfpChange: (Image?) -> Unit = {}
) {
	val pfp = registrationForm.pfp
	val context = LocalContext.current
	val cacheDir = LocalContext.current.cacheDir
	var tempImageCache by remember { mutableStateOf(Uri.EMPTY) }
	var permissionGranted by remember { mutableStateOf(false) }
	val cameraPermission = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.RequestPermission(),
		onResult = {
			permissionGranted = it
		}
	)
	val launchCamera = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.TakePicture(),
		onResult = { success ->
			if (success) {
				tempImageCache?.path?.let { path ->
					val file = File(context.dataDir, path)
					val bitmap = BitmapFactory.decodeFile(file.path).let { decoded ->
						// Fix image orientation
						val matrix = Matrix()
						val rotation = file.inputStream().use { iStream ->
							ExifInterface(iStream).getAttributeInt(
								ExifInterface.TAG_ORIENTATION,
								ExifInterface.ORIENTATION_UNDEFINED
							).let { orientation ->
								when (orientation) {
									ExifInterface.ORIENTATION_ROTATE_90 -> 90f
									ExifInterface.ORIENTATION_ROTATE_180 -> 180f
									ExifInterface.ORIENTATION_ROTATE_270 -> 270f
									else -> 0f
								}
							}
						}
						matrix.postRotate(rotation)
						Bitmap.createBitmap(
							decoded, 0, 0, decoded.width, decoded.height,
							matrix, true
						)
					}
					val img = Image(
						filePath = "${context.filesDir}/${registrationForm.device.address}.jpg",
						bitmap = bitmap
					)
					onPfpChange(img)

					// Delete temp file
					file.delete()
				}
			}
		}
	)

	Box(
		modifier = Modifier
			.size(78.dp)
			.clip(RoundedCornerShape(100))
			.clickable {
				if (!permissionGranted) {
					cameraPermission.launch(Manifest.permission.CAMERA)
				}
				if (permissionGranted) {
					File
						.createTempFile("pfp_cache", ".jpg", cacheDir)
						.apply {
							createNewFile()
						}
						.let {
							tempImageCache = FileProvider.getUriForFile(
								context, "com.penguino.FileProvider", it
							)
							launchCamera.launch(tempImageCache)
							it
						}
				}
			}
	) {
		Column(
			modifier = Modifier
				.background(Color.Gray)
				.fillMaxSize(),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			pfp?.let { image ->
				Image(
					modifier = Modifier.fillMaxSize(),
					bitmap = image.bitmap.asImageBitmap(),
					contentDescription = "preview",
					contentScale = ContentScale.Crop
				)
			} ?: Icon(imageVector = Icons.Default.Add, contentDescription = "add")
		}
	}


	Text(
		text = "Take a photo of your new friend!",
		style = MaterialTheme.typography.displaySmall + TextStyle(fontWeight = FontWeight.Bold)
	)
}

@Composable
private fun ConfirmationScreen() {
	Text(
		text = "You're all set!",
		style = MaterialTheme.typography.displaySmall + TextStyle(fontWeight = FontWeight.Bold)
	)
}

@Preview
@Composable
fun PreviewRegistrationScreen() {
	PenguinoTheme {
		Column(
			Modifier
				.fillMaxSize()
				.background(MaterialTheme.colorScheme.background),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			ImagePrompt(onPfpChange = {})
//			RegistrationScreen()
		}
	}
}

