package com.penguino.data.repositories.chat

import com.penguino.domain.models.ChatMessage
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.StateFlow

interface ChatRepository {
	val history: StateFlow<List<ChatMessage>>
	val latestResponse: StateFlow<ChatMessage?>
	fun setSystemMessage(message: String)
	fun chat(
		system: String? = null,
		message: String,
	)

	suspend fun asyncChat(message: String, system: String? = null): Deferred<String?>
}