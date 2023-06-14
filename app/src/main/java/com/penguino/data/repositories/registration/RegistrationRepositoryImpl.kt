package com.penguino.data.repositories.registration

import com.penguino.data.local.models.RegistrationInfo
import com.penguino.data.local.dao.DeviceDao
import com.penguino.data.network.RegistrationNetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.Callback
import javax.inject.Inject

/**
 * Prioritizes local database before managing remote storage
 */
class RegistrationRepositoryImpl @Inject constructor(
	private val registrationDao: DeviceDao,
	private val registrationApi: RegistrationNetworkDataSource,
): RegistrationRepository {
	override suspend fun getSavedDevices(): Flow<List<RegistrationInfo>> =
		withContext(Dispatchers.IO) {
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