package com.penguino.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// TODO: https://platform.openai.com/docs/api-reference/chat/create?lang=python

data class ChatRequest(
	val model: String,
	val messages: List<ChatMessage>,
//	val temperature: Double = 1.0,
//	@SerializedName("top_p") val topP: Double = 1.0,
//	val n: Int = 1,		// How many chat completion choices to generate for each input message.
)

data class ChatMessage(
	val role: String = USER,
	val content: String,
) {
	companion object {
		const val USER = "user"
		const val SYSTEM = "system"
		const val ASSISTANT = "assistant"
	}
}

// This will become obsolete once I learn paging >:)
class ConversationHistory(window: Int = 5) {
	var windowSize: Int = window
		set(value) {
			field = value
			updatePtr()
		}
	private var ptr = -1;
	private var _history = MutableStateFlow<List<ChatMessage>>(listOf())
	val history: StateFlow<List<ChatMessage>> = _history	// Let's convert this to a state once we start integrating with the ui.

	private fun updatePtr() {
		ptr = if (history.value.isEmpty())
			-1
		else if (history.value.size < windowSize)
			0
		else history.value.size - windowSize
	}

	fun getChunk(): List<ChatMessage> {
		updatePtr()	// Make sure ptr is fresh and crispy
		return history.value.slice((ptr) until history.value.size)
	}

	fun addEntry(entry: List<ChatMessage>) {
		_history.value += entry
		updatePtr()
	}


}