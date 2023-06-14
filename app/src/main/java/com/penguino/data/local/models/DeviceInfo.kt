package com.penguino.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class DeviceInfo (
    @PrimaryKey val address: String = "",
    @ColumnInfo val deviceName: String = ""
): Comparable<DeviceInfo> {
    override fun compareTo(other: DeviceInfo): Int {
        return if (this.address == other.address) 0
        else 1
    }

}
