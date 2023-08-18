package com.penguino.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import com.penguino.data.models.PetInfo
import com.penguino.data.utils.EpochFactory
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
@Entity(primaryKeys = ["address"])
data class RegistrationInfoEntity (
    @Embedded var device: DeviceInfo = DeviceInfo(),
    @ColumnInfo var petName: String = "",
    @ColumnInfo val birthDate: Long = EpochFactory.currentEpochSeconds()
) : ToModel<PetInfo> {
    override fun toModel(): PetInfo {
        return PetInfo(
            name = petName,
            birthDate = birthDate,
            address = device.address
        )
    }
}

