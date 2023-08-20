package com.penguino.data.models.forms

import com.penguino.data.local.models.DeviceInfo
import com.penguino.data.models.Image
import com.penguino.data.utils.EpochFactory
import java.util.Date

data class PetRegistrationForm(
	val name: String = "",
	val birthDay: Long = EpochFactory.currentEpochSeconds(),
	val device: DeviceInfo = DeviceInfo(),
	val pfp: Image? = null
)
