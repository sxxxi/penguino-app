package com.penguino.data.di

import com.penguino.data.repositories.bluetooth.BleRepository
import com.penguino.data.repositories.bluetooth.BleRepositoryImpl
import com.penguino.data.repositories.bluetooth.DeviceDiscoveryRepository
import com.penguino.data.repositories.bluetooth.DeviceDiscoveryRepositoryImpl
import com.penguino.data.repositories.chat.ChatRepository
import com.penguino.data.repositories.chat.ChatRepositoryImpl
import com.penguino.data.repositories.registration.RegistrationRepository
import com.penguino.data.repositories.registration.RegistrationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {
	@Binds
	abstract fun bleRepository(repo: BleRepositoryImpl): BleRepository

	@Binds
	abstract fun deviceDiscoveryRepo(repo: DeviceDiscoveryRepositoryImpl): DeviceDiscoveryRepository

	@Binds
	abstract fun chatRepo(repo: ChatRepositoryImpl): ChatRepository

	@Binds
	abstract fun registrationRepo(repo: RegistrationRepositoryImpl): RegistrationRepository
}