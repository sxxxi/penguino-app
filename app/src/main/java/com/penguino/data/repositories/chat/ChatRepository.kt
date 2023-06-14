package com.penguino.data.repositories.chat

import android.util.Log
import com.penguino.data.network.ChatNetworkDataSource
import com.penguino.data.network.models.ChatMessage
import com.penguino.data.network.models.ChatRequest
import com.penguino.data.network.models.ChatResponse
import com.penguino.data.network.models.FocusedList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ChatRepository @Inject constructor(
	private val chatNetworkDataSource: ChatNetworkDataSource,
) {
	private var systemMsg: ChatMessage? = null
	private val _history = FocusedList<ChatMessage>(20)
	val history = _history.history

	private val _latestResponse = MutableStateFlow<ChatMessage?>(null)
	val latestResponse: StateFlow<ChatMessage?> = _latestResponse

	private val chatCallback = object: Callback<ChatResponse> {
		override fun onResponse(
			call: Call<ChatResponse>,
			response: Response<ChatResponse>
		) {
			if (response.isSuccessful) {
				response.body()?.choices?.first()?.message?.let { message ->
					Log.i(TAG, message.content)
					_history.addEntry(listOf(message))
					_latestResponse.value = message
				}
			} else {
				Log.e(TAG, "Error code: ${response.code()}\nMessage: ${response.errorBody()}")
			}
		}

		override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
			Log.e(TAG, t.message.toString())
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

		chatNetworkDataSource.sendMessage(
			ChatRequest(
				model = "gpt-3.5-turbo",
				messages = messages
			)
		).enqueue(callback)
	}

	companion object {
		const val TAG = "OPENAI"
	}
}