package com.penguino.data.network.models

import com.penguino.data.models.ChatMessage
import com.squareup.moshi.Json

// TODO: https://platform.openai.com/docs/api-reference/chat/create?lang=python

data class ChatRequest(
	@field:Json(name="model") val model: String,
	@field:Json(name="messages") val messages: List<ChatMessage>,
)

