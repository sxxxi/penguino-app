package com.penguino.views

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.penguino.models.RegistrationInfo
import com.penguino.viewmodels.RegistrationViewModel

private const val TAG = "RegistrationPage"
@Composable
fun RegistrationPage(
    modifier: Modifier = Modifier,
    regVM: RegistrationViewModel,
    onNavigateToRemoteControl: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
    ) {
        Text(
            modifier = modifier.padding(horizontal = 12.dp, vertical = 32.dp),
            text = "Register",
            style = MaterialTheme.typography.headlineLarge,
        )

        TextFields(
            modifier = modifier
                .align(Alignment.CenterHorizontally),
            regVM = regVM,
            updater = regVM::updateRegInfo
        )

        Button(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            onClick = {
                Log.d(TAG, regVM.regInfo.value.device.toString())
                regVM.postRegInfo(
                    onSuccess = onNavigateToRemoteControl,
                    onFail = { /* TODO */ }
                )
            }) {
            Text(text = "Let's go!")
        }
    }
}

@Composable
fun TextFields(
    modifier: Modifier = Modifier,
    regVM: RegistrationViewModel,
    updater: ((RegistrationInfo) -> Unit) -> Unit
) {
    Column (
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Get suggested names and store in a mutable state here :)
        val suggestions = remember { regVM.getSuggestedNames() }
        val regInfo by remember { regVM.regInfo }

        TextInputWithSuggestion(
            value = regInfo.name,
            stateUpdater = updater,
            label = "Name",
            suggestions = suggestions
        ) { newValue, updatable ->
            updatable.name = newValue
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
    label: String,
    labelComposable: (@Composable () -> Unit) = { Text(text = label) },
    suggestions: List<String>,
    value: String,
    stateUpdater: ((updatable: RegistrationInfo) -> Unit) -> Unit,
    sideEffect: (newValue: String, updatable: RegistrationInfo) -> Unit
) {
    // Store a local state for the UI.
    var stateVal by remember { mutableStateOf(value) }

    /**
     * I couldn't update the shared mutable state from within
     * the suggestionList component therefore I decided to
     * create a value change handler shared by both components
     */
    fun onValueChange(newValue: String) {
        // Update local state (UI update)
        stateVal = newValue

        // Update ViewModel state
        stateUpdater { updatable ->
            /**
             * Inject/Expose the updatable to the sideEffect function.
             * Theoretically, we should be able to update the ViewModel state
             * by exposing a reference to it to lambda functions.
             *
             * Hope it works
             */
            sideEffect(newValue, updatable)

        }
    }

    TextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 4.dp),
        label = labelComposable,
        value = stateVal,
        onValueChange = ::onValueChange
    )

    SuggestionList(suggestions = suggestions, onClickHandler = ::onValueChange)
}

@Composable
fun SuggestionList(
    modifier: Modifier = Modifier,
    suggestions: List<String>,
    onClickHandler: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = modifier
            .horizontalScroll(scrollState)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        suggestions.forEach { s ->
            SuggestionItem(
                value = s,
                onClickHandler = onClickHandler
            )
        }
    }
}

@Composable
fun SuggestionItem(
    modifier: Modifier = Modifier,
    value: String,
    onClickHandler: (String) -> Unit
) {
    Surface(
        modifier = modifier
            .shadow(2.dp)
            .padding(4.dp)
            .clickable {
                onClickHandler(value)
            }
    ) {
        Text(
            modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            text = value
        )
    }
}