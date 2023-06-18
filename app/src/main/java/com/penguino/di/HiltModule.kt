package com.penguino.di

import android.app.Activity
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.app.ActivityCompat
import androidx.core.app.ComponentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.room.Room
import com.google.gson.GsonBuilder
import com.penguino.data.network.ChatNetworkDataSource
import com.penguino.data.local.RegInfoCache
import com.penguino.data.local.RegInfoCacheImpl
import com.penguino.data.repositories.bluetooth.BleRepository
import com.penguino.data.repositories.bluetooth.BleRepositoryImpl
import com.penguino.data.repositories.registration.RegistrationRepository
import com.penguino.data.repositories.registration.RegistrationRepositoryImpl
import com.penguino.data.network.RegistrationNetworkDataSource
import com.penguino.data.local.DeviceDatabase
import com.penguino.data.local.dao.DeviceDao
import com.penguino.data.repositories.PermissionsRepository
import com.penguino.data.repositories.bluetooth.DeviceDiscoveryRepository
import com.penguino.data.repositories.bluetooth.DeviceDiscoveryRepositoryImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
    fun deviceApi(retrofit: Retrofit): RegistrationNetworkDataSource {
        return retrofit.create(RegistrationNetworkDataSource::class.java)
    }

    @Provides
    @Singleton
    fun deviceRepository(
        deviceApi: RegistrationNetworkDataSource,
        deviceDao: DeviceDao
    ): RegistrationRepository {
        return RegistrationRepositoryImpl(
            registrationApi = deviceApi,
            registrationDao = deviceDao
        )
    }

    @Provides
    @Singleton
    fun openAiApi(): ChatNetworkDataSource {
        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl("https://api.openai.com/v1/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ChatNetworkDataSource::class.java)
    }

    @Provides
    @Singleton
    fun permissionRepo(@ApplicationContext ctx: Context, @ActivityContext activityContext: Activity): PermissionsRepository {
        return PermissionsRepository(ctx, activityContext)
    }

    @Provides
    @Singleton
    fun deviceDiscovery(btAdapter: BluetoothAdapter): DeviceDiscoveryRepository {
        return DeviceDiscoveryRepositoryImpl(btAdapter)
    }
}

































