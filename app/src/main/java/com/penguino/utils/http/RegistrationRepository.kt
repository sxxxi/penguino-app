package com.penguino.utils.http

import com.penguino.bluetooth.models.RegistrationInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class RegistrationRepository {
    companion object {
        private const val BASE_URL = "http://192.168.50.153:8080"
        private val client = OkHttpClient()

        private fun fullUrl(path: String): String {
            return "$BASE_URL$path"
        }

        private fun queueRequest(req: Request, callback: Callback) {
            Thread {
                client.newCall(req).enqueue(callback)
            }.start()
        }

        fun getSuggestedNames(callback: Callback) {
            val request = Request.Builder()
                .get()
                .url(fullUrl("/api/pet/suggestNames/15"))
                .build()

            queueRequest(request, callback)
        }

        fun postRegistrationInfo(regInfo: RegistrationInfo, callback: Callback) {
            val request = Request.Builder()
                .post(body = Json
                    .encodeToJsonElement(regInfo)
                    .jsonObject
                    .toString()
                    .toRequestBody("application/json".toMediaTypeOrNull()))
                .url(fullUrl("/api/pet"))
                .build()

            queueRequest(request, callback)
        }
    }
}
