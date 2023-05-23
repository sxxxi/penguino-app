package com.penguino.viewmodels.uistates

import com.penguino.models.RegistrationInfo

data class RegistrationUiState(
	val suggestions: List<String> = listOf(),
	val regInfo: RegistrationInfo
)
