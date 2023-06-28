package com.penguino.data.repositories.registration

import android.util.Log
import com.penguino.data.local.models.RegistrationInfoEntity
import com.penguino.data.local.dao.DeviceDao
import com.penguino.data.network.RegistrationNetworkDataSource
import com.penguino.data.models.PetInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import retrofit2.Callback
import javax.inject.Inject

/**
 * Prioritize local database before managing remote storage
 */
class RegistrationRepositoryImpl @Inject constructor(
	private val registrationDao: DeviceDao,
	private val registrationApi: RegistrationNetworkDataSource,
): RegistrationRepository {
	override suspend fun getSavedDevices(): Flow<List<PetInfo>> =
		withContext(Dispatchers.IO) {
			registrationDao.getAll().mapLatest { li ->
				li.map { it.toModel() }
			}
		}

	override suspend fun saveDevice(
		device: RegistrationInfoEntity,
		callback: Callback<String>
	) = withContext(Dispatchers.IO) {
		registrationDao.saveDevice(device)
	}

	override suspend fun forgetDevice(device: PetInfo) = withContext(Dispatchers.IO) {
		registrationDao.removeDeviceById(device.address)
	}

	override fun deviceExists(address: String): Boolean {
		return registrationDao.deviceExists(address)
	}
}