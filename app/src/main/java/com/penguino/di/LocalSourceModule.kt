package com.penguino.di

import android.content.Context
import androidx.room.Room
import com.penguino.data.cache.DeviceInfoCache
import com.penguino.data.cache.DeviceInfoCacheImpl
import com.penguino.data.local.DeviceDatabase
import com.penguino.data.local.dao.DeviceDao
import com.penguino.data.repositories.registration.ImageStore
import com.penguino.data.repositories.registration.ImageStoreImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalSourceModule {
	companion object {
		@Provides
		@Singleton
		fun deviceDao(@ApplicationContext ctx: Context): DeviceDao {
			return Room
				.databaseBuilder(
					context = ctx,
					klass = DeviceDatabase::class.java,
					name = "SavedDevicesDb"
				)
				.build()
				.dao()
		}

	}


	@Binds
	abstract fun registrationInfoCache(cache: DeviceInfoCacheImpl): DeviceInfoCache

	@Binds
	abstract fun profilePicStore(impl: ImageStoreImpl): ImageStore
}