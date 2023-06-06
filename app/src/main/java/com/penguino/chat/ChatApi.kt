package com.penguino.chat

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