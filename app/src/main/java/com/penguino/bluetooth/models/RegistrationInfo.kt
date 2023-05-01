package com.penguino.bluetooth.models

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationInfo (
    var name: String = "",
    var personality: String = "",
    var age: Int = 0,
    val device: DeviceInfo = DeviceInfo()
)