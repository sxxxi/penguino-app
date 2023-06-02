package com.penguino.repositories

import com.penguino.models.RegistrationInfo
import com.penguino.room.dao.DeviceDao
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import retrofit2.Callback
import javax.inject.Inject

/**
 * Prioritizes local database before managing remote storage
 */
class RegistrationRepositoryImpl @Inject constructor(
	private val registrationDao: DeviceDao,
	private val registrationApi: RegistrationService,
): RegistrationRepository {
	override suspend fun getSavedDevices(): Flow<List<RegistrationInfo>> = withContext(Dispatchers.IO) {
		registrationDao.getAll()
	}

	override suspend fun saveDevice(
		device: RegistrationInfo,
		callback: Callback<String>
	) = withContext(Dispatchers.IO) {
		registrationDao.saveDevice(device)
	}

	override suspend fun forgetDevice(device: RegistrationInfo) = withContext(Dispatchers.IO) {
		registrationDao.removeDevice(device)
	}

	override fun deviceExists(address: String): Boolean {
		return registrationDao.deviceExists(address)
	}

//	private fun postDevice(
//		device: RegistrationInfo,
//		callback: Callback<String>
//	) {
//		registrationApi.addDeviceInfo(device).enqueue(callback)
//	}
}