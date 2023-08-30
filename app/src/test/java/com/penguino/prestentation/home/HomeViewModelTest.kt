package com.penguino.prestentation.home

import com.penguino.data.local.models.DeviceInfo
import com.penguino.data.repositories.registration.RegistrationRepository
import com.penguino.data.repositories.registration.RegistrationRepositoryFake
import com.penguino.domain.forms.PetRegistrationForm
import com.penguino.domain.models.PetInformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

class HomeViewModelTest {

	private val scheduler = TestCoroutineScheduler()
	private val dispatcher = StandardTestDispatcher(scheduler)
	private lateinit var homeViewModel: HomeViewModel
	private lateinit var mockRegistrationRepo: RegistrationRepository

	@OptIn(ExperimentalCoroutinesApi::class)
	@BeforeEach
	fun setUp() = runTest {
		Dispatchers.setMain(dispatcher)
		mockRegistrationRepo = RegistrationRepositoryFake()

		// Create fake list item
		mockRegistrationRepo.save(
			PetRegistrationForm(
				name = "Ketchup",
				birthDay = Instant.MIN.epochSecond,
				device = DeviceInfo(address = newPetAddress)
			)
		)

		homeViewModel = HomeViewModel(mockRegistrationRepo)

		// Set focused pet
		val pet = PetInformation(address = newPetAddress)
		homeViewModel.setFocusedPet(pet)
		scheduler.advanceUntilIdle()        // run init
	}

	@Test
	fun `Tapped pet is set as focused`() {
		val pet = PetInformation()
		homeViewModel.setFocusedPet(pet)
		assertTrue(homeViewModel.uiState.value.focusedPet == pet)
	}

	@Test
	fun `Test focused pet unset and pet properly deleted`() = runTest(dispatcher) {
		val saved: List<PetInformation> = homeViewModel.uiState.value.savedDevices

		// Delete
		homeViewModel.forgetDevice(newPetAddress)
		scheduler.advanceUntilIdle()

		assert(!saved.any { it.address == newPetAddress })
		assert(homeViewModel.uiState.value.focusedPet == null)
	}

	companion object {
		const val newPetAddress = "UNIQUE"
	}
}