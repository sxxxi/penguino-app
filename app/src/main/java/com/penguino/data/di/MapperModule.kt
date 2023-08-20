package com.penguino.data.di

import com.penguino.data.utils.mappers.PetInfoMapper
import com.penguino.data.utils.mappers.PetInfoMapperImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MapperModule {
	@Provides
	fun petInfoMapper(impl: PetInfoMapperImpl): PetInfoMapper {
		return impl
	}
}