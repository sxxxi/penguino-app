package com.penguino.data.network.tts

import com.penguino.BuildConfig
import com.penguino.data.network.tts.model.TTSRequest
import com.penguino.data.network.tts.model.VoiceId
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

const val API_KEY = BuildConfig.ELEVENLABS_KEY

interface TextToSpeechNetworkDataSource {
    @Headers(
        "accept: audio/mpeg",
        "xi-api-key: $API_KEY",
        "Content-Type: application/json"
    )
    @POST("/v1/text-to-speech/{voiceId}")
    fun convertToSpeech(
        @Path("voiceId") voiceId: String = VoiceId.ANTONI.id,
        @Body data: TTSRequest
    ): Call<ResponseBody>
}