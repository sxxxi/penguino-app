package com.penguino.models

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationInfo (
    var name: String = "",
    var personality: String = "",
    var age: Int = 0,
    var device: DeviceInfo = DeviceInfo()
): java.io.Serializable