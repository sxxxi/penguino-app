package com.penguino.prestentation.chat

import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.penguino.R

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    uiState: ChatViewModel.ChatUiState,
    chat: (String) -> Unit

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
            speechLaunch.launch(speechRecognizerIntent)
        }) {
            Icon(painter = painterResource(id = R.drawable.baseline_mic_24), contentDescription = "Mic")
        }
    }
}