package com.penguino.ui.components

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.penguino.data.models.Image
import com.penguino.data.models.forms.PetRegistrationForm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun ImageCapture(
	registrationForm: PetRegistrationForm = PetRegistrationForm(),
	scope: CoroutineScope = rememberCoroutineScope(),
	pfp: Image? = null,
	onPfpChange: (Image?) -> Unit = {}
) {
	val context = LocalContext.current
	val dataDir = LocalContext.current.dataDir
	val cacheDir = LocalContext.current.cacheDir
	val filesDir = LocalContext.current.filesDir

	var tempImageCache by remember { mutableStateOf(Uri.EMPTY) }
	val launchCamera = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.TakePicture(),
		onResult = { success ->
			if (success) {
				tempImageCache?.path?.let { path ->
					val file = File(dataDir, path)
					val image = BitmapFactory.decodeFile(file.path).let { decoded ->
						val rotation = file.inputStream().use { iStream ->
							// Get image orientation in EXIF and return rotation amount
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
						val matrix = Matrix().apply { postRotate(rotation) }
						val bitmap = Bitmap.createBitmap(
							decoded, 0, 0, decoded.width, decoded.height,
							matrix, true
						)
						Image(
							filePath = "${filesDir}/${registrationForm.device.address}.jpg",
							bitmap = bitmap
						)
					}

					onPfpChange(image)
					file.delete()  // Delete temp file after getting the bitmap
				}
			}
		}
	)
	val cameraPermission = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.RequestPermission(),
		onResult = { grant ->
			if (grant) {
				scope.launch {
					withContext(Dispatchers.IO) {
						File.createTempFile("pfp_cache", ".jpg", cacheDir)
					}.let {
						tempImageCache = FileProvider.getUriForFile(
							context, "com.penguino.FileProvider", it
						)
						launchCamera.launch(tempImageCache)
					}
				}
			}
		}
	)

	Box(
		modifier = Modifier
			.size(78.dp)
			.clip(RoundedCornerShape(100))
			.clickable {
				cameraPermission.launch(Manifest.permission.CAMERA)
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
}