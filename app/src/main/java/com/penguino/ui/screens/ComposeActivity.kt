package com.penguino.ui.screens

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.penguino.ui.navigation.Screen
import com.penguino.ui.navigation.homeScreen
import com.penguino.ui.navigation.navigateToHome
import com.penguino.ui.navigation.navigateToPetInfo
import com.penguino.ui.navigation.navigateToRegistration
import com.penguino.ui.navigation.navigateToRemoteControl
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
                MainScreen(Modifier.fillMaxSize())
            }
        }

        // Ask permission on application launch
        multiplePermissionLauncher.launch(arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS
        ))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    data class BottomNavButton(
        val screen: Screen,
        val icon: ImageVector,
        val label: String?
    )

    val bottomNavButtons = listOf(
        BottomNavButton(
            screen = Screen.HomeScreen,
            icon = Icons.Default.List,
            label = "Saved"
        ),
        BottomNavButton(
            screen = Screen.ScanScreen,
            icon = Icons.Default.Add,
            label = "New"
        )
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavButtons.forEach { button ->
                    NavigationBarItem(
                        icon = { Icon(imageVector = button.icon, null) },
                        label = {
                            if (button.label != null) {
                                Text(text = button.label)
                            }
                        },
                        selected = currentDestination?.hierarchy?.any { it.route == button.screen.route } == true,
                        onClick = {
                            navController.navigate(button.screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = true
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = false
                            }
                        }
                    )
                }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.HomeScreen.route,
            modifier = modifier.padding(it)
        ) {
            homeScreen(onSavedPetClicked = navController::navigateToPetInfo)
            scanScreen(onNavigateToRegistration = navController::navigateToRegistration)
            remoteControlScreen()
            petInfo(
                onNavigateToHome = navController::navigateToHome,
                onNavigateToRc = navController::navigateToRemoteControl,
                onBackPressed = navController::popBackStack
            )
            registrationScreen(
                onNavigateToHome = navController::navigateToHome
            )
        }
    }
}