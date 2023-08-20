package com.penguino.data.repositories.registration

import com.penguino.data.models.PetInformation
import com.penguino.data.models.forms.PetRegistrationForm
import kotlinx.coroutines.flow.Flow

interface RegistrationRepository {
	fun deviceExists(address: String): Boolean

	suspend fun save(reg: PetRegistrationForm)
	suspend fun getById(id: String): PetInformation?
	suspend fun delete(id: String)
	suspend fun getSaved(): Flow<List<PetInformation>>

}