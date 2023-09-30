package com.penguino.prestentation.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.StateFlow

@Composable
fun SampleScreen(
    uiState: SampleViewModel.SampleUiState,
    magicNumIncrementer: () -> Unit
) {
    var isVisible by remember {
        mutableStateOf(false)
    }
    Column(modifier = Modifier.fillMaxSize()) {
        if (isVisible) {
            Text("Hello")
        }

        Text(text = "Magic Number: ${uiState.magicNum}")
        Text(text = "Magic Number2: ${uiState.magivNum2}")

        Button(onClick = { isVisible = !isVisible }) {
            Text(text =
                if(isVisible) {
                    "Hide"
                } else "Show"
            )
        }
        Button(onClick = { magicNumIncrementer() }) {
            Text(text = "Increment magic num")
        }
    }
}