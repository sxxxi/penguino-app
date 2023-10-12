package com.penguino.prestentation.rc.pages.chat

import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.penguino.R
import com.penguino.prestentation.rc.pages.FeaturesScreen
import com.penguino.prestentation.rc.pages.FeedScreen
import com.penguino.ui.theme.PenguinoTheme
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    uiState: ChatViewModel.ChatUiState,
    chat: (String) -> Unit,
    intentionalPause: (Boolean) -> Unit
) {
    val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        .putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")

    val speechLaunch = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            it.data?.extras?.getStringArrayList(RecognizerIntent.EXTRA_RESULTS)?.first()
                ?.let { input ->
                    chat(input)
                }
        })

    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = uiState.latestMessage)
        }
        IconButton(onClick = {
            scope.launch {
                intentionalPause(true)
                speechLaunch.launch(speechRecognizerIntent)
            }

        }) {
            Icon(painter = painterResource(id = R.drawable.baseline_mic_24), contentDescription = "Mic")
        }
    }
}

@Preview
@Composable
fun ChatScreenPreview() {
    PenguinoTheme {
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ChatScreen(
                modifier = Modifier.fillMaxSize(),
                uiState = ChatViewModel.ChatUiState(),
                chat = {  },
                intentionalPause = {  }
            )
        }
    }
}
