package com.penguino.data.models

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