package com.penguino.models

import com.squareup.moshi.Json
import kotlinx.serialization.Serializable

@Serializable
data class RegistrationInfo (
    @field:Json(name = "name") var name: String = "",
    @field:Json(name = "personality") var personality: String = "",
    @field:Json(name = "age") var age: Int = 0,
    @field:Json(name = "deviceInfo") var device: DeviceInfo = DeviceInfo()
): java.io.Serializable