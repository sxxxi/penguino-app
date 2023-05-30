package com.penguino.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import kotlinx.serialization.Serializable

@Serializable
@Entity(primaryKeys = ["address"])
data class RegistrationInfo (
    @Embedded var device: DeviceInfo = DeviceInfo(),
    @ColumnInfo var petName: String = "",
    @ColumnInfo var personality: String = "",
    @ColumnInfo var age: Int = 0,
)