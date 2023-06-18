package com.penguino.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import com.penguino.models.PetInfo
import kotlinx.serialization.Serializable

@Serializable
@Entity(primaryKeys = ["address"])
data class RegistrationInfoEntity (
    @Embedded var device: DeviceInfo = DeviceInfo(),
    @ColumnInfo var petName: String = "",
    @ColumnInfo var personality: String = "",
    @ColumnInfo var age: Int = 0,
) : ToModel<PetInfo> {
    override fun toModel(): PetInfo {
        return PetInfo(
            name = petName,
            personality = personality,
            age = age,
            address = device.address
        )
    }
}

