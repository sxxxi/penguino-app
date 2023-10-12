package com.penguino.prestentation.rc.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.penguino.prestentation.components.VerticalGrid
import com.penguino.prestentation.registration.RegistrationScreen
import com.penguino.ui.theme.PenguinoTheme

@Composable
fun FeaturesScreen(
    btMessageSend: (String) -> Unit,
    onNavigateToFeed: () -> Unit,
    onNavigateToChat: () -> Unit,
) {
    Column {
        Spacer(modifier = Modifier.weight(1f))
        ButtonGrid(
            onNavigateToFeed = onNavigateToFeed,
            onNavigateToChat = onNavigateToChat
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}


@Composable
private fun ButtonGrid(
    onNavigateToFeed: () -> Unit,
    onNavigateToChat: () -> Unit
) {
    val buttons = remember {
        listOf<@Composable () -> Unit> {
            GridButton(label = "Feed") { onNavigateToFeed() }
            GridButton(label = "Chat") { onNavigateToChat() }
        }
    }
    VerticalGrid(columns = 2, items = buttons)
}

@Composable
private fun GridButton(label: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .size(width = 160.dp, height = 50.dp),
        onClick = onClick
    ) {
        Text(text = label)
    }
}

@Preview
@Composable
fun FeaturesScreenPreview() {

    PenguinoTheme {
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FeaturesScreen(
                btMessageSend = { },
                onNavigateToFeed = { },
                onNavigateToChat = { }
            )
        }
    }

}
