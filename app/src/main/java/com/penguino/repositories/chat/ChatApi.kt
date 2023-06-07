package com.penguino.repositories.chat

import com.penguino.models.chat.ChatRequest
import com.penguino.models.chat.ChatResponse
import com.penguino.models.chat.ModelListResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

private const val API_KEY = "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"

interface ChatApi {
	@GET("/v1/models")
	@Headers(
		"Content-Type: application/json",
		"Authorization: Bearer $API_KEY"
	)
	fun getModels(): Call<ModelListResponse>

	@POST("/v1/chat/completions")
	@Headers(
		"Content-Type: application/json",
		"Authorization: Bearer $API_KEY"
	)
	fun sendMessage(@Body request: ChatRequest): Call<ChatResponse>
}