package com.penguino

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.penguino.ui.theme.PenguinoTheme
import com.penguino.viewmodels.BluetoothVM
import com.penguino.views.DeviceList
import com.penguino.views.DeviceScan
import com.penguino.views.HomeScreen
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@AndroidEntryPoint
class ComposeActivity : ComponentActivity() {
    // A popup for granting access. This is temporary and I am looking for better alternatives.
    private val multiplePermissionLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { results ->
            results.forEach {
                Log.d("BOOBA", "${it.key}: ${it.value}")
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



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    btVM: BluetoothVM = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    Scaffold (
        bottomBar = { BottomBar() }
    ) {
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = modifier.padding(it)
        ) {
            composable("home") {
                HomeScreen(onNavigateToScan = { navController.navigate("scan") })
            }
            composable("scan") {
                btVM.scanDevices()
                DeviceScan(
                    onNavigateToDeviceList = { navController.navigate("deviceList") },
                    onNavigateToHome = { navController.navigate("home") },
                )
            }
            composable("deviceList") {
                DeviceList()
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
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