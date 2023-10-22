package com.penguino.data.repositories.chat

import android.util.Log
import com.penguino.data.network.ChatNetworkDataSource
import com.penguino.data.network.models.ChatRequest
import com.penguino.data.network.models.ChatResponse
import com.penguino.data.utils.FocusedList
import com.penguino.domain.models.ChatMessage
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatNetworkDataSource: ChatNetworkDataSource,
) : ChatRepository {
    private var systemMsg: ChatMessage? = null
    private val _history = FocusedList<ChatMessage>(20)
    override val history = _history.history
    private val _latestResponse = MutableStateFlow<ChatMessage?>(null)
    override val latestResponse: StateFlow<ChatMessage?> = _latestResponse
    private val chatCallback = object : Callback<ChatResponse> {
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
                Log.e(TAG, "Error code: ${response.code()}\nMessage: ${response.message()}")
            }
        }

        override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
            Log.e(TAG, t.message.toString())
        }
    }

    override fun setSystemMessage(message: String) {
        systemMsg = ChatMessage(
            role = ChatMessage.SYSTEM,
            content = message
        )
    }

    override fun chat(
        system: String?,
        message: String,
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

        _history.addEntry(
            listOf(
                ChatMessage(
                    content = message
                )
            )
        )
        messages += _history.getChunk()

        chatNetworkDataSource.sendMessage(
            ChatRequest(
                model = "gpt-3.5-turbo",
                messages = messages
            )
        ).enqueue(chatCallback)
    }

    override suspend fun asyncChat(message: String, system: String?): Deferred<String?> = GlobalScope.async {
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

        _history.addEntry(
            listOf(
                ChatMessage(
                    content = message
                )
            )
        )
        messages += _history.getChunk()
        return@async try {
            chatNetworkDataSource.sendMessage(
                ChatRequest(
                    model = "gpt-3.5-turbo",
                    messages = messages
                )
            ).awaitResponse().let {
                it.body()?.choices?.first()?.message?.content
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed sending request to OPENAI")
            null
        }
    }

    companion object {
        const val TAG = "OPENAI"
    }
}