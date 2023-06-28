package com.penguino.data.repositories.chat

import com.penguino.data.models.ChatMessage
import com.penguino.data.network.models.ChatResponse
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Callback

interface ChatRepository {
	val history: StateFlow<List<ChatMessage>>
	val latestResponse: StateFlow<ChatMessage?>
	fun setSystemMessage(message: String)
	fun chat(
		system: String? = null,
		message: String,
	)
}