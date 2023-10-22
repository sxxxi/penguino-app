package com.penguino.data.repositories.tts

import android.net.Uri
import android.util.Log
import com.penguino.data.network.tts.TextToSpeechNetworkDataSource
import com.penguino.data.network.tts.model.TTSRequest
import com.squareup.moshi.Moshi
import retrofit2.awaitResponse
import java.io.File
import javax.inject.Inject

class TTSRepositoryImpl @Inject constructor(
    private val ttsNetSource: TextToSpeechNetworkDataSource,
    private val moshi: Moshi
) : TTSRepository {
    override suspend fun tts(text: String, savePath: String): Uri {
        try {
            val request = TTSRequest(text = text)

            ttsNetSource.convertToSpeech(data = request).awaitResponse().let { res ->
                if (res.isSuccessful) {
                    // Write bytes to temporary file
                    Log.d(TAG, "CREATING FILE")
                    val file = File(savePath).apply {
                        createNewFile()
                        outputStream().write(res.body()!!.bytes())
                    }
                    Log.d(TAG, "FILE CREATED")
                    return Uri.fromFile(file)
                }
                Log.e(TAG, "Response error: ${res.errorBody()!!.string()}")
                return Uri.EMPTY
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error converting text to speech: ${e.message}")
            return Uri.EMPTY
        }
    }

    companion object {
        const val TAG = "TTSRepositoryImpl"
    }
}