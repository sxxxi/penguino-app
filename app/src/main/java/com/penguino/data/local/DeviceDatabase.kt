package com.penguino.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.penguino.data.local.models.RegistrationInfo
import com.penguino.data.local.dao.DeviceDao

@Database(entities = [RegistrationInfo::class], version = 1)
abstract class DeviceDatabase: RoomDatabase() {
	abstract fun dao(): DeviceDao
}