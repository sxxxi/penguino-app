package com.penguino.di

import com.penguino.data.repositories.bluetooth.DeviceDiscoveryRepository
import com.penguino.data.repositories.bluetooth.DeviceDiscoveryRepositoryImpl
import com.penguino.data.repositories.bluetooth.GattRepository
import com.penguino.data.repositories.bluetooth.GattRepositoryImpl
import com.penguino.data.repositories.chat.ChatRepository
import com.penguino.data.repositories.chat.ChatRepositoryImpl
import com.penguino.data.repositories.registration.RegistrationRepository
import com.penguino.data.repositories.registration.RegistrationRepositoryImpl
import com.penguino.data.repositories.tts.TTSRepository
import com.penguino.data.repositories.tts.TTSRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {
	@Binds
//	abstract fun bleRepository(repo: BleRepositoryImpl): BleRepository
	abstract fun bleRepository(repo: GattRepositoryImpl): GattRepository

	@Binds
	abstract fun deviceDiscoveryRepo(repo: DeviceDiscoveryRepositoryImpl): DeviceDiscoveryRepository
//	abstract fun deviceDiscoveryRepo(repo: LeRepository): DeviceDiscoveryRepository

	@Binds
	abstract fun chatRepo(repo: ChatRepositoryImpl): ChatRepository

	@Binds
	abstract fun registrationRepo(repo: RegistrationRepositoryImpl): RegistrationRepository

	@Binds
	abstract fun ttsRepo(repo: TTSRepositoryImpl): TTSRepository
}