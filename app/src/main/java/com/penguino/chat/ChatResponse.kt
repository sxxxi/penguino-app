package com.penguino.chat

import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.LocalDateTime

/*
{
  "id": "chatcmpl-123",
  "object": "chat.completion",
  "created": 1677652288,
  "choices": [{
    "index": 0,
    "message": {
      "role": "assistant",
      "content": "\n\nHello there, how may I assist you today?",
    },
    "finish_reason": "stop"
  }],
  "usage": {
    "prompt_tokens": 9,
    "completion_tokens": 12,
    "total_tokens": 21
  }
}
 */
data class ChatResponse(
	val id: String,
	@SerializedName("object") val obj: String,
	val created: Long,
	val choices: List<ResponseChoice>,
	val usage: ResponseUsage
)

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