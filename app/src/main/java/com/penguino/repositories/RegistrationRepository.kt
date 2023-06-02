package com.penguino.repositories

import com.penguino.models.DeviceInfo
import retrofit2.Callback
import com.penguino.models.RegistrationInfo
import kotlinx.coroutines.flow.Flow

interface RegistrationRepository {
	suspend fun getSavedDevices(): Flow<List<RegistrationInfo>>
	suspend fun saveDevice(
		device: RegistrationInfo,
		callback: Callback<String>
	)
	suspend fun forgetDevice(device: RegistrationInfo)

	fun deviceExists(address: String): Boolean

}