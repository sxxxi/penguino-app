package com.penguino.data.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SerializerModule {
	@Provides
	@Singleton
	fun moshi(): Moshi {
		return Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
	}
}