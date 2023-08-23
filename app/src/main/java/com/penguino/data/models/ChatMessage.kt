package com.penguino.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatMessage(
	@field:Json(name="role") val role: String = USER,
	@field:Json(name="content") val content: String,
) {
	companion object {
		const val USER = "user"
		const val SYSTEM = "system"
		const val ASSISTANT = "assistant"
	}
}