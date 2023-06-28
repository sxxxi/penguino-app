package com.penguino.data.repositories.registration

import com.penguino.data.local.models.RegistrationInfoEntity
import com.penguino.data.models.PetInfo
import kotlinx.coroutines.flow.Flow
import retrofit2.Callback

interface RegistrationRepository {
	suspend fun getSavedDevices(): Flow<List<PetInfo>>
	suspend fun saveDevice(
		device: RegistrationInfoEntity,
		callback: Callback<String>
	)
	suspend fun forgetDevice(device: PetInfo)

	fun deviceExists(address: String): Boolean

}