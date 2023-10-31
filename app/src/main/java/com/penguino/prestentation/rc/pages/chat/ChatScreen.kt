package com.penguino.prestentation.rc.pages.chat

import android.content.Intent
import android.net.Uri
import android.speech.RecognizerIntent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.penguino.R
import com.penguino.domain.forms.BtRequest
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    uiState: ChatViewModel.ChatUiState,
    chat: (String) -> Unit,
    chat2: (String, String, (Uri) -> Unit) -> Unit,
    intentionalPause: (Boolean) -> Unit,
    sendMessage: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val filesRoot = context.filesDir
    val player = remember { ExoPlayer.Builder(context).build() }

    val message = remember(uiState.latestMessage) {
        Log.d("CHANGE", "Changed!!")
        sendMessage(
            BtRequest(
                "displayText",
                uiState.latestMessage
            ).toString()
        )
        uiState.latestMessage
    }

    val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        .putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")

    val speechLaunch = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            it.data?.extras?.getStringArrayList(RecognizerIntent.EXTRA_RESULTS)?.first()
                ?.let { input ->
                    chat2(input, "$filesRoot/myMp3.mp3") { uri ->
                        Log.d("MSG", uiState.latestMessage)

                        player.apply {
                            setMediaItem(MediaItem.fromUri(uri))
                            prepare()
                            play()
                        }
                    }
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
            Text(text = message)
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