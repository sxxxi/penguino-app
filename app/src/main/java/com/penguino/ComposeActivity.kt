package com.penguino

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.penguino.constants.Screen
import com.penguino.navigation.homeScreen
import com.penguino.navigation.navigateToHome
import com.penguino.navigation.navigateToRegistration
import com.penguino.navigation.navigateToRemoteControl
import com.penguino.navigation.navigateToScan
import com.penguino.navigation.registrationScreen
import com.penguino.navigation.remoteControlScreen
import com.penguino.navigation.scanScreen
import com.penguino.ui.theme.PenguinoTheme
import com.penguino.viewmodels.BluetoothViewModel
import com.penguino.views.RegistrationScreen
import com.penguino.views.RemoteControlScreen
import com.penguino.views.ScanScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ComposeActivity : ComponentActivity() {
    // A popup for granting access. This is temporary and I am looking for better alternatives.
    private val multiplePermissionLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { results ->
            results.forEach {
            }
        }

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

@Composable
fun BottomBar() {
    Row {
        Text("First")
        Text("Second")
        Text("Third")
    }
}

@Composable
fun requestPermissions(permissions: Array<String>, callback: (granted: List<String>) -> Unit) {
    rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        // Return list of allowed permissions
        val granted = mutableListOf<String>()
        permissions.forEach { p ->
            if (result.getValue(p)) granted.add(p)
        }
        callback(granted)
    }.launch(permissions)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    Scaffold {
        NavHost(
            navController = navController,
            startDestination = Screen.HomeScreen.route,
            modifier = modifier.padding(it)
        ) {
            homeScreen(onNavigateToScan = navController::navigateToScan)
            scanScreen(
                onNavigateToRegistration = navController::navigateToRegistration,
                onNavigateToHome = navController::navigateToHome
            )
            registrationScreen(onNavigateToRemoteControl = navController::navigateToRemoteControl)
            remoteControlScreen(onNavigateToHome = navController::navigateToHome)
        }
    }
}

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    onNavigateToScan: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
    ) {
        Button(
            modifier = modifier.fillMaxWidth(),
            onClick = { onNavigateToScan() }
        ) {
            Text(text = "Setup Product")
        }
    }

}


