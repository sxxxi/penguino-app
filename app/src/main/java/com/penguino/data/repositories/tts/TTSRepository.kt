package com.penguino.data.repositories.tts

import android.net.Uri

interface TTSRepository {
    suspend fun tts(text: String, savePath: String): Uri
}