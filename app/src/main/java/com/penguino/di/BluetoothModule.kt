package com.penguino.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.penguino.data.bluetooth.BleScanServiceImpl
import com.penguino.data.bluetooth.GattServiceManagerImpl
import com.penguino.data.bluetooth.contracts.BleScanService
import com.penguino.data.bluetooth.contracts.GattServiceManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BluetoothModule {
	companion object {
		@Provides
		@Singleton
		fun provideBluetoothAdapter(
			@ApplicationContext context: Context
		): BluetoothAdapter {
			return context.getSystemService(BluetoothManager::class.java).adapter
		}

	}

	@Binds
	abstract fun leServiceManager(impl: GattServiceManagerImpl): GattServiceManager

	@Binds
	abstract fun bleScan(impl: BleScanServiceImpl): BleScanService
}