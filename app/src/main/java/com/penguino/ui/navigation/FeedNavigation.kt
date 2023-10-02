package com.penguino.ui.navigation


import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.penguino.prestentation.rc.feed.FeedScreen

fun NavGraphBuilder.feedScreen() {
    composable(route = Screen.FeedScreen.route) {
        FeedScreen()
    }
}