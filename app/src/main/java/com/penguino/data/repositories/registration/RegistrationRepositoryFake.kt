package com.penguino.data.repositories.registration

import com.penguino.domain.forms.PetRegistrationForm
import com.penguino.domain.models.PetInformation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class RegistrationRepositoryFake : RegistrationRepository {
	private val _saved = MutableStateFlow(mutableListOf<PetInformation>())

	override fun deviceExists(address: String): Boolean {
		return _saved.value.any { pet -> pet.address == address }
	}

	override suspend fun save(reg: PetRegistrationForm) {
		_saved.update { list ->
			list.add(
				PetInformation(
					address = reg.device.address,
					name = reg.name,
					birthDay = reg.birthDay,
					pfp = reg.pfp
				)
			)
			list
		}
	}

	override suspend fun getById(id: String): PetInformation {
		return _saved.value.first { pet -> pet.address == id }
	}

	override suspend fun delete(id: String) {
		_saved.update { list ->
			list.removeIf { it.address == id }
			list
		}
	}

	override suspend fun getSaved(): Flow<List<PetInformation>> = _saved

}