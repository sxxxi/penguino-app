package com.penguino.ui.screens

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.penguino.ui.navigation.Screen
import com.penguino.ui.navigation.homeScreen
import com.penguino.ui.navigation.navigateToHome
import com.penguino.ui.navigation.navigateToPetInfo
import com.penguino.ui.navigation.navigateToRegistration
import com.penguino.ui.navigation.navigateToRemoteControl
import com.penguino.ui.navigation.navigateToScan
import com.penguino.ui.navigation.petInfo
import com.penguino.ui.navigation.registrationScreen
import com.penguino.ui.navigation.remoteControlScreen
import com.penguino.ui.navigation.scanScreen
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

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
        // Ask permission on application launch
        multiplePermissionLauncher.launch(arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    Scaffold {
        NavHost(
            navController = navController,
            startDestination = Screen.HomeScreen.route,
            modifier = modifier.padding(it)
        ) {
            homeScreen(
                onSavedPetClicked = navController::navigateToPetInfo,
                onNavigateToScan = navController::navigateToScan
            )
            petInfo(
                onNavigateToHome = navController::navigateToHome,
                onNavigateToRc = navController::navigateToRemoteControl,
                onBackPressed = navController::popBackStack
            )
            scanScreen(
                onNavigateToRegistration = navController::navigateToRegistration,
                onNavigateToHome = navController::navigateToHome,
                onBackPressed = navController::popBackStack
            )
            registrationScreen(onNavigateToRemoteControl = { regInfo ->
                navController.navigateToRemoteControl(rcDevice = regInfo, popToHome = true)
            })
            remoteControlScreen(onNavigateToHome = navController::navigateToHome)
        }
    }
}