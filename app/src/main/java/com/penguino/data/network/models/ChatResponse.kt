package com.penguino.data.network.models

import com.google.gson.annotations.SerializedName
import com.penguino.data.models.ChatMessage

data class ChatResponse(
	val id: String,
	@SerializedName("object") val obj: String,
	val created: Long,
	val choices: List<ResponseChoice>,
	val usage: ResponseUsage
) {
	data class ResponseChoice(
		val index: Int,
		val message: ChatMessage,
		@SerializedName("finish_reason") val finishReason: String
	)

	data class ResponseUsage(
		@SerializedName("prompt_tokens") val promptTokens: Int,
		@SerializedName("completion_tokens") val completionTokens: Int,
		@SerializedName("total_tokens") val totalTokens: Int
	)

}