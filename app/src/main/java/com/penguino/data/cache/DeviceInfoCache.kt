package com.penguino.data.cache

import com.penguino.data.local.models.DeviceInfo

/**
 * A contract for reading and writing RegistrationInfo in the
 * SharedPreferences acting as a cache enabling viewModels to
 * share a single RegistrationInfo
 */
interface DeviceInfoCache {
	fun getSelectedDevice(): DeviceInfo?
	fun saveSelectedDevice(regInfo: DeviceInfo)
	fun clearSelectedDevice()
}