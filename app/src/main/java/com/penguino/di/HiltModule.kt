package com.penguino.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.penguino.cache.RegInfoCache
import com.penguino.cache.RegInfoCacheImpl
import com.penguino.repositories.BleRepository
import com.penguino.repositories.BleRepositoryImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
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
    fun petsApi(
        moshi: Moshi
    ): Retrofit  {
        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl("http://192.168.50.153:8080/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}