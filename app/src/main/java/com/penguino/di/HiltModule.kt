package com.penguino.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.google.gson.GsonBuilder
import com.penguino.repositories.chat.ChatApi
import com.penguino.repositories.registration.RegInfoCache
import com.penguino.repositories.registration.RegInfoCacheImpl
import com.penguino.repositories.bluetooth.BleRepository
import com.penguino.repositories.bluetooth.BleRepositoryImpl
import com.penguino.repositories.registration.RegistrationRepository
import com.penguino.repositories.registration.RegistrationRepositoryImpl
import com.penguino.repositories.registration.RegistrationService
import com.penguino.room.DeviceDatabase
import com.penguino.room.dao.DeviceDao
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HiltModule {
    @Provides
    @Singleton
    fun provideBluetoothAdapter(
        @ApplicationContext context: Context
    ): BluetoothAdapter {
        return context.getSystemService(BluetoothManager::class.java).adapter
    }

    @Provides
    @Singleton
    fun provideBluetoothManagement(
        @ApplicationContext context: Context,
        bluetoothAdapter: BluetoothAdapter): BleRepository {
        return BleRepositoryImpl(context, bluetoothAdapter)
    }

    @Provides
    @Singleton
    fun moshi(): Moshi {
        return Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    @Provides
    @Singleton
    fun sharedPreferences(@ApplicationContext ctx: Context): SharedPreferences {
        return ctx.getSharedPreferences("PENGUINO_PREFS", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun registrationInfoCache(
        moshi: Moshi,
        sharedPreferences: SharedPreferences
    ): RegInfoCache {
        return RegInfoCacheImpl(moshi = moshi, prefs = sharedPreferences)
    }

    @Provides
    @Singleton
    fun retrofit(): Retrofit  {
        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl("http://192.168.50.153:8080/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }


    @Provides
    @Singleton
    fun deviceDao(@ApplicationContext ctx: Context): DeviceDao {
        return Room
            .databaseBuilder(
                context = ctx,
                klass = DeviceDatabase::class.java,
                name = "SavedDevicesDatabase")
            .build()
            .dao()
    }

    @Provides
    @Singleton
    fun deviceApi(retrofit: Retrofit): RegistrationService {
        return retrofit.create(RegistrationService::class.java)
    }

    @Provides
    @Singleton
    fun deviceRepository(
        deviceApi: RegistrationService,
        deviceDao: DeviceDao
    ): RegistrationRepository {
        return RegistrationRepositoryImpl(
            registrationApi = deviceApi,
            registrationDao = deviceDao
        )
    }

    @Provides
    @Singleton
    fun openAiApi(): ChatApi {
        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl("https://api.openai.com/v1/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ChatApi::class.java)
    }
}

































