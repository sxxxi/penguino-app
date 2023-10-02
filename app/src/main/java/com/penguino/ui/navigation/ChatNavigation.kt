package com.penguino.ui.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.penguino.prestentation.chat.ChatScreen
import com.penguino.prestentation.chat.ChatViewModel

fun NavGraphBuilder.chatScreen() {
    composable(route = Screen.ChatScreen.route) {
        val viewModel = hiltViewModel<ChatViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        ChatScreen(
            uiState = uiState,
            chat = viewModel::sendMessage
        )
    }
}

fun NavController.navigateToChatScreen() {
    navigate(Screen.ChatScreen.route)
}