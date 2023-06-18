package com.penguino.data.local

import com.penguino.data.local.models.RegistrationInfoEntity

/**
 * A contract for reading and writing RegistrationInfo in the
 * SharedPreferences acting as a cache enabling viewModels to
 * share a single RegistrationInfo
 */
interface RegInfoCache {
	fun getRegInfo(): RegistrationInfoEntity?
	fun saveRegInfo(regInfo: RegistrationInfoEntity)
	fun clearRegInfo()
}