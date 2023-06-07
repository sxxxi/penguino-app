package com.penguino.models.chat

import com.google.gson.annotations.SerializedName

data class ModelListResponse(
	@SerializedName("data") val data: List<Model>,
	@SerializedName("object") val obj: String,
)

data class Model(
	@SerializedName("id") val id: String,
	@SerializedName("object") val obj: String,
	@SerializedName("owned_by") val owner: String,
//	@SerializedName("permission") val permission: List<String>
)
