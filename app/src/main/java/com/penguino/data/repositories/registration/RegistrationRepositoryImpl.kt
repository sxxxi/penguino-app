package com.penguino.data.repositories.registration

import com.penguino.data.local.dao.DeviceDao
import com.penguino.data.local.models.RegistrationInfoEntity
import com.penguino.data.mappers.PetInfoMapper
import com.penguino.domain.forms.PetRegistrationForm
import com.penguino.domain.models.PetInformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Prioritize local database before managing remote storage
 */
class RegistrationRepositoryImpl @Inject constructor(
	private val registrationDao: DeviceDao,
	private val imageStore: ImageStore,
	private val petInfoMapper: PetInfoMapper
) : RegistrationRepository {
	override fun deviceExists(address: String): Boolean {
		return registrationDao.deviceExists(address)
	}

	override suspend fun save(reg: PetRegistrationForm) {
		withContext(Dispatchers.IO) {
			// init db entity
			var entity = RegistrationInfoEntity(
				device = reg.device,
				petName = reg.name,
				birthDate = reg.birthDay
			)

			// insert pfp and update pfpPath in entity
			reg.pfp?.let { img ->
				imageStore.saveImage(img)
				entity = entity.copy(pfpPath = img.filePath)
			}

			registrationDao.saveDevice(regInfo = entity)
		}
	}

	override suspend fun getById(id: String): PetInformation? = withContext(Dispatchers.IO) {
		return@withContext registrationDao.getById(id)?.let { found ->
			petInfoMapper.entityToDomain(found)
		}
	}

	override suspend fun delete(id: String) {
		withContext(Dispatchers.IO) {
			val entity = registrationDao.getById(id)

			// delete pfp first
			entity?.pfpPath?.let { pfp ->
				imageStore.deleteImage(pfp)
			}

			registrationDao.removeDeviceById(id)
		}
	}

	override suspend fun getSaved(): Flow<List<PetInformation>> {
		return registrationDao.getAll().map {
			it.map { entity ->
				petInfoMapper.entityToDomain(entity)
			}
		}
	}
}