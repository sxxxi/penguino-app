package com.penguino.data.network.models

import com.penguino.data.models.ChatMessage
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatResponse(
	@field:Json(name="id") val id: String,
	@field:Json(name = "object") val obj: String,
	@field:Json(name="created") val created: Long,
	@field:Json(name="choices") val choices: List<ResponseChoice>,
	@field:Json(name="usage") val usage: ResponseUsage
) {
	@JsonClass(generateAdapter = true)
	data class ResponseChoice(
		@field:Json(name="index") val index: Int,
		@field:Json(name="message") val message: ChatMessage,
		@field:Json(name = "finish_reason") val finishReason: String
	)

	@JsonClass(generateAdapter = true)
	data class ResponseUsage(
		@field:Json(name = "prompt_tokens") val promptTokens: Int,
		@field:Json(name = "completion_tokens") val completionTokens: Int,
		@field:Json(name = "total_tokens") val totalTokens: Int
	)

}