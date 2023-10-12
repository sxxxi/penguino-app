/*
package com.penguino.tts

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*


interface ElevenLabsApiService {
    @POST("v1/voices/add")
    fun addVoice(
        @Header("xi-api-key") apiKey: String,
        @Part("name") name: String,
        @Part("labels") labels: String,
        @Part("description") description: String,
        @Part file1: MultipartBody.Part,
        @Part file2: MultipartBody.Part
    ): Call<YourResponseModel>  // Define a model class for the expected response

    @GET("v1/voices/settings/default")
    fun getDefaultVoiceSettings(
        @Header("Accept") accept: String
    ): Call<DefaultSettingsModel>  // Define a model class for the expected response

    // Define other API endpoints similarly
}
*/