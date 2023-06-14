package com.penguino.data.repositories.registration

import com.penguino.data.local.models.RegistrationInfo
import kotlinx.coroutines.flow.Flow
import retrofit2.Callback

interface RegistrationRepository {
	suspend fun getSavedDevices(): Flow<List<RegistrationInfo>>
	suspend fun saveDevice(
		device: RegistrationInfo,
		callback: Callback<String>
	)
	suspend fun forgetDevice(device: RegistrationInfo)

	fun deviceExists(address: String): Boolean

}