package com.penguino.data.network

import com.penguino.data.network.models.ChatRequest
import com.penguino.data.network.models.ChatResponse
import com.penguino.data.network.models.ModelListResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

private const val API_KEY = "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"

interface ChatNetworkDataSource {
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