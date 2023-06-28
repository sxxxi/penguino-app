package com.penguino.data.network.models

import com.penguino.data.models.ChatMessage

// TODO: https://platform.openai.com/docs/api-reference/chat/create?lang=python

data class ChatRequest(
	val model: String,
	val messages: List<ChatMessage>,
//	val temperature: Double = 1.0,
//	@SerializedName("top_p") val topP: Double = 1.0,
//	val n: Int = 1,		// How many chat completion choices to generate for each input message.
)

