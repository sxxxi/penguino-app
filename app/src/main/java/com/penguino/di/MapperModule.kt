package com.penguino.di

import com.penguino.data.utils.mappers.PetInfoMapper
import com.penguino.data.utils.mappers.PetInfoMapperImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MapperModule {
	@Binds
	abstract fun petInfoMapper(impl: PetInfoMapperImpl): PetInfoMapper
}