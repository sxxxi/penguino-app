package com.penguino.data.models

import com.penguino.data.utils.EpochFactory

data class PetInformation(
	val address: String = "",
	val name: String = "",
	val birthDay: Long = EpochFactory.currentEpochSeconds(),
	val pfp: Image? = null
)
