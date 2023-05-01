package com.penguino.views

import android.inputmethodservice.Keyboard
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fasterxml.jackson.core.JsonEncoding
import com.fasterxml.jackson.core.io.JsonStringEncoder
import com.penguino.bluetooth.models.RegistrationInfo
import com.penguino.ui.theme.PenguinoTheme
import com.penguino.viewmodels.RegistrationVM
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonEncoder

private const val DTAG = "RegistrationPage"
@Composable
fun RegistrationPage(
    modifier: Modifier = Modifier,
    regVM: RegistrationVM = viewModel()
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
//        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            modifier = modifier.padding(horizontal = 12.dp, vertical = 32.dp),
            text = "Register",
            style = MaterialTheme.typography.headlineLarge,
        )

        TextFields(
            modifier = modifier
                .align(Alignment.CenterHorizontally),
            regInfo = regVM.regInfo.value,
            updater = regVM::updateRegInfo
        )

        Button(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            onClick = { Log.d(DTAG, Json.encodeToString(regVM.regInfo.value)) }) {
            Text(text = "Let's go!")
        }
    }
}

@Composable
fun TextFields(
    modifier: Modifier = Modifier,
    regInfo: RegistrationInfo,
    updater: ((RegistrationInfo) -> Unit) -> Unit
) {
    Column (
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        TextInputWithSuggestion(
            value = regInfo.name,
            updater = updater,
            label = { Text("Name") },
            suggestions = listOf("Foo", "Bar", "Baz")
        ) { newVal, reg ->
            Log.d(DTAG, newVal)
            reg.name = newVal
        }


        TextInput(
            value = regInfo.personality,
            updater = updater, 
            label = { Text("Personality") }
        ) { newVal, updatable ->
            updatable.personality = newVal
        }

        TextInput(
            value = regInfo.age.toString(),
            updater = updater,
            keyboardType = KeyboardType.Number,
            label = { Text("Age") }
        ) { newVal, updatable ->
            updatable.age = if (newVal.isNotEmpty() && newVal.isDigitsOnly())
                newVal.toInt()
            else 0
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInput(
    modifier: Modifier = Modifier,
    value: String,
    updater: ((RegistrationInfo) -> Unit) -> Unit,
    label: (@Composable () -> Unit),
    keyboardType: KeyboardType = KeyboardType.Text,
    combined: (newVal: String, updatable: RegistrationInfo) -> Unit
) {
    var tem by remember { mutableStateOf(value) }
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 4.dp),
        label = label,
        value = tem,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
        onValueChange = { newVal ->
            tem = newVal
            updater {
                combined(tem, it)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInputWithSuggestion(
    modifier: Modifier = Modifier,
    suggestions: List<String>,
    value: String,
    label: (@Composable () -> Unit),
    keyboardType: KeyboardType = KeyboardType.Text,
    updater: ((RegistrationInfo) -> Unit) -> Unit,
    combined: (newVal: String, updatable: RegistrationInfo) -> Unit
) {
    val tem = remember { mutableStateOf(value) }

    TextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 4.dp),
        label = label,
        value = tem.value,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
        onValueChange = { newVal ->
            tem.value = newVal
            updater {
                combined(tem.value, it)
            }
        }
    )

    SuggestionList(mutState = tem, suggestions = listOf("Foo", "Bar", "Baz"))
}

@Composable
fun SuggestionList(
    modifier: Modifier = Modifier,
    mutState: MutableState<String>,
    suggestions: List<String>,
) {
    Row() {
        suggestions.forEach { s ->
            SuggestionItem(mutState = mutState, value = s)
        }
    }
}

@Composable
fun SuggestionItem(
    modifier: Modifier = Modifier,
    mutState: MutableState<String>,
    value: String,
) {
    Surface(
        modifier = modifier.clickable {
            Log.d(DTAG, mutState.value)
            mutState.value = value
        }
    ) {
        Text(text = value)
    }
}

@Preview
@Composable
fun RegistrationPagePreview() {
    PenguinoTheme {
        Surface {
            TextFields(regInfo = RegistrationInfo(), updater = {})
        }

    }
}