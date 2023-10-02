package com.penguino.prestentation.sample

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SampleViewModel @Inject constructor() : ViewModel() {
    private var _uiState = MutableStateFlow(SampleUiState())
    val uiState: StateFlow<SampleUiState> = _uiState

    fun incrementMagicNum() {
        _uiState.update { uiStateCopy ->
            uiStateCopy.copy(magicNum = uiStateCopy.magicNum + 1)
        }
    }

    data class SampleUiState(
        val magicNum: Int = 0,
        val magivNum2: Int = 1
    )
}