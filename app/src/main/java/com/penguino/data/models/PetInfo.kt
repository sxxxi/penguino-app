package com.penguino.data.models

data class PetInfo(
	val name: String = "",
	val personality: String = "",
	val age: Int = 0,
	val address: String = "",
	val isNearby: Boolean = false
)
