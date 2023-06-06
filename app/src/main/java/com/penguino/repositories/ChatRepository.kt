package com.penguino.repositories

import android.util.Log
import com.penguino.chat.ChatApi
import com.penguino.chat.ChatMessage
import com.penguino.chat.ChatRequest
import com.penguino.chat.ChatResponse
import com.penguino.chat.ConversationHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ChatRepository @Inject constructor(
	private val chatApi: ChatApi,
) {
	private var systemMsg: ChatMessage? = null
	private val _history = ConversationHistory(20)
	val history = _history.history

	private val _latestResponse = MutableStateFlow<ChatMessage?>(null)
	val latestResponse: StateFlow<ChatMessage?> = _latestResponse

	private val chatCallback = object: Callback<ChatResponse> {
		val tag = "OPENAI"
		override fun onResponse(
			call: Call<ChatResponse>,
			response: Response<ChatResponse>
		) {
			if (response.isSuccessful) {
				response.body()?.let {
					Log.d(tag, response.body()?.choices?.first()?.message?.content ?: "Oopsies")
					response.body()?.choices?.first()?.message?.let { message ->
						_history.addEntry(listOf(message))
						_latestResponse.value = message
					}

				}
			} else {
				Log.d(tag, response.message())
				Log.d(tag, response.code().toString())
			}
		}

		override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
			Log.d(tag, t.message.toString())
		}
	}

	fun setSystemMessage(message: String) {
		systemMsg = ChatMessage(
			role = ChatMessage.SYSTEM,
			content = message
		)
	}

	fun chat(
		system: String? = null,
		message: String,
		callback: Callback<ChatResponse> = chatCallback
	) {
		val messages = mutableListOf<ChatMessage>()

		// update the system message if necessary
		system?.let {
			setSystemMessage(it)
		}

		// append the system message if not null
		// history must only contain back and forth between user and assistant
		systemMsg?.let {
			messages += it
		}

		_history.addEntry(listOf(
			ChatMessage(
				content = message
			)
		))
		messages += _history.getChunk()

		chatApi.sendMessage(
			ChatRequest(
				model = "gpt-3.5-turbo",
				messages = messages
			)
		).enqueue(callback)

		Log.d("OPENAI", messages.toString())
	}
}