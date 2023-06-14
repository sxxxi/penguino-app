package com.penguino.data.network

import com.penguino.data.local.models.RegistrationInfo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

private const val API = "api/pet"

interface RegistrationNetworkDataSource {
	@GET("$API/suggestNames/{size}")
	fun suggestNames(@Path("size") suggestionSize: Int): Call<List<String>>

	@POST(API)
	fun addDeviceInfo(@Body regInfo: RegistrationInfo): Call<String>
}