package com.penguino.data.repositories.registration

import com.penguino.domain.forms.PetRegistrationForm
import com.penguino.domain.models.PetInformation
import kotlinx.coroutines.flow.Flow

interface RegistrationRepository {
	fun deviceExists(address: String): Boolean

	suspend fun save(reg: PetRegistrationForm)
	suspend fun getById(id: String): PetInformation?
	suspend fun delete(id: String)
	suspend fun getSaved(): Flow<List<PetInformation>>

}