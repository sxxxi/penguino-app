package com.penguino.data.models

import com.penguino.data.utils.EpochFactory
import java.util.Date

data class PetInfo(
	val name: String = "",
	val birthDate: Long = EpochFactory.currentEpochSeconds(),
	val address: String = "",
	val isNearby: Boolean = false
)
