package com.penguino.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import com.penguino.data.utils.EpochFactory
import kotlinx.serialization.Serializable

@Serializable
@Entity(primaryKeys = ["address"])
data class RegistrationInfoEntity(
	@Embedded var device: DeviceInfo = DeviceInfo(),
	@ColumnInfo var pfpPath: String? = null,
	@ColumnInfo var petName: String = "",
	@ColumnInfo val birthDate: Long = EpochFactory.currentEpochSeconds()
)

