package com.penguino

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.penguino.prestentation.MainScreen
import com.penguino.prestentation.rc.feed.FeedScreen
import com.penguino.ui.theme.PenguinoTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ComposeActivity : ComponentActivity() {
	// A popup for granting access. This is temporary and I am looking for better alternatives.
	private val multiplePermissionLauncher: ActivityResultLauncher<Array<String>> =
		registerForActivityResult(
			ActivityResultContracts.RequestMultiplePermissions()
		) { }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			PenguinoTheme {
				//MainScreen(Modifier.fillMaxSize())
				FeedScreen()
			}
		}

		// Ask permission on application launch
		multiplePermissionLauncher.launch(
			arrayOf(
				Manifest.permission.BLUETOOTH_CONNECT,
				Manifest.permission.BLUETOOTH_SCAN,
				Manifest.permission.ACCESS_FINE_LOCATION,
				Manifest.permission.ACCESS_COARSE_LOCATION,
				Manifest.permission.POST_NOTIFICATIONS
			)
		)
	}
}

