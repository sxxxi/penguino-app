package com.penguino.bluetooth.models

data class DeviceInfo (
    val name: String,
    val address: String
): java.io.Serializable, Comparable<DeviceInfo> {
    override fun compareTo(other: DeviceInfo): Int {
        return if (this.address.equals(other.address)) 0
        else 1
    }

}
