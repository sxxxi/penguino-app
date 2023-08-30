package com.penguino.data.mappers

import com.penguino.data.local.models.DeviceInfo
import com.penguino.data.local.models.RegistrationInfoEntity
import com.penguino.data.repositories.registration.ImageStore
import com.penguino.domain.models.Image
import com.penguino.domain.models.PetInformation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.Instant

class PetInfoMapperImplTest {

	private lateinit var petMapper: PetInfoMapper
	private lateinit var imageStore: ImageStore

	@BeforeEach
	fun setUp() {
		imageStore = Mockito.mock(ImageStore::class.java)
		Mockito.`when`(imageStore.getImage(pfp.filePath)).then { pfp }
		petMapper = PetInfoMapperImpl(imageStore)
	}

	@Test
	fun `Entity to domain mapping is correct`() {
		val expected = PetInformation(
			address = "EXPECTED",
			name = "Ketchup",
			birthDay = Instant.now().epochSecond,
			pfp = pfp
		)

		val actual = RegistrationInfoEntity(
			device = DeviceInfo(
				deviceName = "Doesn't matter",
				address = expected.address
			),
			pfpPath = expected.pfp?.filePath,
			petName = expected.name,
			birthDate = expected.birthDay
		).let {
			petMapper.entityToDomain(it)
		}

		assert(expected == actual)
	}

	companion object {
		private val pfp = Image(
			filePath = "/path/to/img",
			bitmap = null
		)

	}
}