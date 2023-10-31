package com.penguino.prestentation.rc.pages.chat

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguino.data.repositories.chat.ChatRepository
import com.penguino.data.repositories.tts.TTSRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SYS_MSG = "You are a cute assistant and your response must be less than 30 characters."

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val ttsRepository: TTSRepository,
): ViewModel() {
    private var _uiState = MutableStateFlow(ChatUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            chatRepository.latestResponse.collectLatest { message ->
                message?.content?.let { content ->
                    _uiState.update { it.copy(latestMessage = content) }
                }
            }
        }
    }

    fun sendMessage(message: String) {
        chatRepository.chat(SYS_MSG, message)
    }

    fun tts(text: String, savePath:String, onResponse: (Uri) -> Unit) {
        viewModelScope.launch {
            chatRepository.asyncChat(text, SYS_MSG).await()?.let { response ->
                _uiState.update { it.copy(latestMessage = response) }
                onResponse(ttsRepository.tts(response, savePath))
            }
        }
    }

    data class ChatUiState(
        val latestMessage: String = ""
    )
}
