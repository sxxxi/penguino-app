package com.penguino.data.network.models

import com.squareup.moshi.Json

data class ModelListResponse(
	@Json(name="data") val data: List<Model>,
	@Json(name="object") val obj: String,
)

data class Model(
	@Json(name="id") val id: String,
	@Json(name="object") val obj: String,
	@Json(name="owned_by") val owner: String,
//	@SerializedName("permission") val permission: List<String>
)
