/*
import com.penguino.tts.ElevenLabsApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ElevenLabsApiClient {
    private const val BASE_URL = "https://api.elevenlabs.io/"

    fun create(): ElevenLabsApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ElevenLabsApiService::class.java)
    }
}
*/