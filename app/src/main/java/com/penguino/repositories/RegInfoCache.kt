package com.penguino.repositories

import com.penguino.models.RegistrationInfo

/**
 * A contract for reading and writing RegistrationInfo in the
 * SharedPreferences acting as a cache enabling viewModels to
 * share a single RegistrationInfo
 */
interface RegInfoCache {
	fun getRegInfo(): RegistrationInfo?
	fun saveRegInfo(regInfo: RegistrationInfo)
	fun clearRegInfo()
}