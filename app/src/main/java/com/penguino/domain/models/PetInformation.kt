package com.penguino.domain.models

import com.penguino.data.utils.EpochFactory

data class PetInformation(
	val address: String = "",
	val name: String = "",
	val birthDay: Long = EpochFactory.currentEpochSeconds(),
	val pfp: Image? = null
)
