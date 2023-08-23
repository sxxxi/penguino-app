package com.penguino.di

import com.penguino.data.network.ChatNetworkDataSource
import com.penguino.data.network.RegistrationNetworkDataSource
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {
	companion object {
		@Provides
		@Singleton
		fun retrofit(
			moshi: Moshi
		): Retrofit {
			return Retrofit.Builder()
				.baseUrl("http://192.168.50.153:8080/")
				.addConverterFactory(MoshiConverterFactory.create(moshi))
				.build()
		}

		@Provides
		@Singleton
		fun deviceApi(retrofit: Retrofit): RegistrationNetworkDataSource {
			return retrofit.create(RegistrationNetworkDataSource::class.java)
		}

		@Provides
		@Singleton
		fun openAiApi(
			moshi: Moshi
		): ChatNetworkDataSource {
			return Retrofit.Builder()
				.baseUrl("https://api.openai.com/v1/")
				.addConverterFactory(MoshiConverterFactory.create(moshi))
				.build()
				.create(ChatNetworkDataSource::class.java)
		}
	}
}