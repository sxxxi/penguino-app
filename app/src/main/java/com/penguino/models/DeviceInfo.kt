package com.penguino.models

import com.squareup.moshi.Json
import kotlinx.serialization.Serializable

@Serializable
data class DeviceInfo (
    @field:Json(name = "name") val name: String = "",
    @field:Json(name = "address") val address: String = ""
): java.io.Serializable, Comparable<DeviceInfo> {
    override fun compareTo(other: DeviceInfo): Int {
        return if (this.address == other.address) 0
        else 1
    }

}
