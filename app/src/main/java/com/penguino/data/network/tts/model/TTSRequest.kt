package com.penguino.data.network.tts.model

import com.squareup.moshi.Json

data class TTSRequest(
    @field:Json(name = "text") val text: String,
//    @field:Json(name = "voice_settings") val voiceSettings: VoiceSettings = VoiceSettings()
) {
    data class VoiceSettings(
        @field:Json(name = "similarity_boost") val similarityBoost: Float = 0.5f,
        @field:Json(name = "stability") val stability: Float = 0.5f,
        @field:Json(name = "style") val style: Int = 0,
        @field:Json(name = "use_speaker_boost") val useSpeakerBoost: Boolean = true
    )
}
