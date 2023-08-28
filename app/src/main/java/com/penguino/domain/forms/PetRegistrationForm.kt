package com.penguino.domain.forms

import com.penguino.data.local.models.DeviceInfo
import com.penguino.data.utils.EpochFactory
import com.penguino.domain.models.Image

data class PetRegistrationForm(
	val name: String = "",
	val birthDay: Long = EpochFactory.currentEpochSeconds(),
	val device: DeviceInfo = DeviceInfo(),
	val pfp: Image? = null
)
