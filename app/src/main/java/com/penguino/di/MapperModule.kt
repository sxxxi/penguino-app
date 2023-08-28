package com.penguino.di

import com.penguino.data.mappers.PetInfoMapper
import com.penguino.data.mappers.PetInfoMapperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MapperModule {
	@Binds
	abstract fun petInfoMapper(impl: PetInfoMapperImpl): PetInfoMapper
}