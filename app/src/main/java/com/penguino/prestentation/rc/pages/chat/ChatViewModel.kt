package com.penguino.prestentation.rc.pages.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguino.data.repositories.chat.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SYS_MSG = "You are a super helpful assistant. Your responses must be as short as " +
        "possible and don't break out of character."

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
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

    data class ChatUiState(
        val latestMessage: String = ""
    )
}
