package com.penguino.retrofit

import com.penguino.models.RegistrationInfo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

private const val API = "api/pet"

interface RegistrationService {
	@GET("$API/suggestNames/{size}")
	fun suggestNames(@Path("size") suggestionSize: Int): Call<List<String>>

	@POST(API)
	fun addPetInfo(@Body regInfo: RegistrationInfo): Call<String>
}