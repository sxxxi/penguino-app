package com.penguino.data.network.tts.model

import com.squareup.moshi.Json

/*
"detail": [
    {
      "loc": [],
      "msg": "string",
      "type": "string"
    }
  ]
 */
data class TTSError(
    @field:Json(name = "detail") val detail: Array<Inner>
) {
    data class Inner(
        @field:Json(name = "loc") val location: Array<Any>,
        @field:Json(name = "msg") val message: String,
        @field:Json(name = "type") val type: String
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Inner

            if (!location.contentEquals(other.location)) return false
            if (message != other.message) return false
            if (type != other.type) return false

            return true
        }

        override fun hashCode(): Int {
            var result = location.contentHashCode()
            result = 31 * result + message.hashCode()
            result = 31 * result + type.hashCode()
            return result
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TTSError

        if (!detail.contentEquals(other.detail)) return false

        return true
    }

    override fun hashCode(): Int {
        return detail.contentHashCode()
    }
}