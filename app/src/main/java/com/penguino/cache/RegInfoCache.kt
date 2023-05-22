package com.penguino.cache

import com.penguino.models.RegistrationInfo

interface RegInfoCache {
	fun getRegInfo(): RegistrationInfo?
	fun saveRegInfo(regInfo: RegistrationInfo)
	fun clearRegInfo()
}