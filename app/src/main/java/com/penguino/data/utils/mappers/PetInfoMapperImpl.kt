package com.penguino.data.utils.mappers

import com.penguino.data.local.models.RegistrationInfoEntity
import com.penguino.data.models.PetInformation
import com.penguino.data.repositories.registration.ImageStore
import javax.inject.Inject

class PetInfoMapperImpl @Inject constructor(
	private val imageStore: ImageStore
) : PetInfoMapper {
	override fun entityToDomain(entity: RegistrationInfoEntity): PetInformation {
		return PetInformation(
			address = entity.device.address,
			name = entity.petName,
			birthDay = entity.birthDate,
			pfp = entity.pfpPath?.let { imageStore.getImage(it) }
		)
	}
}