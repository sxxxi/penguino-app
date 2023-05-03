package com.penguino

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.penguino.ui.theme.PenguinoTheme
import com.penguino.viewmodels.BluetoothVM
import com.penguino.viewmodels.RegistrationVM
import com.penguino.views.DeviceList
import com.penguino.views.RegistrationPage
import com.penguino.views.RemoteControl
import com.penguino.views.ScanPage
import dagger.hilt.android.AndroidEntryPoint

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
            val viewModelStoreOwner = LocalViewModelStoreOwner.current ?: remember {
                val viewModelStore = ViewModelStore()
                object : ViewModelStoreOwner {
                    override val viewModelStore: ViewModelStore
                        get() = viewModelStore
                }
            }
            PenguinoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(storeOwner = viewModelStoreOwner)
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
    storeOwner: ViewModelStoreOwner,
    navController: NavHostController = rememberNavController()
) {
    Scaffold {
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = modifier.padding(it)
        ) {
            composable("home") {
                HomePage(onNavigateToScan = { navController.navigate("scan") })
            }
            composable("scan") {
                ScanPage(
                    regVM = viewModel(viewModelStoreOwner = storeOwner),
                    btVM = hiltViewModel(viewModelStoreOwner = storeOwner),
                    onNavigateToRegistration = { navController.navigate("registration") },
                    onNavigateToHome = { navController.navigate("home") },
                )
            }
            composable("registration") {
                RegistrationPage(
                    regVM = viewModel(viewModelStoreOwner = storeOwner),
                    onNavigateToRemoteControl = { navController.navigate("rc") })
            }

            composable("rc") {
                RemoteControl(
                    btVM = hiltViewModel(viewModelStoreOwner = storeOwner),
                    onNavigateToHome = { navController.navigate("home")}
                )
            }
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
            modifier = modifier
                .fillMaxWidth(),
            onClick = { onNavigateToScan() }
        ) {
            Text(text = "Setup Product")
        }
    }

}